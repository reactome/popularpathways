package org.reactome.server.controller;

import org.apache.commons.io.FileUtils;
import org.reactome.server.service.FileUploadService;
import org.reactome.server.service.PopularPathwaysService;
import org.reactome.server.util.LogDataCSVParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Controller
public class FileUploadController {

    @Autowired
    PopularPathwaysService popularPathwaysService;

    @Autowired
    FileUploadService fileUploadService;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("date") Date date) throws IOException {

        // get year only
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int YEAR = calendar.get(Calendar.YEAR);

        // todo wrong
        fileUploadService.saveToServer(file);
        // save json file
        popularPathwaysService.saveFile(Integer.toString(YEAR));


        ModelAndView mav = new ModelAndView();

        // todo generate json file
        // we have the result file in /results, if no generate by saveFile
        // noted: 404 error ajax call when generate a new one
        // if resultFile is not empty, pass to Model and view
        String outputPath = "src/main/webapp/resources/results";
        File dir = new File(outputPath + "/" + YEAR);
        if(dir.exists()){
            File[] files = dir.listFiles();
            for (File file1 : files) {
                if (file1.length() != 0) {
                    System.out.println("Executable");
                    mav.addObject("file", popularPathwaysService.getOutputFileName(Integer.toString(YEAR)));
                    mav.addObject("fileSuccess", "File successfully uploaded!");
                    mav.setViewName("index");

                } else {
                    System.out.println("File id empty");
                }
            }
        }



/*        ModelAndView mav = new ModelAndView();
        mav.addObject("file", popularPathwaysService.getOutputFileName(Integer.toString(YEAR)));
        mav.addObject("fileSuccess", "File successfully uploaded!");
        mav.setViewName("index");
        return mav;*/

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

