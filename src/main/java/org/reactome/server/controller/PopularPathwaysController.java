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

@Controller
public class PopularPathwaysController {

    @Autowired
    PopularPathwaysService popularPathwaysService;

    public PopularPathwaysController() {
    }

    @RequestMapping(value = "/")
    public ModelAndView getIndex() throws IOException {

        String defaultYear = "2019";
        File jsonFoamtreeFile = popularPathwaysService.generateAndSaveFoamtreeFile(defaultYear);

       /* ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(jsonFoamtreeFile);*/

        File jsonFile = new File(jsonFoamtreeFile.getAbsolutePath());

        //  Apache Commons IO
        String data = FileUtils.readFileToString(jsonFile, String.valueOf(StandardCharsets.UTF_8));

        ModelAndView mav = new ModelAndView("index");
        mav.addObject("data", data);
        mav.addObject("year", defaultYear);
        return mav;
    }
}

