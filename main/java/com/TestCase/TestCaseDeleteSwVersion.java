package com.TestCase;

import com.enums.Columns;
import com.enums.SheetNames;
import com.gilat.citrus.behaviors.software_repository.DeletePackitBehavior;
import com.gilat.citrus.behaviors.software_repository.messages.DeletePackitMessage;
import com.gilat.citrus.behaviors.utils.CommonQueryValidator;
import com.gilat.citrus.grpc.client.GilatGrpcClient;
import com.gilat.excel.RowMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

public class TestCaseDeleteSwVersion extends AbstractTestCaseSoftwareUpdate{
    /**
     * Object used for logging purposes
     */
    private final Logger log = LoggerFactory.getLogger(TestCaseDeleteSwVersion.class);


    /**
     * Constructor
     */

    public TestCaseDeleteSwVersion(GilatGrpcClient gilatGrpcClient, HashMap<String, List<RowMap<String, String>>> rows) {
        super(gilatGrpcClient, rows, SheetNames.DELETE);
    }

    @Override
    public void setup() {
    }

    @Override
    public void execute() {
        CommonQueryValidator validator = new CommonQueryValidator().withTestResources(runner, context).withDataSource(builder.getDataSource());
        Long softRepDbId = validator.getSoftwareRepoDbId(rowFromCRUD.get(Columns.Software_Version.getValue()));
        if (log.isDebugEnabled()) System.out.println("JSON request: \n" + softRepDbId);
        DeletePackitMessage deletePackitMessage = new DeletePackitMessage(softRepDbId);
        DeletePackitBehavior deletePackitBehavior = new DeletePackitBehavior(gilatGrpcClient, deletePackitMessage);
        runner.applyBehavior(deletePackitBehavior);
        deletePackitBehavior.validateAllPackitWasDeleted(validator);
        rowFromCRUD.put(Columns.TIME_TO_DELETE.getValue(), String.valueOf(gilatGrpcClient.getGrpcCallDuration().toMillis()));
    }

    @Override
    public void cleanup() {

    }
}
