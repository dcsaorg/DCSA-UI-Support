package org.dcsa.uisupport.controller;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.dcsa.jit.transferobjects.*;
import org.dcsa.jit.transferobjects.enums.DCSAResponsibleAgencyCode;
import org.dcsa.jit.transferobjects.enums.FacilityTypeCodeOPR;
import org.dcsa.uisupport.service.TimestampExportService;
import org.dcsa.uisupport.transferobjects.TimestampDefinitionTO;
import org.dcsa.uisupport.transferobjects.TimestampExportTO;
import org.dcsa.uisupport.transferobjects.util.DataExportDefinition;
import org.dcsa.uisupport.transferobjects.util.ExcelGenerator;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "unofficial/export-timestamps")
@RequiredArgsConstructor
public class TimestampExportController {

  private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  private final ExcelGenerator excelGenerator;
  private final TimestampExportService timestampExportService;

  private final DataExportDefinition<TimestampExportTO> dataExportDefinition = DataExportDefinition.<TimestampExportTO>builder()
    .column("Publisher SMDG code", TimestampExportTO::operationsEvent, OperationsEventTO::publisher, p ->
      Objects.requireNonNullElseGet(p.identifyingCodes(), Collections::<IdentifyingCodeTO>emptyList).stream()
        .filter(idc -> idc.DCSAResponsibleAgencyCode() == DCSAResponsibleAgencyCode.SMDG)
        .map(IdentifyingCodeTO::partyCode)
        .collect(Collectors.joining(",")))
    .column("Publisher Role", TimestampExportTO::operationsEvent, OperationsEventTO::publisherRole)
    .column("Publisher Name", TimestampExportTO::operationsEvent, OperationsEventTO::publisher, PartyTO::partyName)
    .column("Vessel Name", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::vessel, TransportCallVesselTO::vesselName)
    .column("Vessel IMO", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::vessel, v -> Integer.parseInt(v.vesselIMONumber()))
    .column("Vessel location lat", TimestampExportTO::operationsEvent, OperationsEventTO::vesselPosition, LocationTO::latitude)
    .column("Vessel location long", TimestampExportTO::operationsEvent, OperationsEventTO::vesselPosition, LocationTO::longitude)
    .column("Carrier Import Voyage Number", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::importVoyageNumber)
    .column("Carrier Export Voyage Number", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::exportVoyageNumber)
    .column("Terminal Code", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::location, LocationTO::facilityCode)
    .column("Facility type code", TimestampExportTO::operationsEvent, OperationsEventTO::facilityTypeCode)
    .column("Port Call Service type code", TimestampExportTO::operationsEvent, oe -> mapOrDefault(oe, OperationsEventTO::portCallServiceTypeCode, "<null>"))
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
    .column("Event Timestamp (port local TZ)", timestampExport -> asLocalDateTime(timestampExport.operationsEvent().eventDateTime(),  timestampExport.timezone()))
    .column("Event created date time (port local TZ)", timestampExport -> asLocalDateTime(timestampExport.operationsEvent().eventCreatedDateTime(), timestampExport.timezone()))
    .column("Port (UN Location Code)", TimestampExportTO::operationsEvent, OperationsEventTO::transportCall, TransportCallTO::location, LocationTO::UNLocationCode)
    .column("Port Timezone", TimestampExportTO::timezone)
    .column("Delay Reason Code", TimestampExportTO::operationsEvent, OperationsEventTO::delayReasonCode)
    .column("Negotiation Sequence ID", TimestampExportTO::negotiationSequenceID)
    .column("Remark", TimestampExportTO::operationsEvent, OperationsEventTO::remark)
    .pivotChart((exportDefinition, pivotTable) -> {
      pivotTable.addRowLabel(exportDefinition.getColumnIndexOf("Vessel Name"));
      pivotTable.addRowLabel(exportDefinition.getColumnIndexOf("Negotiation Sequence ID"));
      pivotTable.addReportFilter(exportDefinition.getColumnIndexOf("Port (UN Location Code)"));
      pivotTable.addReportFilter(exportDefinition.getColumnIndexOf("Facility type code"));
      pivotTable.addReportFilter(exportDefinition.getColumnIndexOf("Port Call Service type code"));
      pivotTable.addColLabel(exportDefinition.getColumnIndexOf("Event Classifier code"));
      pivotTable.addColumnLabel(
        DataConsolidateFunction.COUNT,
        exportDefinition.getColumnIndexOf("Event Timestamp (port local TZ)"),
        "Number of timestamps for a given negotiation cycle per Vessel"
      );
    })
    .build();

  private boolean isBerthRelatedTimestamp(OperationsEventTO oe) {
    return oe.facilityTypeCode() == FacilityTypeCodeOPR.BRTH;
  }

  private LocalDateTime asLocalDateTime(OffsetDateTime dateTime, ZoneId timeZone) {
    Instant instant = dateTime.toInstant();
    ZoneOffset offset = timeZone.getRules().getOffset(instant);
    return instant.atOffset(offset).toLocalDateTime();
  }

  private boolean isPBPTimestamp(OperationsEventTO oe) {
    return oe.facilityTypeCode() == FacilityTypeCodeOPR.PBPL;
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

  @GetMapping("excel")
  public ResponseEntity<byte[]> exportAsExcel() throws IOException {
    List<TimestampExportTO> data = timestampExportService.getExportableTimestamps();
    try (Workbook workbook = excelGenerator.generateExcel(dataExportDefinition, data)) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      workbook.write(baos);
      HttpHeaders responseHeaders = new HttpHeaders();
      String name = "timestamps.xlsx";
      responseHeaders.setContentType(EXCEL_MEDIA_TYPE);
      responseHeaders.setContentDisposition(
        ContentDisposition.builder("attachment").filename(name).build()
      );
      return new ResponseEntity<>(baos.toByteArray(), responseHeaders, HttpStatus.OK);
    }
  }

  @GetMapping("json")
  public List<Map<String, Object>> exportAsJson() throws IOException {
    List<TimestampExportTO> data = timestampExportService.getExportableTimestamps();
    List<Map<String, Object>> jsonData = new ArrayList<>();
    try (Workbook workbook = excelGenerator.generateExcel(dataExportDefinition, data)) {
      Sheet dataSheet = workbook.getSheet("data");
      String[] headers = null;
      for (Row row : dataSheet) {

        if (headers == null) {
          headers = StreamSupport.stream(row.spliterator(), false)
            .map(Cell::getStringCellValue)
            .toArray(String[]::new);
          continue;
        }
        Cell[] cells = StreamSupport.stream(row.spliterator(), false)
          .toArray(Cell[]::new);
        if (headers.length < cells.length) {
          throw new IllegalStateException("Header row was shorter than data row");
        }
        Map<String, Object> rowDataJson = new HashMap<>();
        for (int i = 0 ; i < cells.length ; i++) {
          Object value = extractCellValue(cells[i], headers[i]);
          rowDataJson.put(headers[i], value);
        }
        jsonData.add(rowDataJson);
      }
      return jsonData;
    }
  }

  private static Object extractCellValue(Cell cell, String columnName) {
    return switch (cell.getCellType()) {
      case BLANK -> null;
      case BOOLEAN -> cell.getBooleanCellValue();
      // Numeric can also be dates, use name to clarify
      case NUMERIC -> (isDateTime(columnName) ? cell.getLocalDateTimeCellValue().toString() : asNumber(cell));
      case STRING -> cell.getStringCellValue();
      case ERROR -> cell.getErrorCellValue();
      default -> throw new UnsupportedOperationException("Missing case for cell type: " + cell.getCellType()
        + " - column: " + columnName);
    };
  }

  private static Number asNumber(Cell cell) {
    double number = cell.getNumericCellValue();
    // Ensure Vessel IMO and other integer like numbers are kept as integers (They are turned numeric for excel)
    if (Math.floor(number) == Math.ceil(number)) {
      return (long)number;
    }
    return number;
  }

  private static boolean isDateTime(String columnName) {
    String lowecased = columnName.toLowerCase();
    return lowecased.contains("timestamp") || lowecased.contains("date time");
  }
}
