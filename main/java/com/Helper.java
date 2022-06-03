package com;

import javax.sql.DataSource;

import com.TestCase.*;
import com.consol.citrus.TestResult;
import com.consol.citrus.dsl.runner.TestRunner;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.gilat.automation.common.enums.FolderType;
import com.gilat.automation.common.enums.database.DbName;
import com.gilat.automation.common.utils.IPCalcSchema;
import com.gilat.k8s.ColumnsPods;
import com.gilat.k8s.GilatK8sClient;
import com.gilat.k8s.ColDeployment;
import com.gilat.citrus.grpc.client.GilatGrpcClient;
import com.gilat.citrus.grpc.client.GrpcClientBuilder;
import com.gilat.excel.Excel;
import com.gilat.excel.RowMap;
import com.gilat.excel.Table;
import com.gilat.ssh.GilatSshClient;
import com.gilat.automation.common.utils.config.SetupConfig;
import com.Listener.ConfigListener;
import com.google.protobuf.Descriptors;
import com.enums.Columns;
import com.enums.SheetNames;
import enums.Namespaces;
import io.grpc.StatusRuntimeException;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.Assert;
import org.testng.annotations.Listeners;


import java.util.*;
import java.util.stream.Collectors;

import static com.gilat.citrus.grpc.catalog.GrpcEndpointCatalog.grpc;

@Listeners(ConfigListener.class)
public class Helper {


    /**
     * This object is used for connections to the physical data source (our Database). It takes as parameters url, username and password of database
     */
    private static DataSource dataNOCSource;

    /**
     * The Grpc Client used in test
     */
    private static GilatGrpcClient grpcClient;

    /**
     * The index of last Grpc Client used in test
     */
    private static GilatSshClient sshClient;


    private static GilatSshClient createSshClient(){
        String userName = "root";
        String password = "$SatCom$";
        int port = 22;
        //get ip for site
        String ip = IPCalcSchema.getSiteFirstNodeIP(FolderType.SiteNOC,1);
        if (ip==null)
            throw new CitrusRuntimeException("couldn't get ip address for site type: "+"SiteNOC"+", id: "+1);
        //create ssh client
        sshClient = new GilatSshClient(userName,password,ip,port);
        return sshClient;
    }

    public static DataSource createDataSource(){
        createSshClient();
        return createDataSource(IPCalcSchema.POSTGRES.getFixIP(IPCalcSchema.NocVlans.VLAN17),
                sshClient.getPostgresPassword(), DbName.NOC.getName());
    }

    public static DataSource createDataSource(String ip, String password, String dbName){
        DataSource dataNOCSource =  new DriverManagerDataSource(
                String.format("jdbc:postgresql://%s:%s/%s",ip,"5432",dbName),"postgres", password);
        return dataNOCSource;

    }



    public static GilatGrpcClient createGrpcClient(Descriptors.FileDescriptor... fileDescriptorList) {
        //validate ip
        Assert.assertTrue(isValid(SetupConfig.Clients.GRPC.getHost()), "Not Valid IP for GRPC : ["+SetupConfig.Clients.GRPC.getHost()+"]");
        //create grpc client builder with given descriptors and fields
        GrpcClientBuilder clientBuilder = grpc()
                .client()
                .host(SetupConfig.Clients.GRPC.getHost())
                .port(SetupConfig.Clients.GRPC.getPort())
                .deadline((int) SetupConfig.Clients.GRPC.getTimeout())
                .insecure();
        if(fileDescriptorList.length>0)
            clientBuilder.withDescriptors(fileDescriptorList);
//        //build client
        grpcClient = clientBuilder.build();
        return grpcClient;
    }

    public static boolean isValid(final String ip) {
        // only IPv4
        return validator.isValidInet4Address(ip);

    }




    /**
     * This variable is used for validating IPv4 and IPv6 addresses
     */
    private static final InetAddressValidator validator
            = InetAddressValidator.getInstance();



    /**
     * Each array of this two-dimensional array will contain all the data for one test case. This data will be used for DeleteSwVersion
     */
    public static Object[][] dataProviderDeleteSwVersion(final HashMap<String, Table> doc, Descriptors.FileDescriptor... fileDescriptorList) {
        //** Get DELETE sheet from Excel file as a Table */
        Table deleteSwVersionTable = doc.get(SheetNames.DELETE.name());
        //** On each index we have all the data for one test case. This object will be converted into a two-dimensional array of Object type  */
        ArrayList<Object[]> dataProviderAsArrayList = new ArrayList<>();
        dataNOCSource = createDataSource();
        //** Row is equal to 2 because the first two rows are the headers of the Excel file. Each iteration will generate data for one test case */
        for (int row = 2; row < deleteSwVersionTable.maxRows; row++) {
            //** Checks if row is active (marked with x) otherwise the row will be skipped from the test */
            if (deleteSwVersionTable.getRow(row).get(Columns.Active.getValue()).equals("x")) {
                //** The keys are the sheets name (all the sheets we need for SetToDefault) and the value is the row's info from that sheet as a
                // List of RowMap (List can have one o more elements depending on how many rows from that sheet the test will use */
                HashMap<String, List<RowMap<String, String>>> currentRows = new HashMap<>();

                //** Get current row from Excel file (from Main sheet).
                currentRows.put(SheetNames.DELETE.name(), Collections.singletonList(deleteSwVersionTable.getRow(row)));
                createGrpcClient(fileDescriptorList);
                //** Adds an array of size 4 to the ArrayList. Data from this array will be used for one test. Consequently, the method that uses this dataProvider*/
                //** will be executed a number of times == dataProviderAsArrayList.size() */
                dataProviderAsArrayList.add(new Object[]{null, null, new TestCaseDeleteSwVersion(grpcClient, currentRows), dataNOCSource});
            }//end if
        }//end for
        //** converts ArrayList into two-dimensional array of Objects (an array of arrays) */
        return dataProviderAsArrayList.toArray(Object[][]::new);
    }

    /**
     * Each array of this two-dimensional array will contain all the data for one test case. This data will be used for UploadSoftwareVersion
     */
    public static Object[][] dataProviderUploadSwVersion(final HashMap<String, Table> doc,Descriptors.FileDescriptor... fileDescriptorList) {
        //** Get UPLOAD sheet from Excel file as a Table */
        Table uploadSwVersionTable = doc.get(SheetNames.UPLOAD.name());
        //** On each index we have all the data for one test case. This object will be converted into a two-dimensional array of Object type  */
        ArrayList<Object[]> dataProviderAsArrayList = new ArrayList<>();
        dataNOCSource = createDataSource();
        //** Row is equal to 2 because the first two rows are the headers of the Excel file. Each iteration will generate data for one test case */
        for (int row = 2; row < uploadSwVersionTable.maxRows; row++) {
            //** Checks if row is active (marked with x) otherwise the row will be skipped from the test */
            if (uploadSwVersionTable.getRow(row).get(Columns.Active.getValue()).equals("x")) {
                //** The keys are the sheets name (all the sheets we need for SetToDefault) and the value is the row's info from that sheet as a
                // List of RowMap (List can have one o more elements depending on how many rows from that sheet the test will use */
                HashMap<String, List<RowMap<String, String>>> currentRows = new HashMap<>();

                //** Get current row from Excel file (from Main sheet).
                currentRows.put(SheetNames.UPLOAD.name(), Collections.singletonList(uploadSwVersionTable.getRow(row)));
                createGrpcClient(fileDescriptorList);
                //** Adds an array of size 4 to the ArrayList. Data from this array will be used for one test. Consequently, the method that uses this dataProvider*/
                //** will be executed a number of times == dataProviderAsArrayList.size() */
                dataProviderAsArrayList.add(new Object[]{null, null, new TestCaseUploadSwVersion(grpcClient, currentRows), dataNOCSource});
            }//end if
        }//end for
        //** converts ArrayList into two-dimensional array of Objects (an array of arrays) */
        return dataProviderAsArrayList.toArray(Object[][]::new);
    }


    /**
     * Each array of this two-dimensional array will contain all the data for one test case. This data will be used for SoftwareUpdateVersion
     */
    public static Object[][] dataProviderSoftwareUpdateVersion(final HashMap<String, Table> doc, Descriptors.FileDescriptor... fileDescriptorList) {
        //** Get SoftwareUpgrade sheet from Excel file as a Table */
        Table softwareUpdateTable = doc.get(SheetNames.SoftwareUpgrade.name());
        //** On each index we have all the data for one test case. This object will be converted into a two-dimensional array of Object type  */
        ArrayList<Object[]> dataProviderAsArrayList = new ArrayList<>();
        dataNOCSource= createDataSource();

        //** Row is equal to 2 because the first two rows are the headers of the Excel file. Each iteration will generate data for one test case */
        for (int row = 2; row < softwareUpdateTable.maxRows; row++) {
            //** Checks if row is active (marked with x) otherwise the row will be skipped from the test */
            if (softwareUpdateTable.getRow(row).get(Columns.Active.getValue()).equals("x")) {
                //** The keys are the sheets name (all the sheets we need for SetToDefault) and the value is the row's info from that sheet as a
                // List of RowMap (List can have one o more elements depending on how many rows from that sheet the test will use */
                HashMap<String, List<RowMap<String, String>>> currentRows = new HashMap<>();

                //** Get current row from Excel file (from Main sheet).
                currentRows.put(SheetNames.SoftwareUpgrade.name(), Collections.singletonList(softwareUpdateTable.getRow(row)));
                createGrpcClient(fileDescriptorList);

                //** Adds an array of size 4 to the ArrayList. Data from this array will be used for one test. Consequently, the method that uses this dataProvider*/
                //** will be executed a number of times == dataProviderAsArrayList.size() */
                dataProviderAsArrayList.add(new Object[]{null, null, new TestCase_HubElem_SwUpdate(grpcClient, currentRows), dataNOCSource});
            }//end if
        }//end for
        //** converts ArrayList into two-dimensional array of Objects (an array of arrays) */
        return dataProviderAsArrayList.toArray(Object[][]::new);
    }

    /**
     * Writes results of the test into Excel file
     */
    public static void writeToExcel(String outputFilePath, AbstractTestCaseSoftwareUpdate abstractTestCaseSoftwareUpdate, TestRunner testRunner, String sheetName) {
        try {
            //** This line of code writes the result of the test to the console (the words SUCCESS or FAILURE */
            testRunner.echo(testRunner.getTestCase().getTestResult().getResult());
            //** Writes into Excel file the code of the failure and the reason of the failure or writes nothing if the test has passed */
            setTestResult(testRunner.getTestCase().getTestResult(), abstractTestCaseSoftwareUpdate.getRowFromCRUD());
            //** Writes the current row that contains previous information and the result of the test into Excel file */
            Excel.writeRowToSheet(outputFilePath, sheetName, abstractTestCaseSoftwareUpdate.getRowFromCRUD());
        } catch (Exception e) {
            throw new CitrusRuntimeException("Error to write to Excel file: " + e);
        }
    }//end method writeToExcel

    /**
     * Writes the result of the test into Excel file
     */
    private static void setTestResult(TestResult testResult, RowMap<String, String> rowMap) {
        //** Write in Excel file what is the Status of the test */
        rowMap.put(Columns.GRPC_STATUS.getValue(), testResult.getResult());
        //** If the test has failed this variable will hold the cause of the failure (which will be an object of class StatusRuntimeException).
        // If the test didn't fail this variable will be set to null */
        Throwable throwable = testResult.getCause();
        //** This StringBuilder will hold information about the cause of the failure of the test, or will be an empty StringBuilder if the test will pass */
        StringBuilder errors = new StringBuilder();

        //** We will iterate through the stack of the Exception and in the end we will reach the end of the stack and throwable will be equal to null */
        while (throwable != null) {
            //** We are building a StringBuilder that will have all the information about all the exceptions that have occurred */
            errors.append("->").append(throwable.getMessage()).append("\n");
            //** We are iterating through Throwable objects. When we will get to StatusRuntimeException we will be able to fetch the code of the error */
            if (throwable instanceof StatusRuntimeException) {
                //** Writes the code of the failure into Excel file
                rowMap.put(Columns.ACTUAL_BUSINESS_CODE.getValue(), grpcCode((StatusRuntimeException) throwable));
            }//end if
            //** With every iteration throwable.getCause() will fetch another exception, but in the end it will return null. */
            throwable = throwable.getCause();
        }//end while
        //** Writes into Excel file the reason of the failure of the test */
        rowMap.put(Columns.REASON.getValue(), errors.toString());
    }//end method setTestResult



    public static void writeNocKubePodsToExcel(String outputFilePath){
        int GWMSite = SetupConfig.Sites.GWM.getSites()[0];
        int DCMSite = SetupConfig.Sites.DCM.getSites()[0];
        GilatK8sClient gilatK8sNOCClient = new GilatK8sClient(IPCalcSchema.NMS_NODE_1.getFixIP(IPCalcSchema.NocVlans.VLAN17));
        GilatK8sClient gilatK8sGWMClient = new GilatK8sClient(IPCalcSchema.getSiteFirstNodeIP(FolderType.SiteGW,GWMSite));
        GilatK8sClient gilatK8sDCMClient = new GilatK8sClient(IPCalcSchema.getSiteFirstNodeIP(FolderType.SiteDC,DCMSite));
        ArrayList<enums.Namespaces> nameNOCSpaces = new ArrayList<enums.Namespaces>(){
            {
                add(enums.Namespaces.NOC);
                add(enums.Namespaces.POSTGRES);
                add(enums.Namespaces.KAFKA);
            }
        };
        ArrayList<enums.Namespaces> nameGWMSpaces = new ArrayList<enums.Namespaces>(){
            {
                add(enums.Namespaces.GWM);
                add(enums.Namespaces.POSTGRES);
                add(enums.Namespaces.KAFKA);
            }
        };
        ArrayList<enums.Namespaces> nameDCMSpaces = new ArrayList<enums.Namespaces>(){
            {
                add(enums.Namespaces.DCM);
                add(enums.Namespaces.POSTGRES);
                add(enums.Namespaces.KAFKA);
            }
        };

        // Get NOC pods
        for(enums.Namespaces namespace: nameNOCSpaces){
            List<Map<String, String>> kubeNOCResult = gilatK8sNOCClient.getPods(namespace).stream().map(ColumnsPods :: toMap).collect(Collectors.toList());
            Excel.writeSetupStatus(outputFilePath,
                    gilatK8sNOCClient.getSetupName(),
                    gilatK8sNOCClient.getCommand(),
                    kubeNOCResult);
        }

        // Get GWM pods
        for(enums.Namespaces namespace: nameGWMSpaces){
            List<Map<String, String>> kubeGWMResult = gilatK8sGWMClient.getPods(namespace).stream().map(ColumnsPods :: toMap).collect(Collectors.toList());
            Excel.writeSetupStatus(outputFilePath,
                    gilatK8sGWMClient.getSetupName(),
                    gilatK8sGWMClient.getCommand(),
                    kubeGWMResult);
        }

        // Get DCM pods
        for(enums.Namespaces namespace: nameDCMSpaces){
            List<Map<String, String>> kubeDCMResult = gilatK8sDCMClient.getPods(namespace).stream().map(ColumnsPods :: toMap).collect(Collectors.toList());
            Excel.writeSetupStatus(outputFilePath,
                    gilatK8sDCMClient.getSetupName(),
                    gilatK8sDCMClient.getCommand(),
                    kubeDCMResult);
        }
        // Get NOC deployments
        for(Namespaces namespace: nameNOCSpaces){
            List<Map<String, String>> kubeResult = gilatK8sNOCClient.getDeployment(namespace).stream().map(ColDeployment :: toMap).collect(Collectors.toList());
            Excel.writeSetupStatus(outputFilePath,
                    gilatK8sNOCClient.getSetupName(),
                    gilatK8sNOCClient.getCommand(),
                    kubeResult);
        }
        // Get GWM deployments
        for(Namespaces namespace: nameGWMSpaces){
            List<Map<String, String>> kubeGWMResult = gilatK8sGWMClient.getDeployment(namespace).stream().map(ColDeployment :: toMap).collect(Collectors.toList());
            Excel.writeSetupStatus(outputFilePath,
                    gilatK8sGWMClient.getSetupName(),
                    gilatK8sGWMClient.getCommand(),
                    kubeGWMResult);
        }
        // Get DCM deployments
        for(Namespaces namespace: nameDCMSpaces){
            List<Map<String, String>> kubeDCMResult = gilatK8sDCMClient.getDeployment(namespace).stream().map(ColDeployment :: toMap).collect(Collectors.toList());
            Excel.writeSetupStatus(outputFilePath,
                    gilatK8sDCMClient.getSetupName(),
                    gilatK8sDCMClient.getCommand(),
                    kubeDCMResult);
        }
    }

    /**
     * @param runtimeException if when executing a test we have encountered an exception this method takes as argument that exception and returns a code. If we know the code we can find out
     *                         what is the exception
     * @return the code of the exception as a String (for example: code 52 if we try to create a constellation which name already exists)
     */
    private static String grpcCode(StatusRuntimeException runtimeException) {
        try {
            //** Create a JSONObject that will hold information about the Exception that occurred during the execution of the test */
            JSONObject object = (JSONObject) new JSONParser().parse(runtimeException.getStatus().getDescription());
            //** Returns the code as a String
            return object.get("code").toString();
        } catch (Exception e) {
            return "";//If an exception arises when parsing, returns an empty String
        }

    }//end method grpcCode
}

