package com.TestCase;

import com.enums.Columns;
import com.enums.ElemType;
import com.enums.SheetNames;
import com.gilat.citrus.behaviors.software_repository.SetToDefaultBehavior;
import com.gilat.citrus.behaviors.software_repository.UploadPackitBehavior;
import com.gilat.citrus.behaviors.software_repository.messages.SetToDefaultMessage;
import com.gilat.citrus.behaviors.software_repository.messages.UploadPackitMessage;
import com.gilat.citrus.behaviors.utils.CommonQueryValidator;
import com.gilat.citrus.grpc.client.GilatGrpcClient;
import com.gilat.excel.RowMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.List;

public class TestCaseUploadSwVersion extends AbstractTestCaseSoftwareUpdate{

    /**
     * Object used for logging purposes
     */
    private final Logger log = LoggerFactory.getLogger(TestCaseUploadSwVersion.class);
    private String IsDefault = rowFromCRUD.get(Columns.SETDEFAULT.getValue());
    private String repoIdInput = null;
    private String repoPath = null;


    /**
     * Constructor
     */

    public TestCaseUploadSwVersion(GilatGrpcClient gilatGrpcClient, HashMap<String, List<RowMap<String, String>>> rows) {
        super(gilatGrpcClient, rows, SheetNames.UPLOAD);
    }

    @Override
    public void setup() {

    }

    @Override
    public void execute() {
        CommonQueryValidator validator = new CommonQueryValidator().withTestResources(runner, context).withDataSource(builder.getDataSource());
        /** Initialize class FindLatest which will find path for last software release*/
        FindLatest findLatest = new FindLatest();
        /** If Hub Element Software Version is ends with latest will be uploaded last release version else ends upload explicit SW Version */
        String elemPath = ElemType.BASEPATH.getValue() + findLatest.getElementPath(rowFromCRUD.get(Columns.Element_Type.getValue()));
        /** Get Last Release version */
        String releaseVersion = findLatest.findFoldersInDirectory(elemPath);


        {
            if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(ElemType.LATEST_BY_RELEASE.getValue())){
                /** Get Software Version Name */
                String softwareFile = String.valueOf(findLatest.getLatestFileFromDIR(elemPath + "\\" + releaseVersion));
                repoPath = elemPath + releaseVersion + "\\" + softwareFile;
                repoIdInput = softwareFile;
            }
            else {
                repoIdInput = rowFromCRUD.get(Columns.Software_Version.getValue());
                releaseVersion = findLatest.getVersionPath(rowFromCRUD.get(Columns.Software_Version.getValue()),
                        rowFromCRUD.get(Columns.Element_Type.getValue()));
                repoPath = elemPath  + releaseVersion+  "\\" + repoIdInput;
            }

        }


        UploadPackitMessage uploadPackitMessage = new UploadPackitMessage(repoPath);
        if (log.isDebugEnabled()) System.out.println("JSON request: \n" + uploadPackitMessage);
        UploadPackitBehavior uploadPackitBehavior = new UploadPackitBehavior(gilatGrpcClient, uploadPackitMessage);
        runner.applyBehavior(uploadPackitBehavior);
        // Writing in Excel file how much time it took to upload SW Version */
        rowFromCRUD.put(Columns.TIME_TO_UPLOAD.getValue(), String.valueOf(gilatGrpcClient.getGrpcCallDuration().toMillis()));

        if (IsDefault.equals("Yes")){
            Long softRepDbId = validator.getSoftwareRepoDbId(repoIdInput);
            if (log.isDebugEnabled()) System.out.println("JSON request: \n" + softRepDbId);
            SetToDefaultMessage setToDefaultMessage = new SetToDefaultMessage(softRepDbId);
            SetToDefaultBehavior setToDefaultBehavior = new SetToDefaultBehavior(gilatGrpcClient, setToDefaultMessage);
            runner.applyBehavior(setToDefaultBehavior);
            setToDefaultBehavior.validateIsDefault(validator);
            rowFromCRUD.put(Columns.TIME_TO_DEFAULT.getValue(), String.valueOf(gilatGrpcClient.getGrpcCallDuration().toMillis()));
        }

    }








    @Override
    public void cleanup() {

    }
}
