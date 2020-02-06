package org.reactome.server.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.reactome.server.graph.utils.ReactomeGraphCore;
import org.reactome.server.model.data.Foamtree;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FoamtreeGenerator {


    /**
     * Add hits value to foamtree, execute only one Foamtree
     *
     * @param inputFileResult
     * @param foamtree
     * @return
     */
    private  void addHitsToFoamtree(Map<String, Integer> inputFileResult, Foamtree foamtree) {

        if (!inputFileResult.isEmpty()) {
            if (inputFileResult.containsKey(foamtree.getStId())) {
                foamtree.setHits(inputFileResult.get(foamtree.getStId()));
                foamtree.setWeight(foamtree.getHits());
            } else {
                foamtree.setHits(1);
            }
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addHitsToFoamtree(inputFileResult, foamtreeInGroups);
                }
            }
        }
    }

    private void addWeightoFoamtree(Map<String, Integer> inputFileResult, Foamtree foamtree) {

        if (!inputFileResult.isEmpty()) {
            if (inputFileResult.containsKey(foamtree.getStId())) {
                foamtree.setWeight(inputFileResult.get(foamtree.getStId()));
            } else {
                foamtree.setWeight(1);
            }
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addWeightoFoamtree(inputFileResult, foamtreeInGroups);
                }
            }
        }
    }

    private void addWeightToFoamtrees(Map<String, Integer> inputFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addWeightoFoamtree(inputFileResult, foamtree);
        }
    }

    /**
     * Add hits value to Foamtree list
     *
     * @param inputFileResult
     * @param foamtrees
     */
    private void addHitsToFoamtrees(Map<String, Integer> inputFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addHitsToFoamtree(inputFileResult, foamtree);
        }
    }

    /**
     * Sum sub groups weight to parent,execute only one Foamtree
     *
     * @param foamtree
     */
    private  void sumFoamtreeWeight(Foamtree foamtree) {
        List<Foamtree> groups = foamtree.getGroups();
        if (groups != null) {
            // weight = current hits +  all child weights
            int weight = foamtree.getHits();
            for (Foamtree foamtreeInGroups : groups) {
                sumFoamtreeWeight(foamtreeInGroups);
                weight += foamtreeInGroups.getWeight();
            }
            foamtree.setWeight(weight);
        } else {
            // no child: weight = current hits
            foamtree.setWeight(foamtree.getHits());
        }
    }

    /**
     * Sum sub groups weight to parent in foamtree list
     *
     * @param foamtrees
     */
    private  void sumFoamtreesWeight(List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            sumFoamtreeWeight(foamtree);
        }
    }

    /**
     * Save log date to a HashMap form a file
     *
     * @param filepath
     * @return
     * @throws IOException
     */
    static Map<String, Integer> CSVParser(String filepath) throws IOException {
        Map<String, Integer> inputFileResult = new HashMap<>();
        Reader reader = Files.newBufferedReader(Paths.get(filepath));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
        for (CSVRecord record : csvParser) {
            String stId = "R-HSA-" + record.get(0);
            String count = record.get(1).replace(",", "");
            inputFileResult.put(stId, Integer.parseInt(count));
        }
        return inputFileResult;
    }

    // for testing only now
    private static void addweightToLabel(Foamtree foamtree) {
        foamtree.setLabel(foamtree.getLabel() + " " + foamtree.getHits());
        if (foamtree.getGroups() != null) {
            for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                addweightToLabel(foamtreeInGroups);
            }
        }
    }



    private FoamtreeFactory foamtreeFactory = new FoamtreeFactory();
    private List<Foamtree> foamtrees = foamtreeFactory.getFoamtrees();

//    private FoamtreeGenerator foamtreeGenerator = new FoamtreeGenerator();
//    private String inputFilePath = "src/main/resources/log-files/HSA-hits-2018.csv";

   // Map<String, Integer> inputFileResult = foamtreeGenerator.CSVParser(inputFilePath);




//    foamtreeGenerator.addHitsToFoamtrees(inputFileResult, foamtrees);
//    foamtreeGenerator.sumFoamtreesWeight(foamtrees);

    // get Autophagy to test
    //Foamtree foamtree = foamtrees.get(0);
    //addweightToLabel(foamtree);

//
//    // print foamtrees
//    ObjectMapper mapper = new ObjectMapper();
//    String prettyGraph = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(foamtrees.get(0));
//    //System.out.println(prettyGraph);
//
//
//    // generate a json file
//    //mapper.writeValue(new File("src/main/java/org/reactome/server/result/2018-2-pathways-0204.json"), foamtrees.subList(0,2));
//        mapper.writeValue(new File("src/main/webapp/resource/result/2018-log-data-all-pathways-0205.json"), foamtrees

}
