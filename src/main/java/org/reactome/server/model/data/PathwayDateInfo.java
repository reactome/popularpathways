package org.reactome.server.model.data;


import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class PathwayDateInfo implements CustomQuery  {
    private String stId;
    private String lastAuthored;
    private String lastReviewed;
    private String releaseDate;
    private Integer age;


    public String getStId() {
        return stId;
    }

    public void setStId(String stId) {
        this.stId = stId;
    }

    public String getLastAuthored() {
        return lastAuthored;
    }

    public void setLastAuthored(String lastAuthored) {
        this.lastAuthored = lastAuthored;
    }

    public String getLastReviewed() {
        return lastReviewed;
    }

    public void setLastReviewed(String lastReviewed) {
        this.lastReviewed = lastReviewed;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releasedDate) {
        this.releaseDate = releasedDate;
    }


    public Integer getAge(String lastAuthored, String lastReviewed, String released) {

        LocalDate lastAuthoredDate = lastAuthored != null ? parseDate(lastAuthored) : null;
        LocalDate lastReviewedDate = lastReviewed != null ? parseDate(lastReviewed) : null;
        LocalDate releasedDate = released != null ? parseDate(released) : null;

        LocalDate finalDate = getLatest(getLatest(lastAuthoredDate, lastReviewedDate), releasedDate) != null ? getLatest(getLatest(lastAuthoredDate, lastReviewedDate), releasedDate) : LocalDate.now();

        LocalDate current = LocalDate.now();
        age = current.getYear() - finalDate.getYear();
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * Safely compare two dates, null being considered "greater" than a Date
     * @return the earliest of the two
     */
    public static LocalDate getLatest(LocalDate dateA, LocalDate dateB) {
        return dateA == null ? dateB : (dateB == null ? dateA : (dateA.isAfter(dateB) ? dateA : dateB));
    }

    public static LocalDate parseDate(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.contains(" ") ? LocalDate.parse(date.substring(0, date.indexOf(" ")), formatter) : LocalDate.parse(date, formatter);
    }


    @Override
    public CustomQuery build(Record r) {
        PathwayDateInfo pathwayDateInfo = new PathwayDateInfo();
        pathwayDateInfo.setStId(r.get("stId").asString(null));
        pathwayDateInfo.setLastAuthored(r.get("lastAuthored").asString(null));
        pathwayDateInfo.setLastReviewed(r.get("lastReviewed").asString(null));
        pathwayDateInfo.setReleaseDate(r.get("releaseDate").asString(null));
        pathwayDateInfo.setAge(getAge(r.get("lastAuthored").asString(null), r.get("lastAuthored").asString(null), r.get("releaseDate").asString(null)));
        return pathwayDateInfo;
    }
}
