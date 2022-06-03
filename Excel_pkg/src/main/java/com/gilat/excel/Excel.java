package com.gilat.excel;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Excel {

    /**
     * Logger
     */
    protected final static Logger log = LoggerFactory.getLogger(Excel.class);
    private static final String SETUP_STATUS = "Setup_Status";
    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

//    private static synchronized XSSFWorkbook openExcel(String filePath) {
//        verifyPath(filePath);
//        try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath))) {
//            return wb;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * Extract list of Tables from excel by given array of sheets
     *
     * @param filepath
     *         path to excel
     * @param sheets
     *         array of sheets
     *
     * @return List of Tables
     */
    public static synchronized HashMap<String, Table> readSheets(String filepath, String... sheets) {
        verifyPath(filepath);
        HashMap<String, Table> sheetTableMap = new HashMap<>();
        try (XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filepath))) {
            for (String sheetName : sheets) {
                try {
                    Object[][] table = read(wb, sheetName);
                    sheetTableMap.put(sheetName, new Table(table, sheetName));
                } catch (NullPointerException e) {
                    throw new NullPointerException("No such sheet name: [" + sheetName + "]");
                }
            }
            return sheetTableMap;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object[][] read(XSSFWorkbook wb, String sheetName) {
        XSSFSheet sheet = wb.getSheet(sheetName);
        int rowCount = sheet.getLastRowNum();
        int column = sheet.getRow(0).getLastCellNum();
        Object[][] data = new Object[rowCount + 1][column];
        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        for (int i = 0; i <= rowCount; i++) {
            for (int j = 0; j < column; j++) {
                String val = "";
                if (sheet.getRow(i) != null) {
                    XSSFCell cell = sheet.getRow(i).getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    val = DATA_FORMATTER.formatCellValue(cell, evaluator);
                }
                evaluator.clearAllCachedResultValues();
                data[i][j] = val;
            }
        }
        return data;
    }

    private static void verifyPath(String filePath) {
        File f = new File(filePath);
        if (!f.exists())
            throw new NullPointerException("No such file path: " + filePath);
    }

    public static void writeTableToSheet(String filePath, String sheetName, Object[][] table) {
        verifyPath(filePath);
        try (
                FileInputStream file = new FileInputStream(filePath);
                XSSFWorkbook wb = new XSSFWorkbook(file)
        ) {

            write(new FileOutputStream(filePath), new Table(table, sheetName), wb);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeTableToSheet(String filePath, String sheetName, Table table) {
        verifyPath(filePath);
        try (
                FileInputStream file = new FileInputStream(filePath);
                XSSFWorkbook wb = new XSSFWorkbook(file)
        ) {
            write(new FileOutputStream(filePath), table, wb);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void write(OutputStream fileOut, Table table, XSSFWorkbook wb) throws IOException {
        try (fileOut) {
            XSSFSheet sheet;
            if (wb.getSheet(table.getTableName()) == null) {
                wb.createSheet(table.getTableName());
            }
            sheet = wb.getSheet(table.getTableName());
            for (int row = 0; row < table.maxRows; row++) {
                XSSFRow excelRow = sheet.getRow(row);
                if (excelRow == null)
                    excelRow = sheet.createRow(row);
                for (int col = 0; col < table.maxCols; col++) {
                    excelRow.createCell(col).setCellValue(table.getTable()[row][col].toString());
                }
            }
            wb.write(fileOut);
        }
    }

    public static void writeRowToSheet(String filepath, String sheetName, RowMap<String, String> rowMap) {
        verifyPath(filepath);
        try (
                FileInputStream file = new FileInputStream(filepath);
                XSSFWorkbook wb = new XSSFWorkbook(file)) {
            try {
                Table table = new Table(read(wb, sheetName), sheetName);
                if (table.maxRows > rowMap.getTableRowIndex()) {
                    table.updateRow(rowMap);
                    write(new FileOutputStream(filepath), table, wb);
                } else throw new NullPointerException("Row index out of bounds");
            } catch (NullPointerException e) {
                throw new NullPointerException("No such sheet name: " + sheetName);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static XSSFRow createRow(int rowIndex, XSSFSheet sheet) {
        XSSFRow excelRow = sheet.getRow(rowIndex);
        if (excelRow == null)
            excelRow = sheet.createRow(rowIndex);
        return excelRow;
    }

    public static void writeSetupStatus(String filepath, String siteName, String kubectlCommand, List<Map<String, String>> rowMapList) {

        verifyPath(filepath);
        try (FileInputStream in = new FileInputStream(filepath);
             XSSFWorkbook wb = new XSSFWorkbook(in);
             FileOutputStream out = new FileOutputStream(filepath)
        ) {

            XSSFSheet sheet = wb.getSheet(SETUP_STATUS);

            if (sheet == null) {
                sheet = wb.createSheet(SETUP_STATUS);
            }
            AtomicInteger lastRow = new AtomicInteger(sheet.getLastRowNum());
            if (lastRow.get() > 10000) {
                throw new IllegalArgumentException("Last row index " + lastRow.get() + " is greater than maximum allowed 10 000");
            }
//                int lastColumn = sheet.row.getRow(0).getLastCellNum();
            List<List<Map<String, String>>> sortedList = new ArrayList<>();
            rowMapList.forEach(m1 -> Excel.addMap(m1, sortedList));
            XSSFSheet finalSheet = sheet;
            sortedList.forEach(l -> {
                int last = writeStatus(l, finalSheet, lastRow.get() + 2, siteName, kubectlCommand);
                lastRow.set(last);
            });

            wb.write(out);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int writeStatus(List<Map<String, String>> rowMapList, XSSFSheet sheet, int lastRow, String siteName, String namespace) {
        XSSFRow excelRow = createRow(++lastRow, sheet);
        excelRow.createCell(0).setCellValue(siteName);
        excelRow.createCell(1).setCellValue(namespace);
        if (!rowMapList.isEmpty()) {
            int colIndex = -1;
            excelRow = createRow(++lastRow, sheet);
            String[] columns = rowMapList.get(0).keySet().toArray(String[]::new);
            for (String colName : columns) {
                excelRow.createCell(++colIndex).setCellValue(colName);
            }
            for (int row = ++lastRow; row < lastRow + rowMapList.size(); row++) {
                excelRow = createRow(row, sheet);
                Map<String, String> rowMap = rowMapList.get(row - lastRow);
                for (int col = 0; col < rowMap.size(); col++) {

                    String value = rowMap.get(columns[col]);
                    if (value == null)
                        value = "";
                    excelRow.createCell(col).setCellValue(value);
                }
            }
        }
        return lastRow + rowMapList.size() - 1;
    }

    private static void addMap(Map<String, String> mp1,
                               List<List<Map<String, String>>> sortedList) {
        if (sortedList.isEmpty()) {
            createList(mp1, sortedList);
        } else {
            AtomicBoolean found = new AtomicBoolean(false);
            sortedList.forEach(l -> addToList(mp1, found, l));
            if (!found.get()) {
                createList(mp1, sortedList);
            }
        }
    }

    private static void addToList(Map<String, String> mp1, AtomicBoolean found, List<Map<String, String>> l) {
        if (l.get(0).keySet().equals(mp1.keySet())) {
            l.add(mp1);
            found.set(true);
        }
    }

    private static void createList(Map<String, String> mp1, List<List<Map<String, String>>> sortedList) {
        List<Map<String, String>> tmp = new ArrayList<>();
        tmp.add(mp1);
        sortedList.add(tmp);
    }
//    public static void writeRowToSheetInOnePass(String filepath, String sheetName, HashMap<String, String> rowMap){
//        notFinishedYet
//        String value = "";
//        XSSFWorkbook wb = new XSSFWorkbook();
//        XSSFSheetSheet sheet = wb.getSheet(sheetName);
//        Cell cell2Update = sheet.getRow(1).getCell(2);
//        cell2Update.setCellValue(value);
//    }

    /**
     * Copy a file to another location and changes name Creates destination directory if it does not exist
     *
     * @param source
     *         - actual path of file as String (Ex: "C:/Temp/TestIn.xlsx")
     * @param destination
     *         - destination of copy (Ex: "//gna2/pituach/Automation/Test/mPower/_Test_/TestOut.xlsx")
     */

    public static void copyFile(String source, String destination) {
        log.trace("Source file: " + source);
        log.trace("Destination file: " + destination);
        FileSystem system = FileSystems.getDefault();

        Path original = system.getPath(source);
        Path target = system.getPath(destination);
        //Throws an exception if the original fie is not found.
        try {
            Files.createDirectories(target.getParent());
            Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("Create copy from source: [%s] to destination: [%s]", source, destination));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
