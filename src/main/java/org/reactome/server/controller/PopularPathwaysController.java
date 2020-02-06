package org.reactome.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
public class PopularPathwaysController {

    @RequestMapping(value = "/")
    public String getMessage(){
        return "index";
    }

}

