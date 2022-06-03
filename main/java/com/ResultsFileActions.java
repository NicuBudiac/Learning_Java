
package com;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface ResultsFileActions {
    Logger log = LoggerFactory.getLogger(ResultsFileActions.class);
    FileSystem system = FileSystems.getDefault();

    /**
     * Method to get file name from full path
     *
     * @param fullFilePath - the full path to file
     * @return file name
     */
    static String getFileNameFromPath(String fullFilePath) {
        Path p = system.getPath(fullFilePath);
        return p.getFileName().toString();
    }

    /**
     * Method to get path without file name from full path
     *
     * @param fullFilePath - the full path to file
     * @return path without file
     */
    static String getBaseFilePath(String fullFilePath) {
        String tmp = getFileNameFromPath(fullFilePath);
        if (fullFilePath.equals(tmp))
            return "";
        else
            return fullFilePath.split(tmp)[0];
    }

    /**
     * Creates a folder with current date (day_Month_Year) in Results directory (and Results dir if not exists)
     *
     * @param baseFilePath path without file
     * @return baseFilePath/Results/yyyy_MM_dd
     */
    static String createResultsPath(String baseFilePath) {
        SimpleDateFormat formatterResult = new SimpleDateFormat("yyyy_MM_dd");
        Date date = new Date(System.currentTimeMillis());
        String resultsFilePath =Path.of(baseFilePath,"Results",formatterResult.format(date)).toString()+"\\";
        File dir = new File(resultsFilePath);
        if (!dir.exists()) dir.mkdirs();
        return resultsFilePath;
    }

    /**
     * Creates a name for report file as a copy of base file plus current date and time (require .extension)
     * Default format is: _yyyy_MM_dd_HH_mm_ss
     *
     * @param fileName - file name with extension and without path
     * @return fileName_yyyy_MM_dd_HH_mm_ss.extension
     */
    static String createDefaultResultsFile(String fileName) {
        return createResultsFile(fileName, "_yyyy_MM_dd_HH_mm_ss");
    }

    /**
     * Separates file name with extension into file name and extension. Adds to file name given format and extension
     *
     * @param fileName name of file with extension and without path
     * @param format   - string for SimpleDateFormatter constructor
     * @return filename+format+extension (test_2020_01.xlsx, where filename is: test, format is: _yyyy_MM(_2020_01), and extension is: .xlsx)
     */
    static String createResultsFile(String fileName, String format) {
        assert fileName.contains(".") : "Missing extension for: "+fileName;
        String[] dest = fileName.split("\\.");
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return dest[0] + formatter.format(date) + "." + dest[1];
    }

    static String createDefaultResultsFile(String fileName, String version) {
        return createResultsFile(fileName, version, "_yyyy_MM_dd_HH_mm_ss");
    }

    /**
     * Separates file name with extension into file name and extension. Adds to file name given format and extension
     *
     * @param fileName name of file with extension and without path
     * @param format   - string for SimpleDateFormatter constructor
     * @param version  - version from /api-version request
     * @return filename+version+format+extension (test_v1.1_2020_01.xlsx, where filename is: test, version is v1.1, format is: _yyyy_MM(_2020_01), and extension is: .xlsx)
     */
    static String createResultsFile(String fileName, String version, String format) {
        assert fileName.contains(".") : "Missing excel 'xlsx' extension for: "+fileName;
        String[] dest = fileName.split("\\.");
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(System.currentTimeMillis());
        return dest[0] + version + formatter.format(date) + "." + dest[1];
    }

    /**
     * Copy an file to another location and changes name
     *
     * @param source - actual path of file+name as String
     * @param dest   - destination folder+name of copy
     */
    static void copyFileUsingStream(String source, String dest) {
        log.trace("Source file: " + source);
        log.trace("Dest file: " + dest);
        Path original = system.getPath(source);
        Path target = system.getPath(dest);
        try {
            // Throws an exception if the original file is not found.
            Files.copy(original, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Create copy from \n" + source + "\n to \n" + dest);
        } catch (IOException ex) {
            log.error("ERROR copy from \n" + source + "\n to \n" + dest);
        }

    }

    /**
     * Automatically build results file name and copy input file to that location and changing file name to result file name
     *
     * @param fullFilePath the full path to file
     * @return the full path to results file
     */
    static String automaticallyGenerateResultsInSameDir(String fullFilePath) {
        String fileName = ResultsFileActions.getFileNameFromPath(fullFilePath);
        log.info("File name: " + fileName);
        String basePath = ResultsFileActions.getBaseFilePath(fullFilePath);
        log.info("Base path: " + basePath);
        String resultsFile = ResultsFileActions.createDefaultResultsFile(fileName);
        log.info("Results File name: " + resultsFile);
        String resultsDes = ResultsFileActions.createResultsPath(basePath) + resultsFile;
        log.info("Reports destination: " + resultsDes);
        ResultsFileActions.copyFileUsingStream(fullFilePath, resultsDes);
        return resultsDes;
    }

    static String automaticallyGenerateResultsInTargetDir(String fullFilePath) {
        String fileName = ResultsFileActions.getFileNameFromPath(fullFilePath);
        log.info("File name: " + fileName);
        String basePath = System.getProperty("user.dir");
        log.info("Base path: " + basePath);
        String resultsFile = ResultsFileActions.createDefaultResultsFile(fileName);
        log.info("Results File name: " + resultsFile);
        basePath=Path.of(basePath,"target",fileName.split("\\.")[0]).toString();
        String resultsDes = ResultsFileActions.createResultsPath(basePath) + resultsFile;
        log.info("Reports destination: " + resultsDes);
//        ResultsFileActions.copyFileUsingStream(fullFilePath, resultsDes);
        return resultsDes;
    }
    /**
     * Automatically build results file name and copy input file to that location and changing file name to results file name
     *
     * @param fullFilePath the full path to file
     * @param version      version from /api-version request
     * @return the full path to results file
     */
    static String automaticallyGenerateResultsInSameDir(String fullFilePath, String version) {
        String fileName = ResultsFileActions.getFileNameFromPath(fullFilePath);
        log.info("File name: " + fileName);
        String basePath = ResultsFileActions.getBaseFilePath(fullFilePath);
        log.info("Base path: " + basePath);
        String resultsFile = ResultsFileActions.createDefaultResultsFile(fileName, version);
        log.info("Results File name: " + resultsFile);
        String resultsDes = ResultsFileActions.createResultsPath(basePath) + resultsFile;
        log.info("Reports destination: " + resultsDes);
        ResultsFileActions.copyFileUsingStream(fullFilePath, resultsDes);
        return resultsDes;
    }

    /**
     * Automatically build results file name and copy input file in given location and changing file name to results file name
     *
     * @param fullFilePath the full path to file
     * @return the full path to results file
     */
    static String automaticallyGenerateResultsInAnotherDir(String fullFilePath, String resultsDir) {
        String fileName = ResultsFileActions.getFileNameFromPath(fullFilePath);
        log.info("File name: " + fileName);
        String resultsFile = ResultsFileActions.createDefaultResultsFile(fileName);
        log.info("Results File name: " + resultsFile);
        String resultsDes = resultsDir + resultsFile;
        log.info("Reports destination: " + resultsDes);
        ResultsFileActions.copyFileUsingStream(fullFilePath, resultsDes);
        return resultsDes;
    }

    /**
     * Save byte array in file
     *
     * @param data     bite array
     * @param filePath output file
     */
    static void generateCBORFile(byte[] data, String filePath) {

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
            outputStream.write(data);
            File dir;
            String baseFilePath = getBaseFilePath(filePath);
            dir = new File(baseFilePath);
            if (!dir.exists()) dir.mkdirs();
            File myFile = new File(filePath);
            try (OutputStream outputStream1 = new FileOutputStream(myFile)) {
                outputStream.writeTo(outputStream1);
                log.trace("Writing to: " + filePath);

            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }


    }
    static void copyResultsToGNA(String excelResults, String gnaResultsFolder) {
        gnaResultsFolder = ResultsFileActions.createResultsPath(gnaResultsFolder);
        ResultsFileActions.copyFileUsingStream(excelResults, gnaResultsFolder + ResultsFileActions.getFileNameFromPath(excelResults));
    }
}