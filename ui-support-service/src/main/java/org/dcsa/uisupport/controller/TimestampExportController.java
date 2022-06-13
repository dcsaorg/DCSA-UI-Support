package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.dcsa.jit.transferobjects.*;
import org.dcsa.jit.transferobjects.enums.DCSAResponsibleAgencyCode;
import org.dcsa.jit.transferobjects.enums.FacilityTypeCode;
import org.dcsa.uisupport.service.TimestampExportService;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampExportTO;
import org.dcsa.uisupport.transferobjects.util.DataExportDefinition;
import org.dcsa.uisupport.transferobjects.util.ExcelGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.*;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "unofficial/export-timestamps")
@RequiredArgsConstructor
public class TimestampExportController {

    private final ExcelGenerator excelGenerator;
    private final TimestampExportService timestampExportService;

    private final DataExportDefinition<TimestampExportTO> dataExportDefinition = DataExportDefinition.<TimestampExportTO>builder()
            .column("Publisher SMDG code", TimestampExportTO::operationsEvent, oe -> mapPublisher(oe, p ->
                Objects.requireNonNullElseGet(p.identifyingCodes(), Collections::<IdentifyingCodeTO>emptyList).stream()
                        .filter(idc -> idc.DCSAResponsibleAgencyCode() == DCSAResponsibleAgencyCode.SMDG)
                        .map(IdentifyingCodeTO::partyCode)
                        .collect(Collectors.joining(","))))
            .column("Publisher Role", TimestampExportTO::operationsEvent, OperationsEventTO::publisherRole)
            .column("Publisher Name", TimestampExportTO::operationsEvent, OperationsEventTO::publisher, PartyTO::name)
            .column("Vessel Name", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::vessel, VesselTO::name)
            .column("Vessel IMO", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::vessel, v -> Integer.parseInt(v.imoNumber()))
            .column("Vessel location lat", TimestampExportTO::operationsEvent, OperationsEventTO::vesselPosition, LocationTO::latitude)
            .column("Vessel location long", TimestampExportTO::operationsEvent, OperationsEventTO::vesselPosition, LocationTO::longitude)
            .column("Carrier Import Voyage Number", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::importVoyageNumber)
            .column("Carrier Export Voyage Number", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::exportVoyageNumber)
            .column("Terminal Code", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::location, LocationTO::facilityCode)
            .column("Facility type code", TimestampExportTO::operationsEvent, OperationsEventTO::facilityTypeCode)
            .column("Port Call Service type code", TimestampExportTO::operationsEvent, (oe) -> mapOrDefault(oe, OperationsEventTO::portCallServiceTypeCode, "<null>"))
            .column("Event Classifier code", TimestampExportTO::operationsEvent, OperationsEventTO::eventClassifierCode)
            /* Only for *-PBP related timestamps*/
            .column("PBP Location", TimestampExportTO::operationsEvent, oe -> isPBPTimestamp(oe)
                    ? mapEventLocation(oe, LocationTO::locationName)
                    : "N/A (wrong timestamp type)")
            /* Only for *-Berth related timestamps*/
            .column("Berth Location", TimestampExportTO::operationsEvent, oe -> isBerthRelatedTimestamp(oe)
                    ? mapEventLocation(oe, LocationTO::locationName)
                    : "N/A (wrong timestamp type)")
            .column("Event Message", TimestampExportTO::timestampDefinition, TimestampDefinitionTO::timestampTypeName)
            .column("Event Timestamp (port local TZ)", timestampExport -> asLocalDateTime(timestampExport.operationsEvent().dateTime(),  timestampExport.timezone()))
            .column("Event created date time (port local TZ)", timestampExport -> asLocalDateTime(timestampExport.operationsEvent().createdDateTime(), timestampExport.timezone()))
            .column("Port (UN Location Code)", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::location, LocationTO::unLocationCode)
            .column("Port Timezone", TimestampExportTO::timezone)
            .column("Delay Reason Code", TimestampExportTO::operationsEvent, OperationsEventTO::delayReasonCode)
            .column("Negotiation Sequence ID", TimestampExportTO::negotiationSequenceID)
            .column("Remark", TimestampExportTO::operationsEvent, OperationsEventTO::remark)
            .pivotChart((dataExportDefinition, pivotTable) -> {
                pivotTable.addRowLabel(dataExportDefinition.getColumnIndexOf("Vessel Name"));
                pivotTable.addRowLabel(dataExportDefinition.getColumnIndexOf("Negotiation Sequence ID"));
                pivotTable.addReportFilter(dataExportDefinition.getColumnIndexOf("Port (UN Location Code)"));
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

    private boolean isBerthRelatedTimestamp(OperationsEventTO oe) {
        return oe.facilityTypeCode() == FacilityTypeCode.BRTH;
    }

    private LocalDateTime asLocalDateTime(OffsetDateTime dateTime, ZoneId timeZone) {
        Instant instant = dateTime.toInstant();
        ZoneOffset offset = timeZone.getRules().getOffset(instant);
        return instant.atOffset(offset).toLocalDateTime();
    }

    private boolean isPBPTimestamp(OperationsEventTO oe) {
      return oe.facilityTypeCode() == FacilityTypeCode.PBPL;
    }

    private <T> Object mapOrDefault(T t, Function<T, Object> mapper, Object defaultValue) {
        Object res = mapper.apply(t);
        if (res == null) {
            return defaultValue;
        }
        return res;
    }

    private <T> T mapEventLocation(OperationsEventTO oe, Function<LocationTO, T> mapper) {
        LocationTO location = oe.eventLocation();
        if (location != null) {
            return mapper.apply(location);
        }
        return null;
    }

    private <T> T mapPublisher(OperationsEventTO oe, Function<PartyTO, T> mapper) {
        PartyTO publisher = oe.publisher();
        if (publisher != null) {
            return mapper.apply(publisher);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<byte[]> findAll() {
      return excelGenerator.generateExcel(
        "timestamps",
        dataExportDefinition,
        timestampExportService.getExportableTimestamps()
      );
    }
}
