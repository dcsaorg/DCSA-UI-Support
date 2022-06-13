package org.dcsa.uisupport.transferobjects.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ExcelGenerator {

  private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

  public <T> ResponseEntity<byte[]> generateExcel(String basename, DataExportDefinition<T> dataExportDefinition, List<T> rows) {
    try (ExcelGeneratorState<T> state = ExcelGeneratorState.of(dataExportDefinition, rows.size())) {
      state.processData(rows);
      state.dataFinished();
      byte[] data = state.toByteArray();
      HttpHeaders responseHeaders = new HttpHeaders();
      String name = basename + ".xlsx";
      responseHeaders.setContentType(EXCEL_MEDIA_TYPE);
      responseHeaders.setContentDisposition(
        ContentDisposition.builder("attachment").filename(name).build()
      );
      return new ResponseEntity<>(data, responseHeaders, HttpStatus.OK);
    }
  }

  @RequiredArgsConstructor(staticName = "of")
  private static class ExcelGeneratorState<T> implements AutoCloseable {
    private final DataExportDefinition<T> dataExportDefinition;

    private XSSFWorkbook workbook;
    private int rowNum = 0;
    private boolean initialized = false;
    private boolean finished = false;
    private XSSFSheet dataSheet;
    private CellStyle dataRowStyle;
    private CellStyle dateCellStyle;
    private XSSFPivotTable pivotTable;

    public static <T> ExcelGeneratorState<T> of(DataExportDefinition<T> dataExportDefinition, int pivotRowCount) {
      ExcelGeneratorState<T> generator = of(dataExportDefinition);
      generator.initialize(pivotRowCount);
      return generator;
    }
    public void initialize(int pivotRowCount) {
      workbook = new XSSFWorkbook();

      CreationHelper createHelper = workbook.getCreationHelper();
      Font defaultFont = workbook.createFont();
      defaultFont.setBold(false);
      Font boldfaceFont = workbook.createFont();
      boldfaceFont.setBold(true);
      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFont(boldfaceFont);

      dateCellStyle = workbook.createCellStyle();
      dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mmm-dd hh:mm"));

      if (rowNum > 0 || initialized) {
        throw new IllegalStateException("createHeaderRow must be called at most once and before any row is created");
      }

      dataSheet = workbook.createSheet("data");
      Row headerRow = dataSheet.createRow(rowNum++);
      dataExportDefinition.insertColumnNames(headerRow);
      headerRow.setRowStyle(headerStyle);

      if (dataExportDefinition.includePivotChart() && pivotRowCount > 0) {
        XSSFSheet pivotChartSheet = workbook.createSheet("pivot");
        CellReference topLeft = new CellReference("data",
          headerRow.getRowNum(),
          headerRow.getFirstCellNum(),
          false,
          false
        );
        CellReference bottomRight = new CellReference("data",
          pivotRowCount,
          headerRow.getLastCellNum() - 1,
          false,
          false
        );
        AreaReference areaReference = new AreaReference(topLeft, bottomRight, workbook.getSpreadsheetVersion());
        CellReference pivotTopLeft = new CellReference(0, 0);
        pivotTable = pivotChartSheet.createPivotTable(areaReference, pivotTopLeft);
      }

      dataRowStyle = workbook.createCellStyle();
      dataRowStyle.setFont(defaultFont);
      dataSheet.createFreezePane(0, 1);
      initialized = true;
    }


    public void processData(List<T> rows) {
      if (finished) {
        throw new IllegalStateException("Already finished!");
      }
      if (rowNum == 0 && !initialized) {
        initialize(-1);
      }
      for (T t : rows) {
        Row row = dataSheet.createRow(rowNum++);
        dataExportDefinition.writeEntityIntoRow(t, row, dateCellStyle);
        row.setRowStyle(dataRowStyle);
      }
    }

    private void resizeColumn() {
      final int size = dataExportDefinition.getColumnCount();
      for (int i = 0; i < size ; i++) {
        dataSheet.autoSizeColumn(i);
      }
    }

    public void dataFinished() {
      ensureDataIsFinished();
      resizeColumn();
      if (pivotTable != null && rowNum > 0) {
        dataExportDefinition.setupPivotChart(pivotTable);
      }
    }

    private void ensureDataIsFinished() {
      if (!finished) {
        finished = true;
        if (!initialized) {
          // Special-case: If there were no matching results (i.e. the List was empty),
          // then we still have to generate an Excel file.  However, we have to remember
          // to generate the header row (etc.)
          initialize(-1);
        }
        dataSheet.setAutoFilter(new CellRangeAddress(0, rowNum - 1, 0, dataExportDefinition.getColumnCount() - 1));
      }
    }

    @SneakyThrows(IOException.class)
    public byte[] toByteArray() {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();

      ensureDataIsFinished();
      workbook.write(bout);
      return bout.toByteArray();
    }

    @SneakyThrows(IOException.class)
    public void close() {
      if (workbook != null) {
        workbook.close();
      }
    }
  }
}
