import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

class ParetoFrontsGenerator {

    private Point ideal, nadir;

    ParetoFrontsGenerator(Point ideal, Point nadir) {
        this.ideal = ideal;
        this.nadir = nadir;
    }

    //each calling overrides last set paretoFronts
    private ArrayList<ArrayList<Individual>> generateFronts(ArrayList<Individual> population) {//tu ma byc private
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
                        } else if (compared == 1) {//uwaga! zamienione compared = -1 z tym nizszym
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
                            if(i < population.size() - 1) {
                                i++;
                                j = -1;
                            }else {
                                j = paretoFronts.size();
                            }
                            break;
                        } else if (compared == -1) {
                            //nowy front
                            if (paretoFronts.size() < j + 2) {
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

    ArrayList<ArrayList<Individual>> generateFrontsWithAssignments(ArrayList<Individual> population) {
        ArrayList<ArrayList<Individual>> paretoFronts;
        paretoFronts = generateFronts(population);
        assignRank(paretoFronts);
        crowdingDistanceSetter(paretoFronts);

        return paretoFronts;
    }

    private void assignRank(ArrayList<ArrayList<Individual>> paretoFronts) {
        for(int i = 0; i < paretoFronts.size(); i++) {
            for(int j = 0; j < paretoFronts.get(i).size(); j++) {
                paretoFronts.get(i).get(j).setRank(i);
            }
        }
    }

    private void objectiveSorting(ArrayList<ArrayList<Individual>> paretoFronts) {
        for(int i = 0; i < paretoFronts.size(); i++) {
            paretoFronts.get(i).sort(new ObjectiveFrontComparator());
        }
    }

    private void crowdingDistanceSetter(ArrayList<ArrayList<Individual>> paretoFronts) {
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

    String ED_measure(ArrayList<ArrayList<Individual>> paretoFronts) {
        double sumED = 0;
        ArrayList<Individual> paretoFront = paretoFronts.get(0);
        paretoFront.sort(new ObjectiveFrontComparator());
        for(int i = 0; i < paretoFront.size(); i++) {
            Individual ind = paretoFront.get(i);
            sumED += Math.sqrt((long) (int) ((ind.getFitnessTime() - ideal.x)
                    * (ind.getFitnessTime() - ideal.x) + (ind.getFitnessWage() - ideal.y)
                    * (ind.getFitnessWage() - ideal.y)));
        }

        return (sumED / paretoFront.size()) + "";
    }

    String PFS_measure(ArrayList<ArrayList<Individual>> paretoFronts) {
        return paretoFronts.get(0).size() + "";
    }

    double HV_measure(ArrayList<ArrayList<Individual>> paretoFronts) {
        ArrayList<Individual> paretoFront = paretoFronts.get(0);
        paretoFront.sort(new ObjectiveFrontComparator());
        long hyperVolume = 0L;
        double lastY = nadir.y;
        for(int i = 0; i < paretoFront.size(); i++) {
            Individual ind = paretoFront.get(i);
            hyperVolume += ((int)((nadir.x - ind.getFitnessTime())
                    * (lastY - ind.getFitnessWage())));
            lastY = paretoFront.get(i).getFitnessWage();
        }
        return hyperVolume;
    }
}
