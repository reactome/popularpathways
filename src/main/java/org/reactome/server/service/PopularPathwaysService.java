package org.reactome.server.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.reactome.server.graph.exception.CustomQueryException;
import org.reactome.server.graph.service.AdvancedDatabaseObjectService;
import org.reactome.server.model.FoamtreeFactory;
import org.reactome.server.model.FoamtreeGenerator;
import org.reactome.server.model.data.Foamtree;
import org.reactome.server.graph.service.TopLevelPathwayService;
import org.reactome.server.model.data.PathwayDateInfo;
import org.reactome.server.util.LogDataCSVParser;
import org.reactome.server.util.JsonSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
@Service
public class PopularPathwaysService {

    private TopLevelPathwayService tlpService;
    private AdvancedDatabaseObjectService advancedDatabaseObjectService;
    private LogDataCSVParser logDataCSVParser;
    private FileUploadService fileUploadService;
    private static String popularPathwayFolder;
    private Map<File, File> AVAILABLE_FILES;
    private Map<String, Integer> pathwayAge;


    @Autowired
    public void setTlpService(TopLevelPathwayService tlpService) {
        this.tlpService = tlpService;
    }

    @Autowired
    public void setAdvancedDatabaseObjectService(AdvancedDatabaseObjectService advancedDatabaseObjectService) {
        this.advancedDatabaseObjectService = advancedDatabaseObjectService;
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

    public PopularPathwaysService(@Value("${popularpathway.folder}") String folder) {
        popularPathwayFolder = folder;
        getAvailableFiles();
    }

    /**
     * get available files on the server, to avoid generate the same json file when upload an existing log file
     */
    public Map<File, File> getAvailableFiles() {

        if (AVAILABLE_FILES == null) {
            AVAILABLE_FILES = cacheFiles();
        }
        return AVAILABLE_FILES;
    }

    /**
     * regenerate a csv and json file map after upload a new log file
     */
    public void refreshCachedFiles() {
        AVAILABLE_FILES = cacheFiles();
    }

    /**
     * @return a map stId and age as key value pair
     */
    public Map<String, Integer> getPathwayAge() {

        if (pathwayAge == null) {
            pathwayAge = generatePathwayAge();
        }
        return pathwayAge;
    }

    /**
     * find a foamtree json file when give a year
     */
    public File findFoamtreeFileByYear(String year) {

        File foamtreeJsonFile = null;

        Map<File, File> logFilesAndJsonFiles = getAvailableFiles();

        File logFile = new File(popularPathwayFolder + "/" + "log" + "/" + year + "/" + "HSA-hits-" + year + ".csv");

        if (logFilesAndJsonFiles.containsKey(logFile)) {
            foamtreeJsonFile = logFilesAndJsonFiles.get(logFile);
        }
        return foamtreeJsonFile;
    }

    /**
     * get a json foamtree file when upload a log file, if log file already existed(use md5 code to check), do not generate it.
     */
    public File getJsonFoamtreeFile(MultipartFile uploadFile, int year) throws IOException {

        File jsonFoamtreeFile;

        // existing csv files and foamtree json files as key value pair
        Map<File, File> logFilesAndJsonFiles = getAvailableFiles();

        Map<String, File> md5CodeAndJsonFiles = new HashMap<>();

        String uploadFileCode = fileUploadService.getUploadFileMd5Code(uploadFile);

        // generate a new map which is the Md5Code as key and json file as value
        for (Map.Entry<File, File> entry : logFilesAndJsonFiles.entrySet()) {
            String checkSum = DigestUtils.md5Hex(new FileInputStream(entry.getKey()));
            md5CodeAndJsonFiles.put(checkSum, entry.getValue());
        }

        if (md5CodeAndJsonFiles.containsKey(uploadFileCode)) {
            jsonFoamtreeFile = md5CodeAndJsonFiles.get(uploadFileCode);
        } else {
            File csvFile = fileUploadService.saveLogFileToServer(uploadFile, year);
            jsonFoamtreeFile = generateFoamtreeFile(csvFile, Integer.toString(year));
            refreshCachedFiles();
        }
        return jsonFoamtreeFile;
    }


    /**
     * generate json foamtree File and save to server
     *
     * @param logFile log file which is already saved to server
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File generateFoamtreeFile(File logFile, String year) throws IOException {

        Map<String, Integer> logFileResult = logDataCSVParser.CSVParser(logFile.getAbsolutePath());

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();
        FoamtreeGenerator foamtreeGenerator = new FoamtreeGenerator();

        // get stId-age pair
        Map<String, Integer> ageMap = getPathwayAge();
        List<Foamtree> foamtreesWithLogData = foamtreeGenerator.getResults(logFileResult, ageMap, foamtrees);

        // get path
        String outputPath = popularPathwayFolder + "/" + "json" + "/" + year;
        File dirJson = new File(outputPath);
        if (!dirJson.exists()) {
            dirJson.mkdirs();
        }

        File foamtreeJsonFile = new File(outputPath + "/" + "HSA-hits-" + year + ".json");
        JsonSaver jsonSaver = new JsonSaver();
        jsonSaver.writeToFile(foamtreeJsonFile, foamtreesWithLogData);

        return foamtreeJsonFile;
    }


    /**
     * create a HashMap which is used for storing log file and json file pairs
     *
     * @return a map
     */
    public Map<File, File> cacheFiles() {

        Map<File, File> fileMatchMap = new HashMap<>();

        String csvPath = popularPathwayFolder + "/" + "log";
        File logDir = new File(csvPath);
        String jsonPath = popularPathwayFolder + "/" + "json";
        File jsonDir = new File(jsonPath);

        Collection<File> csvFiles = FileUtils.listFiles(logDir, new String[]{"csv"}, true);
        Collection<File> jsonFiles = FileUtils.listFiles(jsonDir, new String[]{"json"}, true);

        for (File csvFile : csvFiles) {
            for (File jsonFile : jsonFiles) {
                if (FilenameUtils.getBaseName(csvFile.getName()).equals(FilenameUtils.getBaseName(jsonFile.getName()))) {
                    fileMatchMap.put(csvFile, jsonFile);
                    break;
                }
            }
        }
        return fileMatchMap;
    }

    /**
     * use @PostConstruct to call method after the initialization
     * generate a HashMap which is used for storing  stId and age pairs
     */
    @PostConstruct
    public Map<String, Integer> generatePathwayAge() {

        Map<String, Integer> pathwayAge = new HashMap<>();

        try {
            String query = "MATCH (p:Pathway{speciesName: 'Homo sapiens'})" +
                    "OPTIONAL MATCH (p) -[:authored]-(a:InstanceEdit)" +
                    "OPTIONAL MATCH (p) -[:reviewed]-(r:InstanceEdit)" +
                    "RETURN p.stId AS stId, max(a.dateTime) AS lastAuthored, max(r.dateTime) AS lastReviewed, p.releaseDate AS releaseDate";
            Collection<PathwayDateInfo> pdis = advancedDatabaseObjectService.getCustomQueryResults(PathwayDateInfo.class, query);

            for (PathwayDateInfo pdi : pdis) {
                Integer age = pdi.getAge(pdi.getLastAuthored(), pdi.getLastReviewed(), pdi.getReleaseDate());
                if (age != null) {
                    // save stId and age as key and value pair
                    pathwayAge.put(pdi.getStId(), age);
                } else {
                    // no age found
                    pathwayAge.put(pdi.getStId(), -1);
                }
            }
        } catch (CustomQueryException e) {
            e.printStackTrace();
        }

        // get the highest and lowest value
//        int max = Collections.max(pathwayAge.values());
//        int min = Collections.min(pathwayAge.values());

        return pathwayAge;
    }

    /**
     * get last modified file from foamtree json folder
     *
     * @return the last modified foamtree json file
     */
    public File getLastModifiedFile() {
        String jsonPath = popularPathwayFolder + "/" + "json";
        Collection<File> jsonFiles = FileUtils.listFiles(new File(jsonPath), new String[]{"json"}, true);
        List<File> sortJsonFiles = jsonFiles.stream().sorted(Comparator.comparingLong(File::lastModified).reversed()).collect(Collectors.toList());
        return sortJsonFiles.get(0);
    }
}
