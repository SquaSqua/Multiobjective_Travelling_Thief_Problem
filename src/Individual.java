import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

class Individual {

    private short[] route;
    private boolean[] packingPlan;
    private double fitnessTime;
    private int fitnessWage;
    private int birthday;

    //fields accessed only through methods in ParetoFrontGenerator
    private double crowdingDistance;
    private int rank;

    private Individual(short[] route, int birthday) {
        this.route = route;
        packingPlan = null;
        this.birthday = birthday;
    }

    Individual(short[] route) {
        this.route = route;
        packingPlan = null;
    }

    Individual(int dimension) {
        short[] route = new short[dimension + 1];
        ArrayList<Integer> routeList = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            routeList.add(i);
        }
        Collections.shuffle(routeList);
        for (int i = 0; i < dimension; i++) {
            route[i] = routeList.get(i).shortValue();
        }
        route[dimension] = route[0];
        this.route = route;
        this.birthday = 0;
    }

    void mutation(double mutProb) {
        for(int i = 0; i < route.length - 1; i++) {
            if(Math.random() < mutProb) {
                int swapIndex = new Random().nextInt(route.length - 1);
                short temp = route[i];
                route[i] = route[swapIndex];
                route[swapIndex] = temp;
            }
        }
        route[route.length - 1] = route[0];

        setPackingPlanAndFitness();
    }

    Individual[] cycleCrossing(Individual parent2, double crossProb, int generation) {
        short[] p2 = parent2.getRoute();
        short[] ch1 = new short[route.length];
        short[] ch2 = new short[route.length];

        if (Math.random() < crossProb) {
            short[] route1 = new short[p2.length - 1];
            short[] route2 = new short[p2.length - 1];
            for (int i = 0; i < route1.length; i++) {
                route1[i] = route[i];
                route2[i] = p2[i];
            }
            short[] child1 = new short[route1.length];
            short[] child2 = new short[route2.length];

            for (int i = 0; i < child1.length; i++) {
                child1[i] = -1;
                child2[i] = -1;
            }
            int beginningValue = route1[0];
            int currentInd = 0;

            boolean isSwapTurn = false;
            while (true) {
                assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                if (route1[currentInd] == route2[currentInd]) {
                    isSwapTurn = !isSwapTurn;
                }
                currentInd = findIndexOfaValue(route2[currentInd], route1);
                if (route2[currentInd] == beginningValue) {
                    assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                    currentInd = findFirstEmpty(child1);
                    if (currentInd == -1) {
                        break;
                    }
                    beginningValue = route1[currentInd];
                    isSwapTurn = !isSwapTurn;
                }
            }
            ch1 = addLastCity(child1);
            ch2 = addLastCity(child2);
        } else {
            for (int i = 0; i < ch1.length; i++) {
                ch1[i] = route[i];
                ch2[i] = p2[i];
            }
        }
        return new Individual[]{
                new Individual(ch1, generation),
                new Individual(ch2, generation)
        };
    }

    private void assignGens(boolean isSwapTurn, int currentInd, short[] route1, short[] route2, short[] child1, short[] child2) {
        if (!isSwapTurn) {
            child1[currentInd] = route1[currentInd];
            child2[currentInd] = route2[currentInd];
        } else {
            child1[currentInd] = route2[currentInd];
            child2[currentInd] = route1[currentInd];
        }
    }

    private short[] addLastCity(short[] child) {
        short[] ch = new short[child.length + 1];
        System.arraycopy(child, 0, ch, 0, child.length);
        ch[ch.length - 1] = ch[0];
        return ch;
    }

    private short findFirstEmpty(short[] route) {
        short firstEmpty = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == -1) {
                firstEmpty = (short)i;
                break;
            }
        }
        return firstEmpty;
    }

    private int findIndexOfaValue(int value, short[] route) {
        int index = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == value) {
                index = i;
                break;
            }
        }
        return index;
    }

    int compareTo(Individual o) {
        return (int) Math.signum((Math.signum(fitnessTime - o.fitnessTime) * -1)+ Math.signum((fitnessWage - o.fitnessWage)));
    }


    //getters
    short[] getRoute() {
        return route;
    }

    boolean[] getPackingPlan() {
        return packingPlan;
    }

    double getFitnessTime() {
        return fitnessTime;
    }

    int getFitnessWage() {
        return fitnessWage;
    }

    int getBirthday() { return birthday; }

    double getCrowdingDistance() {
        return crowdingDistance;
    }

    int getRank() {
        return rank;
    }


    //setters
    void setPackingPlan(boolean[] packingPlan) {
        this.packingPlan = packingPlan;
    }

    void setPackingPlanAndFitness() {
        GreedyPackingPlan.settlePackingPlan(this);
        GreedyPackingPlan.setFitnessForIndividual(this);
    }

    void setFitnessTime(double fitnessTime) {
        this.fitnessTime = fitnessTime;
    }

    void setFitnessWage(int fitnessWage) {
        this.fitnessWage = fitnessWage;
    }

    void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    void setRank(int rank) {
        this.rank = rank;
    }

}