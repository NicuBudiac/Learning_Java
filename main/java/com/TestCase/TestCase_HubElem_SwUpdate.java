package com.TestCase;

import com.GetTime;
import com.SiteDataSource;
import com.enums.*;
import com.gilat.citrus.behaviors.utils.CommonQueryValidator;
import com.gilat.citrus.behaviors.utils.QueriesValidatorBuilder;
import com.gilat.citrus.behaviors.utils.enums.ResourceType;
import com.enums.Test_Criteria;
import com.gilat.citrus.grpc.client.GilatGrpcClient;
import com.gilat.snmp.GilatSNMPClient;
import com.gilat.citrus.behaviors.hub_element.hsp_dps.validators.ValidateHSPOrDPS;
import com.gilat.citrus.behaviors.hub_element.sw_update.SwUpdateMessage;
import com.gilat.citrus.behaviors.hub_element.sw_update.SoftwareUpdateBehavior;
import com.gilat.excel.RowMap;
import com.nms.commons.model.pojo.OidDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;



import static com.consol.citrus.actions.SleepAction.Builder.sleep;

public class TestCase_HubElem_SwUpdate extends AbstractTestCaseSoftwareUpdate{

    /**
     * Object used for logging purposes
     */
    private final Logger log = LoggerFactory.getLogger(TestCase_HubElem_SwUpdate.class);
    private ResourceType ElemType;
    private String repoInput;
    private String releaseInput;
    private String runningVersion = null;
    private String SiteType = null;



    /**
     * Constructor
     */

    public TestCase_HubElem_SwUpdate(GilatGrpcClient gilatGrpcClient, HashMap<String, List<RowMap<String, String>>> rows) {
        super(gilatGrpcClient, rows, SheetNames.SoftwareUpgrade);
    }

    @Override
    public void setup() {


    }

    @Override
    public void execute() {

        // Initializing CommonQueryValidator
        CommonQueryValidator validator = new CommonQueryValidator().
                withTestResources(runner, context).withDataSource(builder.getDataSource());
        //  Initializing QueriesValidatorBuilder for NOC DB
        QueriesValidatorBuilder NocQueriesValidatorBuilder = new QueriesValidatorBuilder().
                withCitrusResources(runner, context).withDataSource(builder.getDataSource());

        /** Initializing ValidateDSPorDPS class and using method getHSPorDPSId which can get database ID for Hub Element
         like DPS, HSP, IPM, MCR, QCTR */
        ValidateHSPOrDPS nocValidateHSPOrDPS = new ValidateHSPOrDPS(NocQueriesValidatorBuilder, getResourceType());
        /** Initialize class FindLatest which will find path for last software release*/
        FindLatest findLatest = new FindLatest();
        /** If Hub Element Software Version is ends with BY_RELEASE will be change last SW release version */
        String elemPath = com.enums.ElemType.BASEPATH.getValue() + findLatest.getElementPath(rowFromCRUD.get(Columns.Element_Type.getValue()));

        if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(com.enums.ElemType.LATEST_BY_SETUP.getValue())){
            /** Get Last Release version by Setup */
            releaseInput = elemPath + nocValidateHSPOrDPS.getLastSwVersion
                    (String.valueOf(rowFromCRUD.get(Columns.Element_Type.getValue())));
        }
        else  if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(com.enums.ElemType.LATEST_BY_RELEASE.getValue())){
            /** Get Last Release version*/
            releaseInput = elemPath + findLatest.findFoldersInDirectory(elemPath);
        }
        else  if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(".zip")){
            /** Get Last Release version*/
            releaseInput = findLatest.getVersionPath(rowFromCRUD.get(Columns.Software_Version.getValue()),rowFromCRUD.get(Columns.Element_Type.getValue()));
        }




        if (rowFromCRUD.get(Columns.Software_Version.getValue()).startsWith("LATEST")){
            /** Get Software Version Name */
            repoInput = String.valueOf(findLatest.getLatestFileFromDIR(releaseInput));
        }
        else {
            repoInput = rowFromCRUD.get(Columns.Software_Version.getValue());
        }



        //  Get software repository ID using function getSoftwareRepoDbId from Behaviors
        long repositoryId = validator.getSoftwareRepoDbId(repoInput);


        /** Get Hub Element DB ID using getElemDBID Method */
        long hubElemDbID =  nocValidateHSPOrDPS.getElemDBbID(rowFromCRUD.get(Columns.Element_Name.getValue())
                ,rowFromCRUD.get(Columns.Element_Type.getValue()));
        SwUpdateMessage swUpdateMessage = new SwUpdateMessage(repositoryId, new SwUpdateMessage.IdListMessage(hubElemDbID));
        SoftwareUpdateBehavior softwareUpdateBehavior = new SoftwareUpdateBehavior(gilatGrpcClient, swUpdateMessage);
        if (log.isDebugEnabled()) System.out.println("JSON request: \n" + swUpdateMessage);
        runner.applyBehavior(softwareUpdateBehavior);
        /** Validate that Hub element was Updated */
        softwareUpdateBehavior.validateAllHubWasUpdated(validator);
        /**Get Start Time Hub Element Software Change */
        String startTime =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

        /** Write in Excel file in how much time was done SW Upgrade */
        rowFromCRUD.put(Columns.TIME_TO_UPGRADE.getValue(), String.valueOf(gilatGrpcClient.getGrpcCallDuration().toSeconds()));
        /** Get Running version from Hub Elements*/
        GilatSNMPClient gilatSNMPClient = new GilatSNMPClient(nocValidateHSPOrDPS.getElemAssignedIP(hubElemDbID));
        gilatSNMPClient.start();
        runner.timer().name("WaitForDeviceReboot").repeatCount(1).actions(sleep().milliseconds(10000));
        Integer counter = 0;
        while (counter < 18) {
            /** Retry running version on each 30 seconds*/
            runner.timer().name("WaitForDeviceBecameOnline").repeatCount(1).actions(sleep().milliseconds(10000));
            try {
                gilatSNMPClient.get(new OidDTO(HubElemOIDs.RUNING_VER_OID.getValue()));
            } catch (Exception e) {
                counter++;
                continue;
            }
            runner.timer().name("Wait for DB Update Element Status").repeatCount(1).actions(sleep().milliseconds(5000));
            runningVersion = gilatSNMPClient.get(new OidDTO(HubElemOIDs.RUNING_VER_OID.getValue()));
            rowFromCRUD.put(Columns.Running_Version.getValue(), runningVersion);
            break;
        }

        /**Get End Time Hub Element Software Change */
        String endTime = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());

        /**Initialize GetTime Class*/
        GetTime getTime = new GetTime();

        /**Calculate how much time Hub Element became online*/
        Long upgradeTime = getTime.findDifference(startTime,endTime);
        /** Write in execel file how much time it takes for HUb Element to Recovery*/
        rowFromCRUD.put(Columns.TIME_TO_RECOVERY_HE.getValue(), String.valueOf(upgradeTime));

        SiteDataSource siteDataSource = new SiteDataSource();

        //  Initializing QueriesValidatorBuilder for Site DB
        QueriesValidatorBuilder SiteQueriesValidatorBuilder = new QueriesValidatorBuilder().
                withCitrusResources(runner, context).withDataSource(siteDataSource.getSiteType(rowFromCRUD.get(Columns.Element_Type.getValue())));
        ValidateHSPOrDPS siteValidateHSPorDPS = new ValidateHSPOrDPS(SiteQueriesValidatorBuilder, getResourceType());
        /**Get Hub Element Status from DCM/DCM*/
        Integer siteElemStatus = siteValidateHSPorDPS.getElemStatus(hubElemDbID);
        /**Write in Excel Hub Element status from GWM DB*/
        rowFromCRUD.put(Columns.DCM_GWM_ELEMENT_STATUS.getValue(), String.valueOf(siteElemStatus));

        /**Get Hub Element Status from NOC DB */
        Integer nocElemStatus = nocValidateHSPOrDPS.getElemStatus(hubElemDbID);
        /** Write in Excel file Status of Hub element after SW Change */
        if (nocElemStatus == 0) {
            rowFromCRUD.put(Columns.NOC_ELEMENT_STATUS.getValue(), ElementStatus.ONLINE.getValue());
        }else {
            rowFromCRUD.put(Columns.NOC_ELEMENT_STATUS.getValue(), ElementStatus.OFFLINE.getValue());
        }

        if (siteElemStatus == 0) {
            rowFromCRUD.put(Columns.DCM_GWM_ELEMENT_STATUS.getValue(), ElementStatus.ONLINE.getValue());
        }else {
            rowFromCRUD.put(Columns.DCM_GWM_ELEMENT_STATUS.getValue(), ElementStatus.OFFLINE.getValue());
        }



        if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(com.enums.ElemType.LATEST_BY_SETUP.getValue())){
            if ((nocElemStatus == 0) && (siteElemStatus == 0) && runningVersion.equals
                    (nocValidateHSPOrDPS.getLastSwVersion(String.valueOf(rowFromCRUD.get(Columns.Element_Type.getValue()))))){
                rowFromCRUD.put(Columns.Test_Result.getValue(), Test_Criteria.PASS.getValue());
            }   // end inner if
            else {
                rowFromCRUD.put(Columns.Test_Result.getValue(), Test_Criteria.FAIL.getValue());
            }

        } // end outer if
        else  if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(com.enums.ElemType.LATEST_BY_RELEASE.getValue())){
            if ((nocElemStatus == 0) && (siteElemStatus == 0) && runningVersion.equals
                    (findLatest.findFoldersInDirectory(elemPath))){
                rowFromCRUD.put(Columns.Test_Result.getValue(), Test_Criteria.PASS.getValue());
            }     // end if
            else {
                rowFromCRUD.put(Columns.Test_Result.getValue(), Test_Criteria.FAIL.getValue());
            }  // end else
        }  // end else if

        else  if (rowFromCRUD.get(Columns.Software_Version.getValue()).endsWith(".zip")){
            if ((nocElemStatus == 0) && (siteElemStatus == 0) && runningVersion.equals(releaseInput)){
                rowFromCRUD.put(Columns.Test_Result.getValue(), Test_Criteria.PASS.getValue());
            }     // end if
            else {
                rowFromCRUD.put(Columns.Test_Result.getValue(), Test_Criteria.FAIL.getValue());
            }  // end else
        }  // end else if





    }

    private ResourceType getResourceType(){

        if (rowFromCRUD.get(Columns.Element_Type.getValue()).equals(String.valueOf(com.enums.ElemType.DPSv4))){
            ElemType = ResourceType.DPSv4;
        }
        else if (rowFromCRUD.get(Columns.Element_Type.getValue()).equals(String.valueOf(com.enums.ElemType.HSPv4))){
            ElemType = ResourceType.HSPv4;
        }
        else if (rowFromCRUD.get(Columns.Element_Type.getValue()).equals(String.valueOf(com.enums.ElemType.IPM6))){
            ElemType = ResourceType.IPM6;
        }
        else if (rowFromCRUD.get(Columns.Element_Type.getValue()).equals(String.valueOf(com.enums.ElemType.MCR8))){
            ElemType = ResourceType.MCR8;
        }
        return ElemType;
    }



    @Override
    public void cleanup() {

    }
}
