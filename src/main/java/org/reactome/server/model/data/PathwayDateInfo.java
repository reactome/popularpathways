package org.reactome.server.model.data;


import org.neo4j.driver.Record;
import org.reactome.server.graph.domain.result.CustomQuery;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class PathwayDateInfo implements CustomQuery  {
    private String stId;
    private String lastAuthored;
    private String lastReviewed;
    private String releaseDate;
    private Integer age;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        LocalDate lastAuthoredDate = null;
        LocalDate lastReviewedDate = null;
        LocalDate releasedDate = null;


        if (lastAuthored != null) {
            lastAuthoredDate = lastAuthored.contains(" ") ? LocalDate.parse(lastAuthored.substring(0, lastAuthored.indexOf(" ")), formatter) : LocalDate.parse(lastAuthored, formatter);
        }
        if (lastReviewed != null && lastReviewed.contains(" ")) {
            lastReviewedDate = lastReviewed.contains(" ")? LocalDate.parse(lastReviewed.substring(0, lastReviewed.indexOf(" ")), formatter): LocalDate.parse(lastReviewed, formatter);
        }
        if (released !=null) {
            releasedDate = released.contains(" ") ? LocalDate.parse(released.substring(0, released.indexOf(" ")), formatter): LocalDate.parse(released, formatter);
        }

        LocalDate finalDate = getLatest(getLatest(lastAuthoredDate, lastReviewedDate), releasedDate);

        if (finalDate == null) {
            finalDate = LocalDate.now();
        }

        LocalDate start = LocalDate.of(finalDate.getYear(), finalDate.getMonth(), finalDate.getDayOfMonth());
        LocalDate end = LocalDate.now();
        age = Math.toIntExact(ChronoUnit.YEARS.between(start, end));

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
