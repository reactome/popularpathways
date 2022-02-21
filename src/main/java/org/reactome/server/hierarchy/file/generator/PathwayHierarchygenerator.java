package org.reactome.server.hierarchy.file.generator;


import com.github.opendevl.JFlat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * This generator will flat the whole json object to csv file by reading json file through a URL.
 * Use controller to generate CSV file but keep this method at this moment.
 */
public class PathwayHierarchygenerator {

    static String jsonFile = "https://reactome.org/ContentService/data/eventsHierarchy/9606";
    static String csvFile = "/Users/path/to/json.csv";

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    public static void main(String[] args) throws IOException {
        URL urlObject = new URL(jsonFile);
        URLConnection urlConnection = urlObject.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        String data = readFromInputStream(inputStream);
        //flat json
        JFlat flatMe = new JFlat(data);
        //get the 2D representation of JSON document, won't work after removing this line
        List<Object[]> json2csv = flatMe.json2Sheet().getJsonAsSheet();
        //write the 2D representation in csv format
        flatMe.write2csv(csvFile);
    }
}