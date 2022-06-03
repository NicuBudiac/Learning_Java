package com.gilat.excel;

import java.util.HashMap;
import java.util.Map;

public class RowMap<K, V> extends HashMap<K, V> {
    private int tableRowIndex;
    public RowMap() {
        super();
    }

    public RowMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public RowMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public RowMap(int initialCapacity) {
        super(initialCapacity);
    }

    public void setTableRowIndex(int tableRowIndex) {
        this.tableRowIndex = tableRowIndex;
    }

    public int getTableRowIndex() {
        return tableRowIndex;
    }
}
