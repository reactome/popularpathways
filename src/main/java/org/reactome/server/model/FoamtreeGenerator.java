package org.reactome.server.model;

import org.reactome.server.model.data.Foamtree;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FoamtreeGenerator {

    public List<Foamtree> getResults(Map<String, Integer> logFileResult, Map<String, Integer> age, List<Foamtree> foamtrees) {

        List<Foamtree> foamtreesAllData;
        List<Foamtree> foamtreesAddWeight = addWeightToFoamtrees(logFileResult, removeDuplicatedFoamtrees(foamtrees));
        foamtreesAllData = addAgeToFoamtrees(age, foamtreesAddWeight);

        return foamtreesAllData;
    }

    /**
     * remove duplicated sub-pathways in foamtree group, execute only one foamtree
     *
     * @param foamtree
     * @return
     */
    private Foamtree removeDuplicatedFoamtree(Foamtree foamtree) {

        if (foamtree.getGroups() != null) {
            List<Foamtree> removeDuplicated = foamtree.getGroups().stream()
                    .distinct()
                    .collect(Collectors.toList());
            foamtree.setGroups(removeDuplicated);

            for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                removeDuplicatedFoamtree(foamtreeInGroups);
            }
        }
        return foamtree;
    }

    /**
     * remove duplicated pathways in foamtree list
     *
     * @param foamtrees
     * @return
     */
    private List<Foamtree> removeDuplicatedFoamtrees(List<Foamtree> foamtrees) {

        for (Foamtree foamtree : foamtrees) {
            removeDuplicatedFoamtree(foamtree);
        }
        return foamtrees;
    }

    /**
     * add hits value to foamtree hits, execute only one foamtree
     *
     * @param logFileResult: stId and hits as key and value pair
     * @param foamtree
     * @return
     */
    private void addHitsToFoamtree(Map<String, Integer> logFileResult, Foamtree foamtree) {

        if (!logFileResult.isEmpty()) {
            if (logFileResult.containsKey(foamtree.getStId())) {
                foamtree.setHits(logFileResult.get(foamtree.getStId()));
                foamtree.setWeight(foamtree.getHits());
            } else {
                foamtree.setHits(1);
            }
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addHitsToFoamtree(logFileResult, foamtreeInGroups);
                }
            }
        }
    }

    /**
     * add hits value to foamtree list
     *
     * @param logFileResult: stId and hits as key and value pair
     * @param foamtrees
     */
    public List<Foamtree> addHitsToFoamtrees(Map<String, Integer> logFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addHitsToFoamtree(logFileResult, foamtree);
        }
        return foamtrees;
    }

    /**
     * add weight value to foamtree weight, execute only one foamtree
     *
     * @param logFileResult: stId and hits as key and value pair
     * @param foamtree
     */
    private void addWeightoFoamtree(Map<String, Integer> logFileResult, Foamtree foamtree) {

        if (!logFileResult.isEmpty()) {
            if (logFileResult.containsKey(foamtree.getStId())) {
                foamtree.setWeight(logFileResult.get(foamtree.getStId()));
            } else {
                foamtree.setWeight(1);
            }
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addWeightoFoamtree(logFileResult, foamtreeInGroups);
                }
            }
        }
    }

    /**
     * add weight value to foamtree list
     *
     * @param logFileResult: stId and hits as key and value pair
     * @param foamtrees
     * @return
     */
    private List<Foamtree> addWeightToFoamtrees(Map<String, Integer> logFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addWeightoFoamtree(logFileResult, foamtree);
        }
        return foamtrees;
    }

    /**
     * add age value to foamtree age, execute only one foamtree
     *
     * @param age:     stId and age as key and value pair
     * @param foamtree
     */
    private void addAgeToFoamtree(Map<String, Integer> age, Foamtree foamtree) {

        if (!age.isEmpty()) {
            if (age.containsKey(foamtree.getStId())) {
                foamtree.setAge(age.get(foamtree.getStId()));
            }

            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addAgeToFoamtree(age, foamtreeInGroups);
                }
            }
        }
    }

    /**
     * add age value to foamtree list
     *
     * @param age:      stId and age as key and value pair
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
     * sum sub groups weight to parent as foamtree weight,execute only one foamtree
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


    //for testing only
    private List<Foamtree> addAgeAsWeightToFoamtrees(Map<String, Integer> age, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addAgeAsWeightToFoamtree(age, foamtree);
        }
        return foamtrees;
    }

    private void addAgeAsWeightToFoamtree(Map<String, Integer> age, Foamtree foamtree) {
        if (!age.isEmpty()) {
            if (age.containsKey(foamtree.getStId())) {
                foamtree.setWeight(age.get(foamtree.getStId()));
            } else {
                foamtree.setWeight(1);
            }
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addAgeAsWeightToFoamtree(age, foamtreeInGroups);
                }
            }
        }
    }
}
