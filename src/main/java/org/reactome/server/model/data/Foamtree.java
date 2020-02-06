package org.reactome.server.model.data;

import org.reactome.server.graph.domain.model.Pathway;

import java.util.ArrayList;
import java.util.List;


public class Foamtree {

    private Long dbId;
    private String stId;
    private String label;
    private int weight;
    private String url;
    private int hits;
    private List<Foamtree> groups;

    public Foamtree() {
    }

    public Foamtree(Pathway node) {
        this.dbId = node.getDbId();
        this.stId = node.getStId();
        this.label = node.getDisplayName();
        this.url = "/PathwayBrowser/#/" + node.getStId();
    }

    public Long getDbId() {
        return dbId;
    }

    public void setDbId(Long dbId) {
        this.dbId = dbId;
    }

    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public List<Foamtree> getGroups() {
        return groups;
    }

    public void setGroups(List<Foamtree> groups) {
        this.groups = groups;
    }

    public void addGroup(Foamtree group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }
}
