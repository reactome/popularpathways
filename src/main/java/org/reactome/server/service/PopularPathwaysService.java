package org.reactome.server.service;

import org.reactome.server.model.FoamtreeFactory;
import org.reactome.server.model.FoamtreeGenerator;
import org.reactome.server.model.data.Foamtree;
import org.reactome.server.graph.service.TopLevelPathwayService;
import org.reactome.server.util.LogDataCSVParser;
import org.reactome.server.util.JsonSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
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
    private JsonSaver jsonSaver;

    @Value("${popularpathway.log.folder}")
    private String popularPathwayLogFolder;

    public String getPopularPathwayLogFolder() {
        return popularPathwayLogFolder;
    }

    private String outputPath = "src/main/webapp/resources/results";

    public PopularPathwaysService() throws IOException {
    }

    // todo a parameter
//     Date date = new Date();
//    Calendar calendar = new GregorianCalendar();
//    calendar.setTime(date);
//    int year = calendar.get(Calendar.YEAR);
    String YEAR = "2018";

    // todo rewrite
    public static File[] getInputFileList(String dirPath, String year) {

        File dir = new File(dirPath + "/" + year);

        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(year + ".csv");
            }
        });
        return fileList;
    }

    public String getInputFileName(String dirPath, String year) throws IOException {

        String fileName = null;
        File[] fileList = getInputFileList(dirPath, year);

        for (File file : fileList) {
            fileName = file.getName();
        }
        return fileName;
    }

    //todo rewrite
    public void saveFile(String year) throws IOException {

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();

        String inputFileName = getInputFileName(popularPathwayLogFolder, year);
        Map<String, Integer> inputFileResult = logDataCSVParser.CSVParser(popularPathwayLogFolder + "/" + year + "/" + inputFileName);

        FoamtreeGenerator foamtreeGenerator = new FoamtreeGenerator();
        List<Foamtree> foamtreesWithLogData = foamtreeGenerator.getResults(inputFileResult, foamtrees);

        jsonSaver.writeToFile(outputPath + "/" + "HSA-hits-" + year + ".json", foamtreesWithLogData);
    }


    public File[] getOutputFileList(String dirPath, String year) {

        //todo to check duplicate files?
        // dictionary is null
        File dir = new File(dirPath);

        System.out.println(year);
        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(year + ".json");
            }
        });
        return fileList;
    }

    public File[] getOutputFileListVer2(String dirPath, String year) throws IOException {

        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        assert files != null;
        Boolean createFile = false;

        for (File file : files) {
            //wrong
            //  if (!file.getName().endsWith(year + ".json")) {
            if (!file.getName().contains(year + ".json")) {
                System.out.println(file.getName());
                System.out.println("before created");
                //saveFile(year);
                System.out.println("after created");
            }
        }

        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(year + ".json");
            }
        });
        return fileList;
    }


    public String getOutputFileName(String year) throws IOException {

        String fileName = null;
        //File[] fileList = getOutputFileList("src/main/webapp/resources/results", year);
        File[] fileList = getOutputFileList("src/main/webapp/resources/results", year);

        for (File file : fileList) {
            fileName = file.getName();
        }
        return fileName;
    }
}
