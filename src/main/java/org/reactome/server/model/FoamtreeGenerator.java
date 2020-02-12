package org.reactome.server.model;

import org.reactome.server.model.data.Foamtree;

import java.util.List;
import java.util.Map;


public class FoamtreeGenerator {

    public List<Foamtree> getResults(Map inputFileResult, List<Foamtree> foamtrees) {
        List<Foamtree> foamtreesWithHits = addHitsToFoamtrees(inputFileResult, foamtrees);
        List<Foamtree> foamtreesWithWeight = addWeightToFoamtrees(inputFileResult, foamtreesWithHits);
        return foamtreesWithWeight;
    }

    /**
     * Add hits value to foamtree, execute only one Foamtree
     *
     * @param inputFileResult
     * @param foamtree
     * @return
     */
    private void addHitsToFoamtree(Map<String, Integer> inputFileResult, Foamtree foamtree) {

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

    /**
     * Add hits value to Foamtree list
     *
     * @param inputFileResult
     * @param foamtrees
     */
    public List<Foamtree> addHitsToFoamtrees(Map<String, Integer> inputFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addHitsToFoamtree(inputFileResult, foamtree);
        }
        return foamtrees;
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

    private List<Foamtree> addWeightToFoamtrees(Map<String, Integer> inputFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addWeightoFoamtree(inputFileResult, foamtree);
        }
        return foamtrees;
    }


    /**
     * Sum sub groups weight to parent,execute only one Foamtree
     *
     * @param foamtree
     */
    public void sumFoamtreeWeight(Foamtree foamtree) {
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
    public List<Foamtree> sumFoamtreesWeight(List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            sumFoamtreeWeight(foamtree);
        }
        return foamtrees;
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
}
