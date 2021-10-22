package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.enums.CodeListResponsibleAgency;
import org.dcsa.core.events.model.enums.FacilityTypeCode;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.uisupport.model.DataExportDefinition;
import org.dcsa.uisupport.model.ExcelGenerator;
import org.dcsa.uisupport.service.UISupportEventService;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "unofficial/export-timestamps")
@RequiredArgsConstructor
public class TimestampExportController {

    private final ExcelGenerator excelGenerator;
    private final UISupportEventService uiSupportEventService;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;
    private final static Set<Class<? extends Event>> OPERATIONS_EVENT_TYPE = Set.of(OperationsEvent.class);
    private final DataExportDefinition<OperationsEvent> dataExportDefinition = DataExportDefinition.<OperationsEvent>builder()
            .column("Publisher SMDG code", oe -> mapPublisher(oe, p ->
                Objects.requireNonNullElseGet(p.getIdentifyingCodes(), Collections::<PartyTO.IdentifyingCode>emptyList).stream()
                        .filter(idc -> idc.getCodeListResponsibleAgencyCode().equals(CodeListResponsibleAgency.SMDG.getCode()))
                        .map(PartyTO.IdentifyingCode::getPartyCode)
                        .collect(Collectors.joining(","))))
            .column("Publisher Role", OperationsEvent::getPublisherRole)
            .column("Publisher Name", oe -> mapPublisher(oe, PartyTO::getPartyName))
            .column("Vessel Name", oe -> mapVessel(oe, Vessel::getVesselName))
            .column("Vessel IMO", oe -> mapVessel(oe, v -> Integer.parseInt(v.getVesselIMONumber())))
            .column("Vessel location lat", oe -> mapVesselLocation(oe, LocationTO::getLatitude))
            .column("Vessel location long", oe -> mapVesselLocation(oe, LocationTO::getLongitude))
            //.column("Rotation From", oe -> "N/A (data not available)")
            //.column("Rotation To", oe -> "N/A (data not available)")
            //.column("Direction", oe -> "N/A (data not available)")
            .column("Carrier Voyage Number", oe -> mapTransportCall(oe, TransportCallTO::getCarrierVoyageNumber))
            .column("Transport Mode", oe -> mapTransportCall(oe, TransportCallTO::getModeOfTransport))
            .column("Facility Location", oe -> mapTransportCall(oe, TransportCallTO::getUNLocationCode))
            .column("Terminal Code", oe -> mapTransportCall(oe, TransportCallTO::getFacilityCode))
            .column("Facility type code", OperationsEvent::getFacilityTypeCode)
            .column("Port Call Service type code", (oe) -> mapOrDefault(oe, OperationsEvent::getPortCallServiceTypeCode, "<null>"))
            .column("Event Classifier code", OperationsEvent::getEventClassifierCode)
            /* Only for *-PBP related timestamps*/
            .column("PBP Location", oe -> isPBPTimestamp(oe)
                    ? mapEventLocation(oe, LocationTO::getLocationName)
                    : "N/A (wrong timestamp type)")
            /* Only for *-Berth related timestamps*/
            .column("Berth Location", oe -> isBerthRelatedTimestamp(oe)
                    ? mapEventLocation(oe, LocationTO::getLocationName)
                    : "N/A (wrong timestamp type)")
            .column("Event Message", oe -> "TODO")// OperationsEvent::getTimestampTypeName)
            .column("Event Timestamp", OperationsEvent::getEventDateTime)
            .column("Event created date time", OperationsEvent::getEventCreatedDateTime)
            //.column("Response time", oe -> "N/A")
            //.column("Port call duration", oe -> "N/A")
            .column("Delay Reason Code", OperationsEvent::getDelayReasonCode)
            .column("Negotiation Sequence ID", TimestampExportController::computeNegotiationSequenceID)
            .column("Remark", OperationsEvent::getRemark)
            .pivotChart((dataExportDefinition, pivotTable) -> {
                pivotTable.addRowLabel(dataExportDefinition.getColumnIndexOf("Vessel Name"));
                pivotTable.addRowLabel(dataExportDefinition.getColumnIndexOf("Negotiation Sequence ID"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Facility Location"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Facility type code"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Port Call Service type code"));
                pivotTable.addColLabel(dataExportDefinition.getColumnIndexOf("Event Classifier code"));
                pivotTable.addColumnLabel(
                        DataConsolidateFunction.COUNT,
                        dataExportDefinition.getColumnIndexOf("Event Timestamp"),
                        "Number of timestamps for a given negotiation cycle per Vessel"
                );
            })
            .build();

    private boolean isBerthRelatedTimestamp(OperationsEvent oe) {
        return oe.getFacilityTypeCode() == FacilityTypeCode.BRTH;
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

    private Object mapTransportCall(OperationsEvent oe, Function<TransportCallTO, ?> mapper) {
        if (oe.getTransportCall() == null) {
            return null;
        }
        return mapper.apply(oe.getTransportCall());
    }

    private Object mapVessel(OperationsEvent oe, Function<Vessel, ?> mapper) {
        if (oe.getTransportCall() == null) {
            return null;
        }
        Vessel vessel = oe.getTransportCall().getVessel();
        if (vessel != null) {
            return mapper.apply(vessel);
        }
        return null;
    }

    private Object mapVesselLocation(OperationsEvent oe, Function<LocationTO, ?> mapper) {
        LocationTO vesselPosition = oe.getVesselPosition();
        if (vesselPosition != null) {
            return mapper.apply(vesselPosition);
        }
        return null;
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

    private static Object computeNegotiationSequenceID(OperationsEvent operationsEvent) {
        StringBuilder builder = new StringBuilder();
        builder.append(operationsEvent.getOperationsEventTypeCode()).append('-');
        if (operationsEvent.getPortCallServiceTypeCode() != null) {
            builder.append(operationsEvent.getPortCallServiceTypeCode());
        } else {
            builder.append(operationsEvent.getFacilityTypeCode());
        }
        if (operationsEvent.getTransportCall() != null) {
            return builder.append('-').append(operationsEvent.getTransportCall().getTransportCallID()).toString();
        }
        return builder.append("-NO_TC-").append(operationsEvent.getEventID());
    }

    @GetMapping
    public Mono<ResponseEntity<byte[]>> findAll() {
        ExtendedGenericEventRequest genericEventRequest = new ExtendedGenericEventRequest(extendedParameters, r2dbcDialect, OPERATIONS_EVENT_TYPE);
        genericEventRequest.parseParameter(
                Map.of("eventType", List.of("OPERATIONS"),
                        "limit", List.of("1000000"))
        );
        return excelGenerator.generateExcel(
          "timestamps",
                dataExportDefinition,
                uiSupportEventService.countAllExtended(genericEventRequest),
                uiSupportEventService.findAllExtended(genericEventRequest).cast(OperationsEvent.class)
        );
    }
}
