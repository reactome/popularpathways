package org.reactome.server.controller;

import org.apache.commons.io.FileUtils;
import org.reactome.server.service.FileUploadService;
import org.reactome.server.service.PopularPathwaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


import java.io.*;
import java.nio.charset.StandardCharsets;
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

        Map<File, File> allFiles = PopularPathwaysController.getAvailableFiles();
        // get year only
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int year = calendar.get(YEAR);

        // save log file to input
        fileUploadService.saveLogFileToServer(file, year);
        //fileUploadService.saveLogFileToServerWithCheck(file, year);

        // get the saved file
        File jsonFoamtreeFile = popularPathwaysService.generateAndSaveFoamtreeFile(Integer.toString(year));
        //File jsonFoamtreeFile = popularPathwaysService.getFoamtreeFileWithCheck(Integer.toString(year));

        //File jsonFile = new File(jsonFoamtreeFile.getAbsolutePath());
        String data = FileUtils.readFileToString(jsonFoamtreeFile, String.valueOf(StandardCharsets.UTF_8));


        ModelAndView mav = new ModelAndView("index");
        mav.addObject("file", jsonFoamtreeFile.getName());
        mav.addObject("fileSuccess", "File successfully uploaded!");
        mav.addObject("data", data);
        mav.addObject("year", year);

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

