package com.gilat.excel.utils;


import com.consol.citrus.TestResult;
import com.gilat.excel.Excel;

import java.util.HashMap;

import static com.gilat.excel.utils.Result.*;


public abstract class InputData {
    private final IScenario scenario;

    public InputData(IScenario scenario) {
        this.scenario = scenario;
    }

    public void setTestResult(TestResult result) {
        if (result.isSuccess()) {
            scenario.setTestResult(PASS);
        } else if (result.isSkipped()) {
            scenario.setTestResult(SKIPPED);
            scenario.setTestReason(result.getCause().toString());
        } else if (result.isFailed()) {
            scenario.setTestResult(FAIL);
            scenario.setTestReason(result.getCause().toString());
        }
    }

    public void setColumnValue(HashMap<String, String> valuesToUpdate) {
        valuesToUpdate.forEach((key, value) -> {
            if (scenario.getRowMain().containsKey(key)) {
                scenario.setColumnValue(key, value);
            }
        });
    }

    public void write(String path, String sheetName) {
        Excel.writeRowToSheet(path, sheetName, scenario.getRowMain());
    }
}
