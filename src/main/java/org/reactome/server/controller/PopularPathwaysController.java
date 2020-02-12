package org.reactome.server.controller;


import org.reactome.server.service.PopularPathwaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
public class PopularPathwaysController {

    @Autowired
    PopularPathwaysService popularPathwaysService;


    private String outputPath = "src/main/webapp/resources/results";
    private String foamtreeFileSuffix = "json";
    private String defaultYear = "2017";

    //File jsonFoamtreeFile = popularPathwaysService.generateAndSaveFoamtreeFile(defaultYear);

    public PopularPathwaysController() {
    }

    @RequestMapping(value = "/")
    public ModelAndView getIndex() throws IOException {
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("file", popularPathwaysService.getFileName(outputPath, defaultYear, foamtreeFileSuffix));
        mav.addObject("year", defaultYear);
        return mav;
    }
}

