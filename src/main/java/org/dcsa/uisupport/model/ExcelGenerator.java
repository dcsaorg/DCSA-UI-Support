package org.dcsa.uisupport.model;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Component
public class ExcelGenerator {

    private static final int BUFFER_ROW = 100;

    private static final MediaType EXCEL_MEDIA_TYPE = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    public <T> Mono<ResponseEntity<byte[]>> generateExcel(String basename, DataExportDefinition<T> dataExportDefinition, Mono<Integer> rowCount, Flux<T> dataFlux) {
        ExcelGeneratorState<T> state = ExcelGeneratorState.of(dataExportDefinition);
        return rowCount.doOnNext(state::initialize)
                .thenMany(dataFlux.buffer(BUFFER_ROW))
                .doOnNext(state::processData)
                // Abuse count to collapse the flux into a non-empty mono,
                // so we can use doOnNext/map from there.
                .count()
                .doOnNext(ignored -> state.dataFinished())
                .map(ignored -> {
                    byte[] data = state.toByteArray();
                    HttpHeaders responseHeaders = new HttpHeaders();
                    String name = basename + ".xlsx";
                    responseHeaders.setContentType(EXCEL_MEDIA_TYPE);
                    responseHeaders.setContentDisposition(
                            ContentDisposition.builder("attachment").filename(name).build()
                    );
                    return new ResponseEntity<>(data, responseHeaders, HttpStatus.OK);
                }).doFinally(ignored -> state.close());
    }

    @RequiredArgsConstructor(staticName = "of")
    private static class ExcelGeneratorState<T> {
        private final DataExportDefinition<T> dataExportDefinition;

        private Workbook workbook;
        private int rowNum = 0;
        private boolean initialized = false;
        private boolean finished = false;
        private Sheet dataSheet;
        private CellStyle dataRowStyle;
        private CellStyle dateCellStyle;
        private XSSFPivotTable pivotTable;

        public void initialize(int pivotRowCount) {
            if (dataExportDefinition.includePivotChart() && pivotRowCount > 0) {
                workbook = new XSSFWorkbook();
            } else {
                // We need a few extra rows in the access window because we also operate on the header row along
                // with the first batch.
                workbook = new SXSSFWorkbook(null, BUFFER_ROW + 5, false);
            }

            CreationHelper createHelper = workbook.getCreationHelper();
            Font defaultFont = workbook.createFont();
            defaultFont.setBold(false);
            Font boldfaceFont = workbook.createFont();
            boldfaceFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(boldfaceFont);

            dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-mmm-dd hh:mm"));

            if (rowNum > 0) {
                throw new IllegalStateException("createHeaderRow must be called at most once and before any row is created");
            }

            dataSheet = workbook.createSheet("data");
            Row headerRow = dataSheet.createRow(rowNum++);
            dataExportDefinition.insertColumnNames(headerRow);
            headerRow.setRowStyle(headerStyle);

            if (dataExportDefinition.includePivotChart() && pivotRowCount > 0) {
                assert workbook instanceof XSSFWorkbook;
                XSSFSheet pivotChartSheet = (XSSFSheet)workbook.createSheet("pivot");
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
            if (dataSheet instanceof SXSSFSheet) {
                ((SXSSFSheet)dataSheet).trackAllColumnsForAutoSizing();
            }
            initialized = true;
        }


        public void processData(List<T> rows) {
            boolean first = rowNum == 0;
            if (finished) {
                throw new IllegalStateException("Already finished!");
            }
            if (first && !initialized) {
                initialize(-1);
            }
            for (T t : rows) {
                Row row = dataSheet.createRow(rowNum++);
                dataExportDefinition.writeEntityIntoRow(t, row, dateCellStyle);
                row.setRowStyle(dataRowStyle);
            }
            if (first && dataSheet instanceof SXSSFSheet) {
                // For SXSSFSheet's, we do auto-resizing after the first row batch to keep the advantage of streaming
                SXSSFSheet sxssfSheet = (SXSSFSheet)dataSheet;
                // The auto-sizing of column is expensive because it involves going over all values in the sheet.
                // Since we can generate very large sheets, we are interested in a "cheap" solution.  We cheat and
                // use the first batch of rows to "guessimate" the size of the column.  This enables us to use POI's
                // built-in resizing feature while avoiding to go through the sheet again.
                resizeColumn();
                sxssfSheet.untrackAllColumnsForAutoSizing();
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
            if (dataSheet instanceof XSSFSheet) {
                // For XSSFSheet's, we can do the resize of all columns at the end
                resizeColumn();
            }
            if (pivotTable != null && rowNum > 0) {
                dataExportDefinition.setupPivotChart(pivotTable);
            }
        }

        private void ensureDataIsFinished() {
            if (!finished) {
                finished = true;
                if (!initialized) {
                    // Special-case: If there were no matching results (i.e. the Flux was empty),
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
                if (workbook instanceof SXSSFWorkbook) {
                    ((SXSSFWorkbook)workbook).dispose();
                }
            }
        }
    }
}
