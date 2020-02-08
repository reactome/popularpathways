package org.reactome.server.controller;


import org.reactome.server.service.PopularPathwaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class PopularPathwaysController {

    @Autowired
    PopularPathwaysService popularPathwaysService;

    @RequestMapping(value = "/")
    public ModelAndView getIndex() throws IOException {
        ModelAndView mav = new ModelAndView();
        mav.addObject("file", popularPathwaysService.getFileName());
        mav.setViewName("index");
        return mav;
    }
}

