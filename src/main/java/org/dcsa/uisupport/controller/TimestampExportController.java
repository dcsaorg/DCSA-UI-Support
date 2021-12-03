package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.enums.DCSAResponsibleAgencyCode;
import org.dcsa.core.events.model.enums.FacilityTypeCode;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.service.TimestampDefinitionService;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.uisupport.model.DataExportDefinition;
import org.dcsa.uisupport.model.ExcelGenerator;
import org.dcsa.uisupport.model.PortWithTimezone;
import org.dcsa.uisupport.model.UITimestampInfo;
import org.dcsa.uisupport.service.PortWithTimezoneService;
import org.dcsa.uisupport.service.UISupportEventService;
import org.dcsa.uisupport.service.UITimestampInfoService;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;

import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "unofficial/export-timestamps")
@RequiredArgsConstructor
public class TimestampExportController {

    private final static ZoneOffset UTC = ZoneOffset.UTC;

    private final ExcelGenerator excelGenerator;
    private final TimestampDefinitionService timestampDefinitionService;
    private final PortWithTimezoneService portWithTimezoneService;
    private final UISupportEventService uiSupportEventService;
    private final UITimestampInfoService uiTimestampInfoService;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

    private final static Set<Class<? extends Event>> OPERATIONS_EVENT_TYPE = Set.of(OperationsEvent.class);
    private final DataExportDefinition<Tuple3<OperationsEvent, TimestampDefinition, ZoneId>> dataExportDefinition = DataExportDefinition.<Tuple3<OperationsEvent, TimestampDefinition, ZoneId>>builder()
            .column("Publisher SMDG code", Tuple2::getT1, oe -> mapPublisher(oe, p ->
                Objects.requireNonNullElseGet(p.getIdentifyingCodes(), Collections::<PartyTO.IdentifyingCode>emptyList).stream()
                        .filter(idc -> idc.getDCSAResponsibleAgencyCode() == DCSAResponsibleAgencyCode.SMDG)
                        .map(PartyTO.IdentifyingCode::getPartyCode)
                        .collect(Collectors.joining(","))))
            .column("Publisher Role", Tuple3::getT1, OperationsEvent::getPublisherRole)
            .column("Publisher Name", Tuple3::getT1, OperationsEvent::getPublisher, PartyTO::getPartyName)
            .column("Vessel Name", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getVessel, Vessel::getVesselName)
            .column("Vessel IMO", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getVessel, v -> Integer.parseInt(v.getVesselIMONumber()))
            .column("Vessel location lat", Tuple3::getT1, OperationsEvent::getVesselPosition, LocationTO::getLatitude)
            .column("Vessel location long", Tuple3::getT1, OperationsEvent::getVesselPosition, LocationTO::getLongitude)
            //.column("Rotation From", oe -> "N/A (data not available)")
            //.column("Rotation To", oe -> "N/A (data not available)")
            //.column("Direction", oe -> "N/A (data not available)")
            .column("Carrier Import Voyage Number", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getImportVoyageNumber)
            .column("Carrier Export Voyage Number", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getExportVoyageNumber)
            .column("Transport Mode", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getModeOfTransport)
            .column("Facility Location", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getUNLocationCode)
            .column("Terminal Code", Tuple3::getT1, OperationsEvent::getTransportCall, TransportCallTO::getFacilityCode)
            .column("Facility type code", Tuple3::getT1, OperationsEvent::getFacilityTypeCode)
            .column("Port Call Service type code", Tuple3::getT1, (oe) -> mapOrDefault(oe, OperationsEvent::getPortCallServiceTypeCode, "<null>"))
            .column("Event Classifier code", Tuple3::getT1, OperationsEvent::getEventClassifierCode)
            /* Only for *-PBP related timestamps*/
            .column("PBP Location", Tuple3::getT1, oe -> isPBPTimestamp(oe)
                    ? mapEventLocation(oe, LocationTO::getLocationName)
                    : "N/A (wrong timestamp type)")
            /* Only for *-Berth related timestamps*/
            .column("Berth Location", Tuple3::getT1, oe -> isBerthRelatedTimestamp(oe)
                    ? mapEventLocation(oe, LocationTO::getLocationName)
                    : "N/A (wrong timestamp type)")
            .column("Event Message", Tuple3::getT2, TimestampDefinition::getTimestampTypeName)
            .column("Event Timestamp (port local TZ)", tuple -> asLocalDateTime(tuple.getT1().getEventDateTime(), tuple.getT3()))
            .column("Event created date time (port local TZ)", tuple -> asLocalDateTime(tuple.getT1().getEventCreatedDateTime(), tuple.getT3()))
            .column("Port (UN Location Code)", Tuple3::getT1, OperationsEvent::getTransportCall, this::getUnLocationCode)
            .column("Port Timezone", Tuple3::getT3)
            //.column("Response time", oe -> "N/A")
            //.column("Port call duration", oe -> "N/A")
            .column("Delay Reason Code", Tuple3::getT1, OperationsEvent::getDelayReasonCode)
            .column("Negotiation Sequence ID", TimestampExportController::computeNegotiationSequenceID)
            .column("Remark", Tuple3::getT1, OperationsEvent::getRemark)
            .pivotChart((dataExportDefinition, pivotTable) -> {
                pivotTable.addRowLabel(dataExportDefinition.getColumnIndexOf("Vessel Name"));
                pivotTable.addRowLabel(dataExportDefinition.getColumnIndexOf("Negotiation Sequence ID"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Facility Location"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Facility type code"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Port Call Service type code"));
                pivotTable.addColLabel(dataExportDefinition.getColumnIndexOf("Event Classifier code"));
                pivotTable.addColumnLabel(
                        DataConsolidateFunction.COUNT,
                        dataExportDefinition.getColumnIndexOf("Event Timestamp (port local TZ)"),
                        "Number of timestamps for a given negotiation cycle per Vessel"
                );
            })
            .build();

    private boolean isBerthRelatedTimestamp(OperationsEvent oe) {
        return oe.getFacilityTypeCode() == FacilityTypeCode.BRTH;
    }

    private String getUnLocationCode(TransportCallTO transportCallTO) {
        String unLocode = transportCallTO.getUNLocationCode();
        if (unLocode == null && transportCallTO.getLocation() != null) {
            unLocode = transportCallTO.getLocation().getUnLocationCode();
        }
        return unLocode;
    }

    private LocalDateTime asLocalDateTime(OffsetDateTime dateTime, ZoneId timeZone) {
        Instant instant = dateTime.toInstant();
        ZoneOffset offset = timeZone.getRules().getOffset(instant);
        return instant.atOffset(offset).toLocalDateTime();
    }

    private boolean isPBPTimestamp(OperationsEvent oe) {
        return oe.getFacilityTypeCode() == FacilityTypeCode.PBPL;
    }

    private <T> Object mapOrDefault(T t, Function<T, Object> mapper, Object defaultValue) {
        Object res = mapper.apply(t);
        if (res == null) {
            return defaultValue;
        }
        return res;
    }

    private Object mapEventLocation(OperationsEvent oe, Function<LocationTO, ?> mapper) {
        LocationTO location = oe.getEventLocation();
        if (location != null) {
            return mapper.apply(location);
        }
        return null;
    }

    private Object mapPublisher(OperationsEvent oe, Function<PartyTO, ?> mapper) {
        PartyTO publisher = oe.getPublisher();
        if (publisher != null) {
            return mapper.apply(publisher);
        }
        return null;
    }

    private static Object computeNegotiationSequenceID(Tuple2<OperationsEvent, TimestampDefinition> tuple) {
        OperationsEvent operationsEvent = tuple.getT1();
        TimestampDefinition timestampDefinition = tuple.getT2();
        StringBuilder builder = new StringBuilder();
        if (operationsEvent.getTransportCall() != null) {
            builder.append(operationsEvent.getTransportCall().getTransportCallID());
        } else {
            builder.append("NO_TC-").append(operationsEvent.getEventID());
        }
        return builder.append('-').append(timestampDefinition.getNegotiationCycle()).toString();
    }

    @GetMapping
    public Mono<ResponseEntity<byte[]>> findAll() {
        ExtendedGenericEventRequest genericEventRequest = new ExtendedGenericEventRequest(extendedParameters, r2dbcDialect, OPERATIONS_EVENT_TYPE);
        genericEventRequest.parseParameter(
                Map.of("eventType", List.of("OPERATIONS"),
                        "limit", List.of("1000000"),
                        "sort", List.of("eventCreatedDateTime:ASC")
                )
        );
        ExtendedRequest<PortWithTimezone> portWithTimezoneExtendedRequest = new ExtendedRequest<>(extendedParameters, r2dbcDialect, PortWithTimezone.class);
        portWithTimezoneExtendedRequest.parseParameter(Map.of("limit", List.of("1000000")));
        return excelGenerator.generateExcel(
          "timestamps",
                dataExportDefinition,
                uiSupportEventService.countAllExtended(genericEventRequest),
                timestampDefinitionService.findAll()
                                .collectMap(TimestampDefinition::getId, Function.identity())
                                .flatMap(timestampDefinitions ->
                                    Mono.zip(uiTimestampInfoService.findAll()
                                            .collectMap(UITimestampInfo::getEventID, info -> timestampDefinitions.get(info.getTimestampDefinition())),
                                            portWithTimezoneService.findAllExtended(portWithTimezoneExtendedRequest)
                                                    .collectMap(PortWithTimezone::getUnLocationCode,
                                                            portWithTimezone -> {
                                                                String timezone = portWithTimezone.getTimezone();
                                                                if (timezone == null) {
                                                                    return UTC;
                                                                }
                                                                try {
                                                                    return ZoneId.of(timezone);
                                                                } catch (DateTimeException e) {
                                                                    return UTC;
                                                                }
                                                            }
                                                    )
                                    )).flatMapMany(tuple -> {
                                        Map<UUID, TimestampDefinition> eventID2TimestampDefinitions = tuple.getT1();
                                        Map<String, ZoneId> unLocode2TZ = tuple.getT2();
                                        return uiSupportEventService.findAllExtended(genericEventRequest).cast(OperationsEvent.class)
                                            .concatMap(oe -> {
                                                TimestampDefinition timestampDefinition = eventID2TimestampDefinitions.get(oe.getEventID());
                                                TransportCallTO transportCallTO = oe.getTransportCall();
                                                String unLocode = transportCallTO.getUNLocationCode();
                                                if (unLocode == null && transportCallTO.getLocation() != null) {
                                                    unLocode = transportCallTO.getLocation().getUnLocationCode();
                                                }
                                                return Mono.zip(
                                                            Mono.just(oe),
                                                            Mono.justOrEmpty(timestampDefinition),
                                                            Mono.just(unLocode2TZ.getOrDefault(unLocode, UTC))
                                                        );
                                            });
                                })
                );
    }
}
