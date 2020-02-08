package org.reactome.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactome.server.model.data.Foamtree;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
public class SaveToJsonFile {

    private ObjectMapper mapper = new ObjectMapper();

    public void writeToFile(String filePath, List<Foamtree> foamtrees) throws IOException {
        File file = new File(filePath);
        mapper.writeValue(file, foamtrees);
    }
}
