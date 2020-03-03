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
     * remove duplicated pathways in foamtree list
     *
     * @param foamtrees a foamtree list of top level pathways and sub pathways
     */
    private List<Foamtree> removeDuplicatedFoamtrees(List<Foamtree> foamtrees) {

        for (Foamtree foamtree : foamtrees) {
            removeDuplicatedFoamtree(foamtree);
        }
        return foamtrees;
    }

    /**
     * remove duplicated sub-pathways in foamtree group, execute only one foamtree object
     *
     * @param foamtree object
     */
    private void removeDuplicatedFoamtree(Foamtree foamtree) {

        if (foamtree.getGroups() != null) {
            List<Foamtree> removeDuplicated = foamtree.getGroups().stream()
                    .distinct()
                    .collect(Collectors.toList());
            foamtree.setGroups(removeDuplicated);

            for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                removeDuplicatedFoamtree(foamtreeInGroups);
            }
        }
    }

    /**
     * add hits value to foamtree list
     */
    public List<Foamtree> addHitsToFoamtrees(Map<String, Integer> logFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addHitsToFoamtree(logFileResult, foamtree);
        }
        return foamtrees;
    }

    /**
     * add hits value to foamtree hits attribute, execute only one foamtree
     *
     * @param logFileResult stId and hits as key value pair
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
     * add weight value to foamtree list
     */
    private List<Foamtree> addWeightToFoamtrees(Map<String, Integer> logFileResult, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addWeightoFoamtree(logFileResult, foamtree);
        }
        return foamtrees;
    }

    /**
     * add weight value to foamtree weight attribute, execute only one foamtree
     */
    private void addWeightoFoamtree(Map<String, Integer> logFileResult, Foamtree foamtree) {

        if (!logFileResult.isEmpty()) {
            foamtree.setWeight(logFileResult.getOrDefault(foamtree.getStId(), 1));
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addWeightoFoamtree(logFileResult, foamtreeInGroups);
                }
            }
        }
    }

    /**
     * add age value to foamtrees list
     */
    private List<Foamtree> addAgeToFoamtrees(Map<String, Integer> age, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addAgeToFoamtree(age, foamtree);
        }
        return foamtrees;
    }

    /**
     * add age value to foamtree age attribute, execute only one foamtree
     *
     * @param age stId and age as key value pair
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
     * sum sub groups weight to parent as weight of a foamtree in foamtrees list
     */
    public List<Foamtree> sumFoamtreesWeight(List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            sumFoamtreeWeight(foamtree);
        }
        return foamtrees;
    }

    /**
     * sum sub groups weight to parent as weight of a foamtree, execute only one foamtree
     */
    private void sumFoamtreeWeight(Foamtree foamtree) {
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

    //for testing only
    private List<Foamtree> addAgeAsWeightToFoamtrees(Map<String, Integer> age, List<Foamtree> foamtrees) {
        for (Foamtree foamtree : foamtrees) {
            addAgeAsWeightToFoamtree(age, foamtree);
        }
        return foamtrees;
    }

    private void addAgeAsWeightToFoamtree(Map<String, Integer> age, Foamtree foamtree) {
        if (!age.isEmpty()) {
            foamtree.setWeight(age.getOrDefault(foamtree.getStId(), 1));
            if (foamtree.getGroups() != null) {
                for (Foamtree foamtreeInGroups : foamtree.getGroups()) {
                    addAgeAsWeightToFoamtree(age, foamtreeInGroups);
                }
            }
        }
    }
}
