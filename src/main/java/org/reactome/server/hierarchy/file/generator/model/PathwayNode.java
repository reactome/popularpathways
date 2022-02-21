package org.reactome.server.hierarchy.file.generator.model;

import java.util.List;

public class PathwayNode {

    private String stId;
    private String url;
    private String name;
    private String species;
    private List<PathwayNode> subpathway;

    public PathwayNode() {
    }

    public PathwayNode(String stId, String name, String url, String species, List<PathwayNode> subpathway) {
        this.stId = stId;
        this.name = name;
        this.url = "https://reactome.org/PathwayBrowser/#/" + url;
        this.species = species;
        this.subpathway = subpathway;
    }

    public PathwayNode(String name, String stId, String species, List<PathwayNode> subpathway) {
        this.name = name;
        this.url = "https://reactome.org/PathwayBrowser/#/" + stId;
        this.species = species;
        this.subpathway = subpathway;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String stId) {
        this.url = "https://reactome.org/PathwayBrowser/#/" + stId ;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getStId() {
        return stId;
    }

    public String getName() {
        return name;
    }

    public List<PathwayNode> getsubpathway() {
        return subpathway;
    }

    public void setStId(String stId) {
        this.stId =  stId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setsubpathway(List<PathwayNode> subpathway) {
            this.subpathway = subpathway;
    }
}
