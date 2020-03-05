package org.reactome.server.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogDataCSVParser {

    public Map<String, Integer> CSVParser(String filepath) throws IOException {
        Map<String, Integer> inputFileResult = new HashMap<>();
        Reader reader = Files.newBufferedReader(Paths.get(filepath));
        // skip the header line
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        for (CSVRecord record : csvParser) {
            // accessing columns by index
            String stId = record.get(0).startsWith("R-HSA-") ? record.get(0) : "R-HSA-" + record.get(0);
            String count = record.get(1).replace(",", "");
            inputFileResult.put(stId, Integer.parseInt(count));
        }
        return inputFileResult;
    }
}
