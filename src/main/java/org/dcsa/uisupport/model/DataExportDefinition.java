package org.dcsa.uisupport.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class DataExportDefinition<T> {

    private final Map<String, ColumnDefinition<T>> columnDefinitions;
    private final BiConsumer<DataExportDefinition<T>, XSSFPivotTable> pivotTableGenerator;

    public int getColumnCount() {
        return columnDefinitions.size();
    }

    public boolean includePivotChart() {
        return pivotTableGenerator != null;
    }

    public void setupPivotChart(XSSFPivotTable pivotTable) {
        if (pivotTableGenerator == null) {
            throw new IllegalStateException("setupPivotChart MUST NOT be called with includePivotChart() returns false");
        }
        pivotTableGenerator.accept(this, pivotTable);
    }

    public int getColumnIndexOf(String column) {
        ColumnDefinition<T> columnDefinition = columnDefinitions.get(column);
        if (columnDefinition == null) {
            throw new IllegalStateException("Unknown Column " + column);
        }
        return columnDefinition.getColumnIndex();
    }

    public void insertColumnNames(Row headerRow) {
        int i = 0;
        for (String columnName : columnDefinitions.keySet()) {
            int columnIndex = i++;
            Cell cell = headerRow.createCell(columnIndex);
            cell.setCellValue(columnName);
        }
    }

    public void writeEntityIntoRow(T entity, Row valueRow) {
        int i = 0;
        for (ColumnDefinition<T> columnDefinition : columnDefinitions.values()) {
            Object value = columnDefinition.valueExtractor.apply(entity);
            Cell cell = valueRow.createCell(i++);
            if (value == null) {
                cell.setBlank();
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Number) {
                cell.setCellValue(((Number)value).doubleValue());
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <T> DataExportDefinition<T> of(LinkedHashMap<String, Function<T, Object>> mapperFunctions) {
        return of(mapperFunctions, null);
    }

    public static <T> DataExportDefinition<T> of(LinkedHashMap<String, Function<T, Object>> mapperFunctions, BiConsumer<DataExportDefinition<T>, XSSFPivotTable> pivotTableGenerator) {
        LinkedHashMap<String, ColumnDefinition<T>> columnDefinitions = new LinkedHashMap<>();
        int columnIndex = 0;
        for (Map.Entry<String, Function<T, Object>> entry : mapperFunctions.entrySet()) {
            String key = entry.getKey();
            ColumnDefinition<T> columnDefinition = ColumnDefinition.of(columnIndex++, entry.getValue());
            if (columnDefinitions.putIfAbsent(key, columnDefinition) != null) {
                throw new IllegalArgumentException("Key " + key + " is not unique");
            }
        }
        return new DataExportDefinition<>(
                Collections.unmodifiableMap(columnDefinitions),
                pivotTableGenerator
        );
    }

    @Data(staticConstructor = "of")
    private static class ColumnDefinition<T> {
        final int columnIndex;
        final Function<T, Object> valueExtractor;
    }

    public static class Builder<T> {
        private final LinkedHashMap<String, ColumnDefinition<T>> columnDefinitions = new LinkedHashMap<>();
        private BiConsumer<DataExportDefinition<T>, XSSFPivotTable> pivotTableGenerator;

        private Builder() {}

        public Builder<T> column(String columnName, Function<T, Object> valueExtractor) {
            ColumnDefinition<T> columnDefinition = ColumnDefinition.of(columnDefinitions.size(), valueExtractor);
            if (columnDefinitions.putIfAbsent(columnName, columnDefinition) != null) {
                throw new IllegalArgumentException("Column " + columnName + " is not unique");
            }
            return this;
        }

        public Builder<T> pivotChart(BiConsumer<DataExportDefinition<T>, XSSFPivotTable> pivotTableGenerator) {
            this.pivotTableGenerator = pivotTableGenerator;
            return this;
        }

        public DataExportDefinition<T> build() {
            if (columnDefinitions.isEmpty()) {
                throw new IllegalStateException("No columns to export");
            }
            return new DataExportDefinition<>(
                    Collections.unmodifiableMap(new LinkedHashMap<>(columnDefinitions)),
                    pivotTableGenerator
            );
        }
    }

}
