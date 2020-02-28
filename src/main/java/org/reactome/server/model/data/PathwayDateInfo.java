package org.reactome.server.model.data;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


public class PathwayDateInfo {
    private String stId;
    private String lastAuthored;
    private String lastReviewed;
    private String releaseDate;
    private Integer age;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private DateTimeFormatter releaseDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

    public Integer getAge(String lastAuthored, String lastReviewed, String releasedDate) {

        LocalDate finalDate = LocalDate.now();

        // lastAuthored and lastReviewed are null
        if (lastAuthored == null && lastReviewed == null) {
            finalDate = LocalDate.parse(releasedDate, releaseDateFormatter);
        }

        if (lastAuthored != null && !lastAuthored.isEmpty() && lastReviewed == null) {
            finalDate = LocalDate.parse(lastAuthored, formatter);
        }

        if (lastReviewed != null && !lastReviewed.isEmpty() && lastAuthored == null) {
            finalDate = LocalDate.parse(lastReviewed, formatter);
        }

        if (lastReviewed != null && !lastReviewed.isEmpty() &&
                lastAuthored != null && !lastAuthored.isEmpty()) {
            LocalDate authored = LocalDate.parse(lastAuthored, formatter);
            LocalDate reviewed = LocalDate.parse(lastReviewed, formatter);

            if (reviewed.isAfter(authored)) {
                finalDate = reviewed;
            } else {
                finalDate = authored;
            }
        }

        LocalDate start = LocalDate.of(finalDate.getYear(), finalDate.getMonth(), finalDate.getDayOfMonth());
        LocalDate end = LocalDate.now();
        age = Math.toIntExact(ChronoUnit.YEARS.between(start, end));

        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
