package org.reactome.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactome.server.model.data.Foamtree;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class JsonSaver {

    public void writeToFile(File file, List<Foamtree> foamtrees) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(file, foamtrees);
    }
}
