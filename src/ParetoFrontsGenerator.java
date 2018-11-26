import java.util.ArrayList;

class ParetoFrontsGenerator {

    private static Point ideal = Configuration.getIdeal();
    private static Point nadir = Configuration.getNadir();


    //each calling overrides last set paretoFronts
    private static ArrayList<ArrayList<Individual>> generateFronts(ArrayList<? extends Individual> population) {
        ArrayList<ArrayList<Individual>> paretoFronts = new ArrayList<>();
        paretoFronts.add(new ArrayList<>());
        for(int i = 0; i < population.size(); i++) {
            for (int j = 0; j < paretoFronts.size(); j++) {
                ArrayList<Individual> currentFront = paretoFronts.get(j);
                if (currentFront.size() == 0) {
                    currentFront.add(population.get(i));
                    break;
                } else {
                    for (int k = 0; k < currentFront.size(); k++) {
                        int compared = population.get(i).compareTo(currentFront.get(k));
                        if ((compared == 0) && (k == currentFront.size() - 1)) {
                            currentFront.add(population.get(i));
                            if(i < population.size() - 1) {
                                i++;
                                j = -1;
                            }else {
                                j = paretoFronts.size();
                            }
                            break;
                        } else if (compared == 1) {
                            //zamiana miejsc
                            ArrayList<Individual> betterFront = new ArrayList<>();
                            betterFront.add(population.get(i));
                            for(int z = 0; z < k; ) {
                                betterFront.add(currentFront.get(z));
                                currentFront.remove(z);
                                k--;
                            }
                            for(int z = 1; z < currentFront.size(); z++) {
                                if(population.get(i).compareTo(currentFront.get(z)) == 0) {
                                    betterFront.add(currentFront.get(z));
                                    currentFront.remove(z);
                                    z--;
                                }
                            }
                            paretoFronts.add(j, betterFront);
                            ArrayList<ArrayList<Individual>> fixedPareto = new ArrayList<>();
                            for(int correctParetoIndex = 0; correctParetoIndex < j + 1; correctParetoIndex++) {
                                fixedPareto.add(paretoFronts.get(correctParetoIndex));
                            }
                            fixedPareto.addAll(fixFronts(paretoFronts, j));
                            paretoFronts = fixedPareto;
                            if(i < population.size() - 1) {
                                i++;
                                j = -1;
                            }else {
                                j = paretoFronts.size();
                            }
                            break;
                        } else if (compared == -1) {
                            //nowy gorszy front
                            if (paretoFronts.size() < j + 2) {//1+1
                                paretoFronts.add(new ArrayList<>());
                                paretoFronts.get(j + 1).add(population.get(i));
                                if(i < population.size() - 1) {
                                    i++;
                                    j = -1;
                                }else {
                                    j = paretoFronts.size();
                                }
                                break;
                            }
                            else {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return paretoFronts;
    }

    private static ArrayList<ArrayList<Individual>> fixFronts(ArrayList<ArrayList<Individual>> paretoFronts, int j) {
        ArrayList<Individual> tempPopulation = new ArrayList<>();
        for(int i = j + 1; i < paretoFronts.size(); i++) {
            tempPopulation.addAll(paretoFronts.get(i));
        }
        return generateFronts(tempPopulation);
    }

    static ArrayList<ArrayList<Individual>> generateFrontsWithAssignments(ArrayList<Individual> population) {
        ArrayList<ArrayList<Individual>> paretoFronts;
        paretoFronts = generateFronts(population);
        assignRank(paretoFronts);
        crowdingDistanceSetter(paretoFronts);

        return paretoFronts;
    }

    private static void assignRank(ArrayList<ArrayList<Individual>> paretoFronts) {
        for(int i = 0; i < paretoFronts.size(); i++) {
            for(int j = 0; j < paretoFronts.get(i).size(); j++) {
                paretoFronts.get(i).get(j).setRank(i);
            }
        }
    }

    private static void objectiveSorting(ArrayList<ArrayList<Individual>> paretoFronts) {
        for (ArrayList<Individual> paretoFront : paretoFronts) {
            paretoFront.sort(new ObjectiveFrontComparator());
        }
    }

    private static void crowdingDistanceSetter(ArrayList<ArrayList<Individual>> paretoFronts) {
        objectiveSorting(paretoFronts);
        for (ArrayList<Individual> paretoFront : paretoFronts) {
            paretoFront.get(0).setCrowdingDistance(Double.POSITIVE_INFINITY);
            paretoFront.get(paretoFront.size() - 1).setCrowdingDistance(Double.POSITIVE_INFINITY);

            for (int j = 1; j < paretoFront.size() - 1; j++) {
                Individual currentInd = paretoFront.get(j);
                double a = Math.abs(paretoFront.get(j + 1).getFitnessTime()
                        - paretoFront.get(j - 1).getFitnessTime());
                double b = Math.abs(paretoFront.get(0).getFitnessWage()
                        - paretoFront.get(paretoFront.size() - 1).getFitnessWage());
                currentInd.setCrowdingDistance(a * b);
            }
        }
    }

    static String ED_measure(ArrayList<ArrayList<Individual>> paretoFronts) {
        double sumED = 0;
        ArrayList<Individual> paretoFront = paretoFronts.get(0);
        paretoFront.sort(new ObjectiveFrontComparator());
        for (Individual ind : paretoFront) {
            sumED += Math.sqrt((long)((ind.getFitnessTime() - ideal.x)
                    * (ind.getFitnessTime() - ideal.x) + (ind.getFitnessWage() - ideal.y)
                    * (ind.getFitnessWage() - ideal.y)));
        }

        return (sumED / paretoFront.size()) + "";
    }

    static String PFS_measure(ArrayList<ArrayList<Individual>> paretoFronts) {
        return paretoFronts.get(0).size() + "";
    }

    static double HV_measure(ArrayList<ArrayList<Individual>> paretoFronts) {
        ArrayList<Individual> paretoFront = paretoFronts.get(0);
        paretoFront.sort(new ObjectiveFrontComparator());
        long hyperVolume = 0L;
        double lastY = nadir.y;
        for (Individual ind : paretoFront) {
            hyperVolume += ((int) ((nadir.x - ind.getFitnessTime())
                    * (lastY - ind.getFitnessWage())));
            lastY = ind.getFitnessWage();
        }
        return hyperVolume;
    }
}
