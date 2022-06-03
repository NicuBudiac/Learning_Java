package com.gilat.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Table class to work with tables
 */
public class Table {
    /**
     * Logger
     */
    protected final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * Returns maximum rows in table
     */
    public final int maxRows;

    /**
     * Returns maximum columns in table
     */
    public final int maxCols;

    /**
     * Key (String) == values from first row of Excel sheet (one value == one key). Value (Integer) == index of the column
     * (starting with 0. 0, 1, 2 and so on). Commented by Eduard Rodideal
     */
    private final HashMap<String, Integer> columnNameToIndex = new HashMap<>();

    /**
     * Key (Integer) ==  index of the column (starting with 0. 0, 1, 2 and so on).
     * Value (String) == values from first row of Excel sheet. Commented by Eduard Rodideal
     */
    private final HashMap<Integer, String> indexToColumnName = new HashMap<>();

    /**
     * The user chooses a value from a column of his choice (ex value = 2). This List contains all the rows that have in
     * the chosen column value == 2. Commented by Eduard Rodideal
     */
    private final List<RowMap<String, String>> listOfRowMaps;

    /**
     * This object represents one sheet from Excel file. table[0] will be first row of the sheet, table[1] second row, table[2] third row and so on.
     * table[0][0] == cell from first row and first column, table[0][1] == cell from first row and second column and so on. Commented by Eduard Rodideal
     */
    private final Object[][] table;

    /**
     * The value of this object == with the name of the sheet from where the info is fetched and kept in table object. Commented by Eduard Rodideal
     */
    private volatile String tableName = "Unnamed Table";

    /**
     * @param table two-dimensional array of type Object. Represents the table from one sheet in Excel file without
     *              knowing the name of the sheet. Commented by Eduard Rodideal
     */
    public Table(Object[][] table) {
        this.table = table;
        this.maxCols = getNumberOfCols();
        this.maxRows = getNumberOfRows();
        indexColumnNames();
        listOfRowMaps = getRowMapList();
    }

    /**
     * This constructor creates a table from an Excel sheet, and we know the name of the sheet. Commented by Eduard Rodideal
     */
    public Table(Object[][] table, String tableName) {
        this.table = table;
        this.tableName = tableName;
        this.maxCols = getNumberOfCols();
        this.maxRows = getNumberOfRows();
        indexColumnNames();
        listOfRowMaps = getRowMapList();
    }

    /**
     * This method instantiates two HashMap objects (columnNameToIndex and indexToColumnName). Keys in columnNameToIndex
     * == content of cells of first row from Excel sheet. Values of columnNameToIndex == nr of the column starting with zero.
     * ex. columnNameToIndex[Repository Name][3] where "Repository Name" is the value from the first row fourth column.
     * indexToColumnName is just the reverse of columnToIndex. ex indexToColumnName[3][Repository Name] where the key is the number of the column
     * and value is the value from first row and fourth column. Commented by Eduard Rodideal
     */
    private void indexColumnNames() {
        for (int i = 0; i < maxCols; i++) {
            columnNameToIndex.put(table[0][i].toString(), i);
            indexToColumnName.put(i, table[0][i].toString());
        }
    }

    /**
     * Getter method that returns the table object. Commented by Eduard Rodideal
     */
    public Object[][] getTable() {
        return table;
    }

    /**
     * Calculates maximum rows in table
     */
    private int getNumberOfRows() {
        for (int i = 0; i < table.length; i++) {
            if (table[i][0].toString().isEmpty())
                return i;
        }
        return this.table.length;
    }

    /**
     * Calculates maximum columns in table
     */
    private int getNumberOfCols() {
        if (table == null) {
            log.error("Table '" + tableName + "' is null");
            return -1;
        }
        for (int i = 0; i < table[0].length; i++) {
            if (table[0][i].toString().isEmpty())
                return i;
        }
        return this.table[0].length;
    }

    /**
     * @param rowIndex the index of the row we want to return
     * @return one row (row at index rowIndex) from Excel file as a RowMap<String, String>. Commented by Eduard Rodideal
     */
    public RowMap<String, String> getRow(int rowIndex) {
        RowMap<String, String> rowMap = new RowMap<>();
        if (this.columnNameToIndex.isEmpty()) {
            indexColumnNames();
        }

        if (rowIndex > table.length) {
            return rowMap;
        }

        rowMap = fillRowMap(rowIndex);
        return rowMap;
    }

    /**
     * @param colName column to filter
     * @param value   value to match
     * @return all the rows that have the value (value) in the column (colName). Commented by Eduard Rodideal
     */
    public List<RowMap<String, String>> getRows(String colName, String value) {
        List<RowMap<String, String>> rows = new ArrayList<>();
        if (this.columnNameToIndex.isEmpty()) {
            indexColumnNames();
        }

        if (!this.columnNameToIndex.containsKey(colName)) {
            return rows;
        }

        rows = listOfRowMaps.stream()
                .filter(rowMap -> rowMap.get(colName).equals(value))
                .collect(Collectors.toList());

        return rows;
    }

    /**
     * @param rowIndex the index of the row from where we want to fetch the information
     * @return RowMap with the data from the row at index rowIndex. Commented by Eduard Rodideal
     */
    private RowMap<String, String> fillRowMap(int rowIndex) {
        RowMap<String, String> dumpRowMap = new RowMap<>();
        AtomicInteger index = new AtomicInteger();
        Arrays.stream(table[rowIndex]).forEachOrdered(
                cellValue -> {
                    dumpRowMap.put(indexToColumnName.get(index.get()), (String) cellValue);
                    index.incrementAndGet();
                }
        );
        dumpRowMap.setTableRowIndex(rowIndex);
        return dumpRowMap;
    }

    /**
     * @return a List where at index 0 holds the info of row 0 from Excel file, at index 1 holds the info of row 1 and so on. Commented by Eduard Rodideal
     */
    private List<RowMap<String, String>> getRowMapList() {
        List<RowMap<String, String>> rows = new ArrayList<>();
        for (int r = 0; r < maxRows; r++) {
            RowMap<String, String> rowMap = fillRowMap(r);
            rows.add(rowMap);
        }
        return rows;
    }

    /**
     * Update values in a row of the table. Input HashMap<colName, cellValue>, should contain colName = "Index"
     * to find the rowIndex in the table.
     *
     * @param rowMap RowMap<colName, cellValue>
     */
    public void updateRow(RowMap<String, String> rowMap) {
        int rowIndex = rowMap.getTableRowIndex();
        for (String colName : rowMap.keySet()) {
            if (columnNameToIndex.containsKey(colName) && this.table[rowIndex][columnNameToIndex.get(colName)] !=
                    rowMap.get(colName)) {
                this.table[rowIndex][columnNameToIndex.get(colName)] = rowMap.get(colName);
            }
        }
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * From my observation we don't use this class anywhere. Commented by Eduard Rodideal
     */
    public static class Cell {
        private final int row, col;
        private final String val;

        public Cell(int row, int col, String val) {
            this.row = row;
            this.col = col;
            this.val = val;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public String getVal() {
            return val;
        }

        @Override
        public String toString() {
            String fs = "Row: %s\nCol: %s\nVal: %s";
            return String.format(fs, row, col, val);
        } //end method toString
    } //end class Cell
} //end class Table
