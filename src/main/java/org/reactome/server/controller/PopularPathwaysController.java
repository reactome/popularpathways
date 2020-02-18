package org.reactome.server.controller;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.reactome.server.service.PopularPathwaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Calendar.YEAR;

@Controller
public class PopularPathwaysController {

    @Autowired
    PopularPathwaysService popularPathwaysService;

    // AVAILABLE_FILES
    public static Map<File, File> AVAILABLE_FILES;

    public PopularPathwaysController() throws IOException {
        getAvailableFiles();
    }

    //todo path
    public static Map<File, File> cacheFiles() throws IOException {

        Map<File, File> fileMap = new HashMap<>();

        //String csvPath = popularPathwaysService.getPopularPathwayFolder()+ "/" + "log";
        String csvPath = "/Users/reactome/Reactome/popularpathways/log";
        File logDir = new File(csvPath);
        //String jsonPath = popularPathwaysService.getPopularPathwayFolder() + "/" + "json";
        String jsonPath = "/Users/reactome/Reactome/popularpathways/json";
        File jsonDir = new File(jsonPath);

        List<File> csvFiles = fetchFiles(logDir, "csv");
        List<File> jsonFiles = fetchFiles(jsonDir, "json");

        // is there a clearer way?
        for (File csvFile : csvFiles) {
            for (File jsonFile : jsonFiles) {
                if (FilenameUtils.getBaseName(csvFile.getName()).equals(FilenameUtils.getBaseName(jsonFile.getName()))) {
                    fileMap.put(csvFile, jsonFile);
                }
            }
        }
        return fileMap;
    }

    @RequestMapping(value = "/")
    public ModelAndView getIndex() throws IOException {


        Calendar calendar = new GregorianCalendar();
        String currentYear = String.valueOf(calendar.get(YEAR));

        // todo it is wrong
        File jsonFoamtreeFile = popularPathwaysService.findFoamtreeFile(currentYear);
        //File jsonFoamtreeFile = popularPathwaysService.generateAndSaveFoamtreeFile(defaultYear);

        //  Apache Commons IO convert file to String
        String data = FileUtils.readFileToString(jsonFoamtreeFile, String.valueOf(StandardCharsets.UTF_8));

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("data", data);
        mav.addObject("year", currentYear);
        return mav;
    }

    public static Map<File, File> getAvailableFiles() throws IOException {
        if (AVAILABLE_FILES == null) {
            AVAILABLE_FILES = cacheFiles();
        }
        return AVAILABLE_FILES;
    }

    // iterate the dir to find all files
    public static List<File> fetchFiles(File dir, String suffix) {
        List<File> filesList = new ArrayList<>();
        if (dir == null || dir.listFiles() == null) {
            return filesList;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile() && file.getName().endsWith("." + suffix)) {
                filesList.add(file);
            } else {
                filesList.addAll(fetchFiles(file, suffix));
            }
        }
        return filesList;
    }
}
