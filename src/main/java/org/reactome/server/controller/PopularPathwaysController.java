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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getIndex() throws IOException {

        Calendar calendar = new GregorianCalendar();
        int lastYear = (calendar.get(YEAR) - 1);

        ModelAndView mav = new ModelAndView("index");

        File jsonFoamtreeFile = popularPathwaysService.findFoamtreeFileByYear(lastYear);
        List<String> yearList= popularPathwaysService.getYearList();
        String data;

        if (jsonFoamtreeFile != null) {
            data = FileUtils.readFileToString(jsonFoamtreeFile, String.valueOf(StandardCharsets.UTF_8));
            mav.addObject("year", lastYear);
        } else {
            // visualize the last modified file when no default file found
            File lastModifiedFile = popularPathwaysService.getLastModifiedFile();
            // Apache Commons IO convert file to String
            data = FileUtils.readFileToString(lastModifiedFile, String.valueOf(StandardCharsets.UTF_8));
            mav.addObject("year", lastModifiedFile.getName().replaceAll("\\D+", ""));
        }
        mav.addObject("yearList", yearList);
        mav.addObject("data", data);
        return mav;
    }

    @RequestMapping(value = "/load/{yearIndex}", method = RequestMethod.GET)
    public ModelAndView getYearIndex(@PathVariable("yearIndex") int year) throws IOException {

        ModelAndView mav = new ModelAndView("index");
        File jsonFoamtreeFile = popularPathwaysService.findFoamtreeFileByYear(year);

        List<String> yearList= popularPathwaysService.getYearList();

        String data = FileUtils.readFileToString(jsonFoamtreeFile, String.valueOf(StandardCharsets.UTF_8));
        mav.addObject("year", year);
        mav.addObject("yearList", yearList);
        mav.addObject("data", data);
        return mav;
    }
}
