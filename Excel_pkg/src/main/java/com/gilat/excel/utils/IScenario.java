package com.gilat.excel.utils;

import com.gilat.excel.RowMap;

public interface IScenario {
    int getIndex();

    void setTestReason(String cause);

    void setTestResult(Result result);

    void setColumnValue(String column, String value);

    RowMap<String, String> getRowMain();
}
