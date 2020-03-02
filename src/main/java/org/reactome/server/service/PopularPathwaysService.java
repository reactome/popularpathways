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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;


import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


@Component
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

    public PopularPathwaysService(@Value("${popularpathway.folder}") String folder) throws IOException {
        popularPathwayFolder = folder;
        getAvailableFiles();
    }

    /**
     * Get available files on the server, try to avoid generate the same json file when upload a exists log file
     *
     * @return
     * @throws IOException
     */
    public Map<File, File> getAvailableFiles() throws IOException {

        if (AVAILABLE_FILES == null) {
            AVAILABLE_FILES = cacheFiles();
        }
        return AVAILABLE_FILES;
    }

    /**
     *  stId -Age of a pathways as key and value pair
     * @return
     */
    public Map<String, Integer> getPathwayAge() {

        if (pathwayAge == null) {
            pathwayAge = generatePathwayAge();
        }
        return pathwayAge;
    }

    // find a foamtree json file when give a year
    public File findFoamtreeFileFromMapByYear(String year) throws IOException {

        File foamtreeJsonFile = null;

        // NPE
        File logFile = new File(popularPathwayFolder + "/" + "log" + "/" + year + "/" + "HSA-hits-" + year + ".csv");

        Map<File, File> logFilesAndJsonFiles = getAvailableFiles();

        if (logFilesAndJsonFiles.containsKey(logFile)) {
            foamtreeJsonFile = logFilesAndJsonFiles.get(logFile);
        }
        return foamtreeJsonFile;
    }

    /**
     * get a json foamtree file when upload a log file, if log file already existed(use md5 code to check), do not generate it.
     *
     * @param uploadFile
     * @param year
     * @return
     * @throws IOException
     */
    public File getJsonFoamtreeFile(MultipartFile uploadFile, int year) throws IOException {

        File jsonFoamtreeFile;

        //pair of existing csv files and foamtree json files
        Map<File, File> logFilesAndJsonFiles = getAvailableFiles();

        Map<String, File> md5CodeAndJsonFiles = new HashMap<>();

        String uploadFileCode = fileUploadService.getUploadFileMd5Code(uploadFile);

        //generate a new map which is the Md5Code as key and json file as value
        for (Map.Entry<File, File> entry : logFilesAndJsonFiles.entrySet()) {
            String checkSum = DigestUtils.md5Hex(new FileInputStream(entry.getKey()));
            md5CodeAndJsonFiles.put(checkSum, entry.getValue());
        }

        if (md5CodeAndJsonFiles.containsKey(uploadFileCode)) {
            jsonFoamtreeFile = md5CodeAndJsonFiles.get(uploadFileCode);
        } else {
            File csvFile = fileUploadService.saveLogFileToServer(uploadFile, year);
            jsonFoamtreeFile = generateFoamtreeFile(csvFile, Integer.toString(year));
        }
        return jsonFoamtreeFile;
    }


    /**
     * generate json foamtree File and save to server
     *
     * @param logFile
     * @param year
     * @return
     * @throws IOException
     */
    //todo rewrite 0217
    public File generateFoamtreeFile(File logFile, String year) throws IOException {

        Map<String, Integer> logFileResult = logDataCSVParser.CSVParser(logFile.getAbsolutePath());

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();
        FoamtreeGenerator foamtreeGenerator = new FoamtreeGenerator();

        // get stId-age pair
        Map<String, Integer> ageMap = getPathwayAge();
        List<Foamtree> foamtreesWithLogData = foamtreeGenerator.getResults(logFileResult, ageMap, foamtrees);

        JsonSaver jsonSaver = new JsonSaver();
        String outputPath = popularPathwayFolder + "/" + "json" + "/" + year;
        File dirJson = new File(outputPath);
        if (!dirJson.exists()) {
            dirJson.mkdirs();
        }

        File foamtreeJsonFile = new File(outputPath + "/" + "HSA-hits-" + year + ".json");
        jsonSaver.writeToFile(foamtreeJsonFile, foamtreesWithLogData);

        return foamtreeJsonFile;
    }


    /**
     * create a HashMap which is used for storing log file & json file pairs
     *
     * @return
     * @throws IOException
     */
    public Map<File, File> cacheFiles() {

        Map<File, File> fileMap = new HashMap<>();

        String csvPath = popularPathwayFolder + "/" + "log";
        File logDir = new File(csvPath);
        String jsonPath = popularPathwayFolder + "/" + "json";
        File jsonDir = new File(jsonPath);

        //todo call once
        Collection<File> csvFiles = FileUtils.listFiles(logDir, new String[]{"csv"}, true);
        Collection<File> jsonFiles = FileUtils.listFiles(jsonDir, new String[]{"json"}, true);

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

    /**
     * use @PostConstruct to call method after the initialization
     * generate a HashMap which is used for storing  stId & age pairs
     *
     * @return
     */
    @PostConstruct
    public Map<String, Integer> generatePathwayAge() {

        // create stId - age pair
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
                    // todo wired
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


    // todo unused for now
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
