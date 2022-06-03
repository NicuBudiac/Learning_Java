package com.gilat.excel.utils;

import com.gilat.excel.RowMap;

import java.util.HashMap;

public class ExcelOidValuesMaps {
    /**
     * Purpose of this class is to provide an interface to work with excel inputs that contains
     * oid as column name
     */
    private final HashMap<String, String> oidToValuesMap;
    private final HashMap<String, String> oidToColumnsMap;
    private final HashMap<String, String> columnsToValuesMap;
    private final RowMap<String, String> originalOidToValuesMap;
    private final HashMap<String, String> columnsToOidsMap;

    public ExcelOidValuesMaps(RowMap<String, String> oidToValuesMap, RowMap<String, String> oidToColumnNamesMap) {
        this.originalOidToValuesMap = oidToValuesMap;
        this.oidToValuesMap = parseMapOid(oidToValuesMap);
        this.oidToColumnsMap = parseMapOid(oidToColumnNamesMap);
        this.columnsToValuesMap = buildColumnsToValuesMap(this.oidToValuesMap, this.oidToColumnsMap);
        this.columnsToOidsMap = buildColumnsToOidsMap(this.oidToColumnsMap);
    }

    private HashMap<String, String> buildColumnsToValuesMap(HashMap<String, String> oidValuesMap,
                                                            HashMap<String, String> oidColumnNamesMap) {
        HashMap<String, String> mapColumnValues = new HashMap<>();
        oidColumnNamesMap.forEach((oid, columnName) -> mapColumnValues.put(columnName, oidValuesMap.get(oid)));
        return mapColumnValues;
    }

    private HashMap<String, String> buildColumnsToOidsMap(HashMap<String, String> oidColumnNames) {
        HashMap<String, String> mapColumnOids = new HashMap<>();

        oidColumnNames.forEach((oid, columnName) -> mapColumnOids.put(columnName, oid));
        return mapColumnOids;
    }

    private HashMap<String, String> parseMapOid(HashMap<String, String> oidMap) {
        HashMap<String, String> dumpMap = new HashMap<>();
        oidMap.forEach((oid, value) -> {
            if (isOid(oid)) {
                dumpMap.put(oid, value);
            }
        });
        return dumpMap;
    }

    public static boolean isOid(String value) {
        return value.matches("^(\\d+\\.)+\\d+$");
    }

    public String getValueByColumnName(String columnName) {
        return this.columnsToValuesMap.getOrDefault(columnName, null);
    }

    public String getValueByOid(String oid) {
        return this.oidToValuesMap.getOrDefault(oid, null);
    }

    public String getOidByColumnName(String columnName) {
        return this.columnsToOidsMap.getOrDefault(columnName, null);
    }

    public HashMap<String, String> getOidToValuesMap() {
        return oidToValuesMap;
    }

    public HashMap<String, String> getOidToColumnsMap() {
        return oidToColumnsMap;
    }

    public HashMap<String, String> getColumnsToValuesMap() {
        return this.columnsToValuesMap;
    }

    public HashMap<String, String> getColumnsToOidsMap() {
        return this.columnsToOidsMap;
    }

    public RowMap<String, String> getOriginalOidToValuesMap() {
        return originalOidToValuesMap;
    }
}
