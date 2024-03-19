package org.reactome.server.controller;

import org.apache.commons.io.FileUtils;
import org.reactome.server.service.PopularPathwaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Calendar.YEAR;

@Controller
public class FileUploadController {

    private PopularPathwaysService popularPathwaysService;

    @Autowired
    public void setPopularPathwaysService(PopularPathwaysService popularPathwaysService) {
        this.popularPathwaysService = popularPathwaysService;
    }

    @RequestMapping(value = "/uploadlog", method = RequestMethod.POST)
    public ModelAndView uploadFile(@RequestParam("logFile") MultipartFile file, @RequestParam("date") Date date) throws IOException {

        ModelAndView mav = new ModelAndView("index");
        // get year only
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(YEAR);

        // get foamtreeJosnFile if it doesn't exist then create it
        File jsonFoamtreeFile = popularPathwaysService.getJsonFoamtreeFile(file, year);

        // get available Year list
        List<String> yearList= popularPathwaysService.getYearList();

        String data;
        if (jsonFoamtreeFile != null) {
            // convert file to String
            data = FileUtils.readFileToString(jsonFoamtreeFile, String.valueOf(StandardCharsets.UTF_8));
            mav.addObject("data", data);
            mav.addObject("year", year);
            mav.addObject("yearList", yearList);
        } else {
            mav = new ModelAndView("uploadlog");
            mav.addObject("errormsg", "File is wrong! Please check your file and try again.");
        }
        return mav;
    }

    /**
     * date binding
     * in this way spring will know how to convert a String to a Date
     * binder method in controller to bind date values from jsp
     */
    @InitBinder
    public void intDate(WebDataBinder dataBinder) {
        dataBinder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
    }
}

