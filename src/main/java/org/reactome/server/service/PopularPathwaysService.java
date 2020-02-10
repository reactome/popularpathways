package org.reactome.server.service;

import org.reactome.server.model.FoamtreeFactory;
import org.reactome.server.model.FoamtreeGenerator;
import org.reactome.server.model.data.Foamtree;
import org.reactome.server.util.LogDataCSVParser;
import org.reactome.server.util.JsonSaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PopularPathwaysService {

    @Autowired
    private FoamtreeFactory foamtreeFactory;
    @Autowired
    private FoamtreeGenerator foamtreeGenerator;
    @Autowired
    private LogDataCSVParser logDataCSVParser;
    @Autowired
    private JsonSaver jsonSaver;

    private String inputFilePath = "src/main/resources/log-files/HSA-hits-2018.csv";
    private String outputPath = "src/main/webapp/resources/results/test.json";
    private List<Foamtree> foamtreesWithLogData;

    private void saveFile() throws IOException {
        List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();
        Map<String, Integer> inputFileResult = logDataCSVParser.CSVParser(inputFilePath);
        foamtreesWithLogData = foamtreeGenerator.getResults(inputFileResult, foamtrees);
        jsonSaver.writeToFile(outputPath, foamtreesWithLogData);
    }
    
    private static File[] getFileList(String dirPath) {

        //todo duplicate files?
        // dictionary is null
        File dir = new File(dirPath);

        File[] fileList = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith("0204.json");
            }
        });
        return fileList;
    }

    public String getFileName() throws IOException {

        //File file = new File("src/main/webapp/resource/result/2018-log-data-all-pathways-0204.json");
        //saveFile();
        //return file.getName();

        //0208
        //1. allow user to upload or save all log files on server?
        //2.

        String fileName = null;
        File[] fileList = getFileList("src/main/webapp/resources/results");

        for (File file : fileList) {
            fileName = file.getName();
        }
        return fileName;
    }
}
