package com.TestCase;

import com.consol.citrus.context.TestContext;
import com.consol.citrus.dsl.runner.TestRunner;
import com.gilat.automation.common.ITestCase;
import com.gilat.citrus.behaviors.utils.QueriesValidatorBuilder;
import com.gilat.citrus.grpc.client.GilatGrpcClient;
import com.gilat.excel.Excel;
import com.gilat.excel.RowMap;
import com.gilat.excel.Table;
import com.enums.SheetNames;
import com.enums.Columns;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import java.io.File;
import java.util.*;


public abstract class AbstractTestCaseSoftwareUpdate implements ITestCase {

    /**
     * An object representing a client that can communicate with a server. It knows address of the host, port number and many more information that a client needs to have
     */
    protected final GilatGrpcClient gilatGrpcClient;
    /**
     * Test builder interface defines builder pattern methods for creating a new Citrus test case. This object is a wrapper that contains all the information for one test case.
     */
    protected static TestRunner runner;

    /**
     * Class holding and managing test variables
     */
    protected static TestContext context;

    /**
     * It's a wrapper class for TestRunner, TestContext and DataSource. In other words we have TestRunner, TestContext and DataSource in one object and can access these objects
     * by using getter methods getRunner, getContext and getDataSource
     */
    protected static QueriesValidatorBuilder builder = new QueriesValidatorBuilder();


    protected final HashMap<String, List<RowMap<String, String>>> rowsDifferentSheetsSameIndex;

    /**
     * Contains information from one row of the Excel file
     * key = column name; value = cell value at the intersection of the column with current row.
     */
    protected final RowMap<String, String> rowFromCRUD;

    /**
     * Constructor
     */
    public AbstractTestCaseSoftwareUpdate(GilatGrpcClient grpcClient, HashMap<String, List<RowMap<String, String>>> currentExcelRows, SheetNames sheetName) {
        this.rowsDifferentSheetsSameIndex = new RowMap<>(currentExcelRows);
        this.gilatGrpcClient = grpcClient;
        rowFromCRUD = currentExcelRows.get(sheetName.name()).get(0);
    }

    /**
     * Sets runner variable and context variable if context variable is not null
     */
    @Override
    public void setCitrusRunner(TestRunner testRunner) {
        runner = testRunner;
        if (context != null)
            builder.withCitrusResources(runner, context);
    }

    /**
     * sets context variable and runner variable if runner variable is not null
     */
    @Override
    public void setCitrusContext(TestContext testContext) {
        context = testContext;
        if (runner != null)
            builder.withCitrusResources(runner, context);
    }


    /**
     * Setter method for QueriesValidatorBuilder and using this instantiates runner object and context object
     */
    public void setBuilder(QueriesValidatorBuilder builder) {
        MatcherAssert.assertThat("Builder is null", builder, Matchers.notNullValue());
        this.builder = builder;
        this.runner = builder.getRunner();
        this.context = builder.getContext();
    }// end method

    /**
     * Getter method for getting mainRow
     */
    public RowMap<String, String> getRowFromCRUD() {
        return rowFromCRUD;
    }
}
