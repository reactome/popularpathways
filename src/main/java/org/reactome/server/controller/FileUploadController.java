package org.reactome.server.controller;

import org.reactome.server.model.data.Foamtree;
import org.reactome.server.service.FileUploadService;
import org.reactome.server.service.PopularPathwaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.*;

import static java.util.Calendar.YEAR;

@Controller
public class FileUploadController {

    @Autowired
    PopularPathwaysService popularPathwaysService;

    @Autowired
    FileUploadService fileUploadService;


    @RequestMapping(value = "/uploadlog", method = RequestMethod.POST)
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") Date date) throws IOException {

        // get year only
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(YEAR);

        fileUploadService.saveLogFileToServer(file, year);

        // save json file
        File jsonFoamtreeFile = popularPathwaysService.generateAndSaveFoamtreeFile(Integer.toString(year));

        String outputPath = "src/main/webapp/resources/results";
        String foamtreeFileSuffix = "json";


        System.out.println(jsonFoamtreeFile.length());

        System.out.println("1");
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("file", jsonFoamtreeFile.getName());
        //mav.addObject("file", popularPathwaysService.getFileName(outputPath,"2020",foamtreeFileSuffix));
        System.out.println("2");
        mav.addObject("fileSuccess", "File successfully uploaded!");
        System.out.println("3");
        mav.addObject("year", year);
        System.out.println("4");

        return mav;

    }

    // date binding
    // in this way spring will know how to convert a String to a Date
    // initbinder method in controller to bind date values from jsp.
    @InitBinder
    public void intDate(WebDataBinder dataBinder) {
        dataBinder.addCustomFormatter(new DateFormatter("yyyy-MM-dd"));
    }
}

