package org.reactome.server.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.reactome.server.model.FoamtreeFactory;
import org.reactome.server.model.FoamtreeGenerator;
import org.reactome.server.model.data.Foamtree;
import org.reactome.server.graph.service.TopLevelPathwayService;
import org.reactome.server.util.LogDataCSVParser;
import org.reactome.server.util.JsonSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Component
public class PopularPathwaysService {

    private TopLevelPathwayService tlpService;
    private LogDataCSVParser logDataCSVParser;
    private FileUploadService fileUploadService;
    private static String popularPathwayFolder;
    public Map<File, File> AVAILABLE_FILES;

    @Autowired
    public void setTlpService(TopLevelPathwayService tlpService) {
        this.tlpService = tlpService;
    }

    @Autowired
    public void setLogDataCSVParser(LogDataCSVParser logDataCSVParser) {
        this.logDataCSVParser = logDataCSVParser;
    }

    @Autowired
    public void setFileUploadService(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }


    public String getPopularPathwayFolder() {
        return popularPathwayFolder;
    }

    public PopularPathwaysService(@Value("${popularpathway.folder}") String folder) throws IOException {
        popularPathwayFolder = folder;
        getAvailableFiles();
    }


    // find a foamtree json file when give a year
    public File findFoamtreeFileFromMap(String year) throws IOException {

        File jsonFoamtreeFile = null;

        // NPE
        File csvFile = new File(popularPathwayFolder + "/" + "log" + "/" + year + "/" + "HSA-hits-" + year + ".csv");

        Map<File, File> allFiles = getAvailableFiles();

        if (allFiles.containsKey(csvFile)) {
            jsonFoamtreeFile = allFiles.get(csvFile);
        }
        return jsonFoamtreeFile;
    }


    public File getJsonFoamtreeFile(MultipartFile uploadFile, int year) throws IOException {

        File jsonFoamtreeFile;

        Map<File, File> allFiles = getAvailableFiles();

        Map<String, File> allFilesChecksum = new HashMap<>();

        File convertUploadFile = fileUploadService.convertFile(uploadFile);
        String uploadFileCode = DigestUtils.md5Hex(new FileInputStream(convertUploadFile));

        for (Map.Entry<File, File> entry : allFiles.entrySet()) {
            String checkSum = DigestUtils.md5Hex(new FileInputStream(entry.getKey()));
            allFilesChecksum.put(checkSum, entry.getValue());
        }

        if (allFilesChecksum.containsKey(uploadFileCode)) {
            jsonFoamtreeFile = allFilesChecksum.get(uploadFileCode);
        } else {
            File csvFile = fileUploadService.saveLogFileToServer(uploadFile, year);
            jsonFoamtreeFile = generateFoamtreeFile(csvFile, Integer.toString(year));
        }
        return jsonFoamtreeFile;
    }

    //todo rewrite 0217
    public File generateFoamtreeFile(File logFile, String year) throws IOException {


        Map<String, Integer> inputFileResult = logDataCSVParser.CSVParser(logFile.getAbsolutePath());

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();
        FoamtreeGenerator foamtreeGenerator = new FoamtreeGenerator();
        List<Foamtree> foamtreesWithLogData = foamtreeGenerator.getResults(inputFileResult, foamtrees);

        JsonSaver jsonSaver = new JsonSaver();
        String outputPath = popularPathwayFolder + "/" + "json" + "/" + year;
        File dirJson = new File(outputPath);
        if (!dirJson.exists())
            dirJson.mkdirs();

        File jsonFoamtreeFile = new File(outputPath + "/" + "HSA-hits-" + year + ".json");
        jsonSaver.writeToFile(jsonFoamtreeFile, foamtreesWithLogData);

        return jsonFoamtreeFile;
    }


    public Map<File, File> cacheFiles() throws IOException {

        Map<File, File> fileMap = new HashMap<>();

        String csvPath = popularPathwayFolder + "/" + "log";
        // String csvPath ="/Users/reactome/Reactome/popularpathways"+ "/" + "log";
        File logDir = new File(csvPath);
        String jsonPath = popularPathwayFolder + "/" + "json";
        File jsonDir = new File(jsonPath);

        //todo call once
        Collection<File> csvFiles = FileUtils.listFiles(logDir, new String[]{"csv"} , true);
        Collection<File> jsonFiles = FileUtils.listFiles(jsonDir, new String[]{"json"} , true);


        //.stream().filter(file -> Boolean.parseBoolean(FilenameUtils.getExtension("csv"))).collect(Collectors.toList());
        // is there a clearer way?
        for (File csvFile : csvFiles) {
            for (File jsonFile : jsonFiles) {
                if (FilenameUtils.getBaseName(csvFile.getName()).equals(FilenameUtils.getBaseName(jsonFile.getName()))) {
                    fileMap.put(csvFile, jsonFile);
                    break;
                }
            }
        }
        return fileMap;
    }


    public Map<File, File> getAvailableFiles() throws IOException {

        if (AVAILABLE_FILES == null) {
            AVAILABLE_FILES = cacheFiles();
        }
        return AVAILABLE_FILES;
    }

    // todo unused
//    public static File[] getFileList(String dirPath, String year, String suffix) {
//
//        File dir = new File(dirPath);
//
//        return dir.listFiles(new FilenameFilter() {
//            public boolean accept(File dir1, String name) {
//                return name.endsWith(year + "." + suffix);
//            }
//        });
//    }
//
//    public String getFileName(String dirPath, String year, String suffix) throws IOException {
//
//        String fileName = null;
//        File[] fileList = getFileList(dirPath, year, suffix);
//
//        if (fileList != null) {
//            for (File file : fileList) {
//                fileName = file.getName();
//            }
//        }
//        return fileName;
//    }

    // iterate the dir to find all files
//    public List<File> fetchFiles(File dir, String suffix) {
//        List<File> filesList = new ArrayList<>();
//        if (dir == null || dir.listFiles() == null) {
//            return filesList;
//        }
//
//        for (File fileInDir : dir.listFiles()) {
//            if (fileInDir.isFile() && fileInDir.getName().endsWith("." + suffix)) {
//                filesList.add(fileInDir);
//            } else {
//                filesList.addAll(fetchFiles(fileInDir, suffix));
//            }
//        }
//        return filesList;
//    }

}
