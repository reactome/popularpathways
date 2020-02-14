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

    @Value("${popularpathway.log.folder}")
    private String popularPathwayLogFolder;


    public String getPopularPathwayLogFolder() {
        return popularPathwayLogFolder;
    }

    public PopularPathwaysService() throws IOException {
    }

    //todo rewrite
    public File generateAndSaveFoamtreeFile (String year) throws IOException {

        FoamtreeFactory foamtreeFactory = new FoamtreeFactory(tlpService);
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();

        String logFileSuffix = "csv";
        String dirLog = popularPathwayLogFolder + "/" + year;
        String inputFileName = getFileName(dirLog, year, logFileSuffix);
        Map<String, Integer> inputFileResult = logDataCSVParser.CSVParser(dirLog + "/" + inputFileName);

        FoamtreeGenerator foamtreeGenerator = new FoamtreeGenerator();
        List<Foamtree> foamtreesWithLogData = foamtreeGenerator.getResults(inputFileResult, foamtrees);

        JsonSaver jsonSaver = new JsonSaver();
        String outputPath = "src/main/webapp/resources/results";
        File jsonFoamtreeFile = new File(outputPath + "/" + "HSA-hits-" + year + ".json");
        jsonSaver.writeToFile(jsonFoamtreeFile, foamtreesWithLogData);

        return jsonFoamtreeFile;
    }

    // todo rewrite
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

        if (fileList != null){
            for (File file : fileList) {
                fileName = file.getName();
            }
        }

        return fileName;
    }
}
