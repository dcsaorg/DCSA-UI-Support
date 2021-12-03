package org.dcsa.uisupport.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;

import java.time.LocalDateTime;
import java.util.*;
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

    public void writeEntityIntoRow(T entity, Row valueRow, CellStyle dateCellStyle) {
        int i = 0;
        for (ColumnDefinition<T> columnDefinition : columnDefinitions.values()) {
            Object value = columnDefinition.valueExtractor.apply(entity);
            Cell cell = valueRow.createCell(i++);
            if (value == null) {
                cell.setBlank();
            } else if (value instanceof Boolean) {
                cell.setCellValue((Boolean) value);
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof LocalDateTime) {
                cell.setCellValue((LocalDateTime) value);
                cell.setCellStyle(dateCellStyle);
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

        public <I> Builder<T> column(String columnName, Function<T, I> mapping, Function<I, Object> valueExtractor) {
            return column(columnName, combine(mapping, valueExtractor));
        }

        public <I1, I2> Builder<T> column(String columnName, Function<T, I1> mappingA, Function<I1, I2> mappingB, Function<I2, Object> valueExtractor) {
            return column(columnName, combine(mappingA, mappingB, valueExtractor));
        }

        public <I1, I2, I3> Builder<T> column(String columnName, Function<T, I1> mappingA, Function<I1, I2> mappingB, Function<I2, I3> mappingC, Function<I3, Object> valueExtractor) {
            return column(columnName, combine(mappingA, mappingB, mappingC, valueExtractor));
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

    private static <A, B, C> Function<A, C> combine(Function<A, B> first, Function<B, C> second) {
        return (a -> {
            B b = first.apply(a);
            return b != null ? second.apply(b) : null;
        });
    }

    private static <A, B, C, D> Function<A, D> combine(Function<A, B> first, Function<B, C> second, Function<C, D> third) {
        return combine(combine(first, second), third);
    }

    private static <A, B, C, D, E> Function<A, E> combine(Function<A, B> first, Function<B, C> second, Function<C, D> third, Function<D, E> fourth) {
        return combine(combine(first, second, third), fourth);
    }

}
