package org.reactome.server.hierarchy.file.generator.controller;


import com.github.opendevl.JFlat;
import com.google.gson.Gson;
import org.reactome.server.graph.service.HierarchyService;
import org.reactome.server.graph.service.helper.PathwayBrowserNode;
import org.reactome.server.hierarchy.file.generator.model.PathwayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This controller will generate a csv file base on the value we want by reusing the HierarchyService in graph-core
 */
@Controller
public class HierarchyGeneratorController {

    static String CSV_FILE = "/Users/path/to/json.csv";
    private final HierarchyService eventHierarchyService;

    @Autowired
    public HierarchyGeneratorController(HierarchyService eventHierarchyService) {
        this.eventHierarchyService = eventHierarchyService;
    }

    @RequestMapping(value = "/pathwaylist", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getFile() throws Exception {

        Collection<PathwayBrowserNode> pathwayBrowserNodes = eventHierarchyService.getEventHierarchy("9606", false).stream().sorted().collect(Collectors.toList());
        Collection<PathwayNode> pathwayNodes = new ArrayList<>();

        for (PathwayBrowserNode p : pathwayBrowserNodes) {
            PathwayNode pathwayNode = new PathwayNode();
            //  pathwayNode.setStId(p.getStId());
            pathwayNode.setName(p.getName());
            pathwayNode.setUrl(p.getStId());
            pathwayNode.setSpecies(p.getSpecies());
            List<PathwayNode> children = convert(p.getChildren());
            pathwayNode.setsubpathway(children);
            pathwayNodes.add(pathwayNode);
        }
        //convert a list of objects to JSON
        String json = new Gson().toJson(pathwayNodes);

        //flat json
        JFlat flatMe = new JFlat(json);
        //get the 2D representation of JSON document, won't work after removing this line
        List<Object[]> json2csv = flatMe.json2Sheet().getJsonAsSheet();
        //write the 2D representation in csv format
        flatMe.json2Sheet().headerSeparator("_").write2csv(CSV_FILE);
        return "done!";
    }

    public static List<PathwayNode> convert(Set<PathwayBrowserNode> children) {
        return children == null ? null :
                children.stream()
                        .filter(r -> r.getType().equals("Pathway"))
                        .map(r -> new PathwayNode(r.getName(), r.getStId(), r.getSpecies(), convert(r.getChildren())))
                        .collect(Collectors.toList());
    }
}
