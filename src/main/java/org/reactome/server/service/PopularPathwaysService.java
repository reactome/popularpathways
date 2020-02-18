package org.reactome.server.service;

import org.apache.commons.codec.digest.DigestUtils;
import org.reactome.server.controller.PopularPathwaysController;
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
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

@Component
public class PopularPathwaysService {

    @Autowired
    private TopLevelPathwayService tlpService;

    @Autowired
    private LogDataCSVParser logDataCSVParser;

    @Autowired
    private FileUploadService fileUploadService;

    @Value("${popularpathway.folder}")
    private String popularPathwayFolder;


    public String getPopularPathwayFolder() {
        return popularPathwayFolder;
    }

    public PopularPathwaysService() throws IOException {
    }

    //todo keep it for now, deleted it later
    public File generateAndSaveFoamtreeFile(String year) throws IOException {

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();

        String logFileSuffix = "csv";
        String dirLog = popularPathwayFolder + "/" + "log" + "/" + year;
        System.out.println(dirLog);
        String inputFileName = getFileName(dirLog, year, logFileSuffix);
        Map<String, Integer> inputFileResult = logDataCSVParser.CSVParser(dirLog + "/" + inputFileName);

        // todo do not generate FOAMTREE data every time. Use md5 checksum...
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

    // find a foamtree json file when give a year
    public File findFoamtreeFile(String year) throws IOException {

        File jsonFoamtreeFile = null;

        // NPE
        File csvFile = new File(popularPathwayFolder + "/" + "log" + "/" + year + "/" + "HSA-hits-" + year + ".csv");

        Map<File, File> allFiles = PopularPathwaysController.getAvailableFiles();

        if (allFiles.containsKey(csvFile)) {
            jsonFoamtreeFile = allFiles.get(csvFile);
        }
        return jsonFoamtreeFile;
    }

    //todo rewrite 0217
    public File generateFoamtreeFile(File file, String year) throws IOException {

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();
        System.out.println(file.getAbsolutePath());
        Map<String, Integer> inputFileResult = logDataCSVParser.CSVParser(file.getAbsolutePath());

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

    public File getJsonFoamtreeFile(MultipartFile file, int year) throws IOException {

        File jsonFoamtreeFile;

        Map<File, File> allFiles = PopularPathwaysController.getAvailableFiles();

        Map<String, File> allFilesChecksum = new HashMap<>();

        File uploadFile = fileUploadService.convertFile(file);
        String uploadFileCode = DigestUtils.md5Hex(new FileInputStream(uploadFile));


        for (Map.Entry<File, File> entry : allFiles.entrySet()) {
            String checkSum = DigestUtils.md5Hex(new FileInputStream(entry.getKey()));
            allFilesChecksum.put(checkSum, entry.getValue());
        }

        if (allFilesChecksum.containsKey(uploadFileCode)) {
            jsonFoamtreeFile = allFilesChecksum.get(uploadFileCode);
            return jsonFoamtreeFile;
        } else {
            fileUploadService.saveLogFileToServer(file, year);
            jsonFoamtreeFile = generateFoamtreeFile(uploadFile, Integer.toString(year));
        }
        return jsonFoamtreeFile;
    }

    public static File[] getFileList(String dirPath, String year, String suffix) {

        File dir = new File(dirPath);

        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(year + "." + suffix);
            }
        });
        return fileList;
    }

    public String getFileName(String dirPath, String year, String suffix) throws IOException {

        String fileName = null;
        File[] fileList = getFileList(dirPath, year, suffix);

        if (fileList != null) {
            for (File file : fileList) {
                fileName = file.getName();
            }
        }
        return fileName;
    }
}
