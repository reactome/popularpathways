package org.reactome.server.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LogDataCSVParser {

    public Map<String, Integer> CSVParser(String filepath) {

        Map<String, Integer> inputFileResult = new HashMap<>();

        try {
            //File normalisedFile = normalizeLogFile(filepath);
            Reader reader = Files.newBufferedReader(Paths.get(filepath));
            // skip the header line
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreEmptyLines(true));
            for (CSVRecord record : csvParser) {
                // accessing columns by index
                if (record.get(0) != null && !record.get(0).isEmpty()
                    && record.get(1) != null && !record.get(1).isEmpty()) {
                    String stId = record.get(0).startsWith("R-HSA-") ? record.get(0) : "R-HSA-" + record.get(0);
                    String count = record.get(1).replace(",", "");
                    inputFileResult.put(stId, Integer.parseInt(count));
                }
            }
            csvParser.close();
            reader.close();
        } catch (IOException e) {
            e.getMessage();
        }
        return inputFileResult;
    }

    /**
     * Remove empty line from log file
     * @param filepath the file
     * @return the normalised file
     */
    private File normalizeLogFile(String filepath) throws IOException {
        File ret = new File(filepath);
        List<String> lines = FileUtils.readLines(new File(filepath));
        List<String> onlyLines = lines.stream().filter(l -> l != null && !l.isEmpty()).collect(Collectors.toList());
        FileUtils.writeLines(ret, onlyLines);
        return ret;
    }
}
