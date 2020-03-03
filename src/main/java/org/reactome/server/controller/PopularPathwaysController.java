package org.reactome.server.controller;


import org.apache.commons.io.FileUtils;
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

    private PopularPathwaysService popularPathwaysService;

    @Autowired
    public void setPopularPathwaysService(PopularPathwaysService popularPathwaysService) {
        this.popularPathwaysService = popularPathwaysService;
    }

    public PopularPathwaysController() {
    }

    @RequestMapping(value = "/")
    public ModelAndView getIndex() throws IOException {

        Calendar calendar = new GregorianCalendar();
        String lastYear = String.valueOf(calendar.get(YEAR) - 1);

        Map<File, File> logFilesAndJsonFiles = popularPathwaysService.cacheFiles();
        File jsonFoamtreeFile = popularPathwaysService.findFoamtreeFileFromMapByYear(lastYear, logFilesAndJsonFiles);

        if (jsonFoamtreeFile == null) {
            File lastModifiedFile = popularPathwaysService.getLastModifiedFile();
            String data = FileUtils.readFileToString(lastModifiedFile, String.valueOf(StandardCharsets.UTF_8));
            ModelAndView mav = new ModelAndView("index");
            mav.addObject("data", data);
            mav.addObject("year", lastModifiedFile.getName().replaceAll("\\D+", ""));
            return mav;
        }

        //  Apache Commons IO convert file to String
        String data = FileUtils.readFileToString(jsonFoamtreeFile, String.valueOf(StandardCharsets.UTF_8));

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("data", data);
        mav.addObject("year", lastYear);
        return mav;
    }
}
