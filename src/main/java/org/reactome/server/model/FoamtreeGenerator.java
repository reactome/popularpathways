package org.reactome.server.model;

import org.reactome.server.model.data.Foamtree;

import java.util.List;
import java.util.Map;


public class FoamtreeGenerator {

    //todo wired
    public List<Foamtree> getResults(Map inputFileResult,Map age, List<Foamtree> foamtrees) {
        //List<Foamtree> foamtreesWithHits = addHitsToFoamtrees(inputFileResult, foamtrees);
        //List<Foamtree> foamtreesWithWeight = sumFoamtreesWeight(foamtreesWithHits);
        List<Foamtree> foamtreesAddWeight = addWeightToFoamtrees(inputFileResult, foamtrees);
        List<Foamtree> foamtreesAllDate = addAgeToFoamtrees(age, foamtreesAddWeight);

        return foamtreesAllDate;
    }

    /**
     * add hits value to foamtree, execute only one Foamtree
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
     * add hits value to Foamtree list
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

    /**
     * add weight value to foamtree, execute only one Foamtree
     * @param inputFileResult
     * @param foamtree
     */
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

    /**
     * add weight value to Foamtree list
     * @param inputFileResult
     * @param foamtrees
     * @return
     */
    private List<Foamtree> addWeightToFoamtrees(Map<String, Integer> inputFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addWeightoFoamtree(inputFileResult, foamtree);
        }
        return foamtrees;
    }

    /**
     * add age value to foamtree, execute only one foamtree
     * @param age
     * @param foamtree
     */
    private void addAgeToFoamtree(Map<String, Integer> age, Foamtree foamtree) {

        if (!age.isEmpty()) {
            if (age.containsKey(foamtree.getStId())) {
                foamtree.setAge(age.get(foamtree.getStId()));
            } else {
                foamtree.setAge(20);
            }
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addWeightoFoamtree(age, foamtreeInGroups);
                }
            }
        }
    }

    /**
     * add age value to Foamtree list
     * @param age
     * @param foamtrees
     * @return
     */
    private List<Foamtree> addAgeToFoamtrees(Map<String, Integer> age, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addAgeToFoamtree(age, foamtree);
        }
        return foamtrees;
    }

    /**
     * sum sub groups weight to parent,execute only one foamtree
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
     * sum sub groups weight to parent in foamtree list
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
