package com.TestCase;


import com.enums.ElemType;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FindLatest {
    private String filePath;
    private String fileName;
    private String elemType;
    private String versionName;
    public File getLatestDirFromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        File lastModifiedFile = files[0];
        for (int i = 1; i < files.length; i++) {
            if (lastModifiedFile.lastModified() < files[i].lastModified() && files[i].isDirectory()) {
                lastModifiedFile = files[i];
            }
        }
        return lastModifiedFile;
    }

    public String getLatestFileFromDIR(String filepath) {
        this.filePath = filepath;
        File folder = new File(String.valueOf(filepath));
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (String.valueOf(listOfFiles[i]).endsWith(".zip")) {
                fileName = listOfFiles[i].getName();
            }
        }
        return fileName;
    }

    public String getElementPath(String elemType) {
        this.elemType = elemType;
        if (elemType.equals(ElemType.DPSv4.getValue()) || elemType.equals(ElemType.HSPv4.getValue())) {
            elemType = elemType.substring(0, elemType.length() - 2) + ElemType.MIDPATH.getValue();
        } else if (elemType.equals(ElemType.IPM6.getValue()) || elemType.equals(ElemType.MCR8.getValue())) {
            elemType = elemType.substring(0, elemType.length() - 1) + ElemType.MIDPATH.getValue();
        }

        return elemType;
    }

    public String getVersionPath(String versionName, String elemType ){
        this.versionName = versionName;
        this.elemType = elemType;
        if (elemType.equals(ElemType.DPSv4.getValue()) || elemType.equals(ElemType.HSPv4.getValue())) {
            versionName = versionName.substring(11, versionName.length() - 9);
        }
        else if (elemType.equals(ElemType.IPM6.getValue()) || elemType.equals(ElemType.MCR8.getValue())) {
            versionName = versionName.substring(10, versionName.length() - 9);
        }



        return versionName;
    }

    public String findFoldersInDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        FileFilter directoryFileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        /**Get subdirectories from Directory in a list */
        File[] directoryListAsFile = directory.listFiles(directoryFileFilter);
        List<String> foldersInDirectory = new ArrayList<String>(directoryListAsFile.length);
        for (File directoryAsFile : directoryListAsFile) {
            foldersInDirectory.add(directoryAsFile.getName());
        }
        /** Remove directory from list that should not be used */
        for (int i = 0; i < foldersInDirectory.size(); i++) {
            String dirName = foldersInDirectory.get(i);
            if (dirName.contains("-")) {
                foldersInDirectory.remove(dirName);
            }
        }
        /**Remove points from each directory name to find greater value which means last release*/
        List<Integer> replacedDir = new ArrayList();
        for (int k = 0; k < foldersInDirectory.size(); k++){
            String dirName = foldersInDirectory.get(k);
            replacedDir.add(Integer.valueOf(dirName.replace(".","")));
        }



        /** Get index of list element which represent last release as list*/
        List indexOfLastRelease = (Arrays.asList(replacedDir.indexOf(Collections.max(replacedDir))));
        /** Transform List value in String value*/
        String stringIndexOfLastRelease = String.valueOf(indexOfLastRelease.get(0));
        /**Tranform String Index value of last release in Integer value */
        Integer integerIndexOfLastRelease = Integer.parseInt(stringIndexOfLastRelease);

        return foldersInDirectory.get(integerIndexOfLastRelease);
    }





}


