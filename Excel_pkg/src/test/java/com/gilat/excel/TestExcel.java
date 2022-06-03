package com.gilat.excel;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class TestExcel {

    // For windows: %USERPROFILE%\AppData\Local\Temp  For linux: /tmp
    private static final String tempDir = System.getProperty("java.io.tmpdir");
    public static String filePath = "src/test/resources/Scenarios.xlsx";
    //public static String filePathOut = "src/test/resources/Out.xlsx";
    public static String filePathOut = tempDir + "_Test_/TestOut.xlsx";
    //public static String filePathOut = "\\\\gna2\\pituach\\Automation\\Test\\mPower\\_Test_\\TestOut.xlsx"; /*works*/
    //public static String filePathOut = "//gna2/pituach/Automation/Test/mPower/_Test_/TestOut.xlsx"; /*also works*/
    //public static String filePathOut = "//gna2/pituach/Automation/Test/mPower/BootP/Results/YYYY_MM_DD/YYYY_MM_DD_hh_mm_ss_File.xlsx"; /*also works*/
    public static String[] sheets = {"MAIN", "ROWS"};
    public static String sheetName = sheets[0];
    public Object[][] table;
    private HashMap<String, Table> doc;
    private final RowMap<String, String> rowMap = new RowMap<>();

    @BeforeTest
    public void setUp() {
        Excel.copyFile(filePath, filePathOut);
        doc = Excel.readSheets(filePathOut, sheets);
        assert doc != null;
        table = doc.get(sheetName).getTable();
        //table[0][0] = table[0][0] + "addString";
        //table[3][6] = "Pass";
//        rowMap.put("Index", "1");
//        rowMap.put("RESULT", "Pass");
    }

    @Test
    public void readExcelTest() {
        Assert.assertNotNull(doc);
        System.out.println("File read: " + doc.values());
        System.out.println("Row 0 | Column 0 value: " + doc.get("MAIN").getTable()[0][0]);
        System.out.println("End of read test.");
    }

    @Test (expectedExceptions = {NullPointerException.class}, expectedExceptionsMessageRegExp = "No such file path: .*")
    public void readExcelFileNotFoundTest() {
        String filePathWrong = tempDir + "NotAnActualFolder\\NotAnActualFile.file";
        //doc = Excel.readSheets(filePathOut, sheets);
        doc = Excel.readSheets(filePathWrong, sheets);
    }

    @Test (expectedExceptions = {NullPointerException.class}, expectedExceptionsMessageRegExp = "No such file path: .*")
    public void readExcelFileNotFoundTestTwo() {
        String filePathWrong = tempDir + "_Test_/TestOut.doc";
        doc = Excel.readSheets(filePathWrong, sheets);
    }

    @Test (expectedExceptions = {NullPointerException.class}, expectedExceptionsMessageRegExp = "No such sheet name: .*")
    public void readExcelEmptySheetTest() {
        String[] sheets = {"MAIN",""};
        doc = Excel.readSheets(filePathOut, sheets);
    }

    @Test
    public void theWriteTableToExcelTest() {
        Assert.assertNotNull(doc);
        System.out.println("File read: " + filePathOut);
        System.out.println("Write new table to excel sheet.");
        Object[][] table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName).getTable();
        table[3][6] = "Pass: write table to sheet";
        Excel.writeTableToSheet(filePathOut, sheetName, table);
        Object[][] validationTable = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheets[0]).getTable();
        for (int row = 0; row < table.length; row++) {
            for (int col = 0; col < table[0].length; col++) {
                Assert.assertEquals(validationTable[row][col], table[row][col]);
            }
        }
        System.out.println("End of write test.");
    }


    @Test (expectedExceptions = {NullPointerException.class}, expectedExceptionsMessageRegExp = "No such file path: .*")
    public void theWriteTableToExcelWrongFileTest() {
        String filePathWrong = tempDir + "_Test_/TestOut.doc";
        //String filePathWrong = "";
        Excel.writeTableToSheet(filePathWrong,sheetName,table);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class}, expectedExceptionsMessageRegExp = "sheetName '' is invalid .*")
    public void theWriteTableToExcelNoSheetNameTest() {
        String sheetName = "";
        Excel.writeTableToSheet(filePathOut,sheetName,table);
    }

    @Test
    public void theWriteTableToExcelNewSheetNameTest() {
        System.out.println("*****");
        System.out.println("Write table to excel nonexistent sheet");
        System.out.println("Write Table Obj to Excel: " + filePathOut);
        Object[][] table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName).getTable();
        String sheetName = "MAIN_New";
        String[] sheets = {sheetName};
        String stringPass = "Pass: write Table to new sheet";
        table[3][6] = stringPass;
        Excel.writeTableToSheet(filePathOut,sheetName,table);
        //Excel.writeTableToSheet(filePathOut, sheetName + "_New", tableObj);
        table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName).getTable();
        Assert.assertTrue (table.length > 0);
        Assert.assertEquals (table[3][6], stringPass);
        System.out.println("End of write test.");
    }

    @Test
    public void writeTableObjToExcelTest() {
        Assert.assertNotNull(doc);
        System.out.println("Write Table Obj to Excel: " + filePathOut);
        //Table tableObj = doc.get("MAIN");
        Table tableObj = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        tableObj.getTable()[5][6] = "Pass: write TableObj to sheet";
        Excel.writeTableToSheet(filePathOut, sheetName, tableObj);
        Table tableObjValidation = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        System.out.println(tableObjValidation.getTable()[5][6]);
        Assert.assertEquals(tableObjValidation.getTable()[5][6], "Pass: write TableObj to sheet");
        System.out.println("End of write test.");
    }

    @Test (expectedExceptions = {NullPointerException.class}, expectedExceptionsMessageRegExp = "No such file path: .*")
    public void writeTableObjToExcelWrongFilePathTest() {
        String filePathWrong = tempDir + "_Test_/TestOut.doc";
        Table tableObj = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        Excel.writeTableToSheet(filePathWrong, sheetName, tableObj);
    }

    @Test (expectedExceptions = {IllegalArgumentException.class}, expectedExceptionsMessageRegExp = "sheetName '' is invalid .*")
    public void writeTableObjToExcelEmptySheetNameTest() {
        Table tableObj = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        String sheetNameWrong = "";
        Excel.writeTableToSheet(filePathOut, sheetNameWrong, tableObj);
    }

    @Test
    public void getRowFromTable() {
        Table MAIN = doc.get("MAIN");
        int index = 2;
        RowMap<String, String> currentRow = MAIN.getRow(index);
        Assert.assertTrue(currentRow.getTableRowIndex() == 2);
        System.out.printf("Value of Index column from row %d is: [%s]\n", index, currentRow.get("Index"));
    }

    @Test
    public void getRowsFromTable() {
        Table MAIN = doc.get("ROWS");
        List<RowMap<String, String>> activeRows = MAIN.getRows("Active", "x");
        Assert.assertEquals(activeRows.size(), 6, "Expected active rows don't match");
        Assert.assertEquals(activeRows.get(0).size(), 3, "Expected columns for first active row don't match");

        List<RowMap<String, String>> platformInfo1 = MAIN.getRows("PLATFORM_INFO", "pi1");
        Assert.assertEquals(platformInfo1.size(), 3, "Platform info 1 filtered rows don't match");
        Assert.assertEquals(platformInfo1.get(2).get("Index"), "6");
        Assert.assertEquals(platformInfo1.get(2).get("Active"), "x");
        Assert.assertEquals(platformInfo1.get(2).get("PLATFORM_INFO"), "pi1");

        List<RowMap<String, String>> platformInfo3 = MAIN.getRows("PLATFORM_INFO", "pi3");
        Assert.assertEquals(platformInfo3.size(), 1, "Platform info 3 filtered rows don't match");
        Assert.assertEquals(platformInfo3.get(0).get("Index"), "3");
        Assert.assertEquals(platformInfo3.get(0).get("Active"), "x");
        Assert.assertEquals(platformInfo3.get(0).get("PLATFORM_INFO"), "pi3");

        List<RowMap<String, String>> platformInfo4 = MAIN.getRows("PLATFORM_INFO", "pi4");
        Assert.assertEquals(platformInfo4.size(), 2, "Platform info 4 filtered rows don't match");
        Assert.assertEquals(platformInfo4.get(1).get("Index"), "5");
        Assert.assertEquals(platformInfo4.get(1).get("Active"), "x");
        Assert.assertEquals(platformInfo4.get(1).get("PLATFORM_INFO"), "pi4");

        List<RowMap<String, String>> missingColumn = MAIN.getRows("missingColumn", "pi4");
        Assert.assertTrue(missingColumn.isEmpty());

        List<RowMap<String, String>> nonexistentValue = MAIN.getRows("PLATFORM_INFO", "nonexistentValue");
        Assert.assertTrue(nonexistentValue.isEmpty());
    }

    @Test
    public void getRowFromTableIndexOutOfRange() {
        Table MAIN = doc.get("MAIN");
        HashMap<String, String> currentRow = MAIN.getRow(1000000);
        assert currentRow.isEmpty();
    }

    @Test
    public void writeRowToExcel() {
        rowMap.put("Index", "1");
        rowMap.put("RESULT", "Pass: write row to excel - first");
        rowMap.setTableRowIndex(1);
        Excel.writeRowToSheet(filePathOut, sheetName, rowMap);
        rowMap.put("Index", "2");
        rowMap.put("RESULT", "Pass: write row to excel - second");
        rowMap.setTableRowIndex(2);
        Excel.writeRowToSheet(filePathOut, sheetName, rowMap);
        Table table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        Assert.assertEquals(table.getTable()[1][6], "Pass: write row to excel - first");
        System.out.println(table.getTable()[1][6]);
        Assert.assertEquals(table.getTable()[2][6], "Pass: write row to excel - second");
        System.out.println(table.getTable()[2][6]);
    }

    @Test (expectedExceptions = {NullPointerException.class})
    public void writeRowToExcelWrongRow() {
        RowMap<String, String> rowMap = new RowMap<>();
        rowMap.put("Index", "10000");
        rowMap.put("RESULT", "Fail: write row to excel");
        rowMap.setTableRowIndex(10000);
        Excel.writeRowToSheet(filePathOut, sheetName, rowMap);
    }

    @Test
    public void updateRowInTable() {
        rowMap.put("Index", "4");
        rowMap.put("RESULT", "Pass: update row in Table");
        rowMap.setTableRowIndex(4);
        Table table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        table.updateRow(rowMap);
        Excel.writeTableToSheet(filePathOut, sheetName, table);
        table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        Assert.assertEquals(table.getTable()[4][6], "Pass: update row in Table");
        System.out.printf("Table row updated: [%s] \n", table.getTable()[4][6]);
    }

    @Test (expectedExceptions = {ArrayIndexOutOfBoundsException.class}, expectedExceptionsMessageRegExp = "Index .*. out of bounds .*")
    public void updateRowInTableWrongRow() {
        RowMap<String, String> rowMap = new RowMap<>();
        rowMap.put("Index", "40000");
        rowMap.put("RESULT", "Fail: update row in Table");
        rowMap.setTableRowIndex(40000);
        Table table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheetName);
        table.updateRow(rowMap);
    }

    @Test
    public void writeTableObjToExcelNonExistentSheetTest() {
        String[] sheets = {"MAIN_New2"};
        Assert.assertNotNull(doc);
        System.out.println("*****");
        System.out.println("Write table to excel nonexistent sheet");
        System.out.println("Write Table Obj to Excel: " + filePathOut);
        Table tableObj = doc.get("MAIN");
        tableObj.getTable()[5][6] = "Pass: write TableObj to new sheet";
        Excel.writeTableToSheet(filePathOut, sheets[0], tableObj);
        Table table = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheets[0]);
        assert (table.maxRows > 0);
        System.out.println("End of write test.");
    }
    @Test
    public void theWriteTableToExcelTest_() {
        Assert.assertNotNull(doc);
        System.out.println("File read: " + filePathOut);
        System.out.println("Write new table to excel sheet.");
        Object[][] table = Objects.requireNonNull(Excel.readSheets(filePathOut, "MAIN","ROWS")).get(sheetName).getTable();
        table[3][6] = "Pass: write table to sheet";
        Excel.writeTableToSheet(filePathOut, sheetName, table);
        Object[][] validationTable = Objects.requireNonNull(Excel.readSheets(filePathOut, sheets)).get(sheets[0]).getTable();
        for (int row = 0; row < table.length; row++) {
            for (int col = 0; col < table[0].length; col++) {
                Assert.assertEquals(validationTable[row][col], table[row][col]);
            }
        }
        System.out.println("End of write test.");
    }
}
