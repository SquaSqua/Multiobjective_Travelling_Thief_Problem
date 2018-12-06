import java.util.ArrayList;
import java.util.Collections;
abstract class Individual {

    short[] route;
    private boolean[] packingPlan;
    private double fitnessTime;
    private int fitnessWage;
    private int birthday;

    //fields accessed only through methods in ParetoFrontGenerator
    private double crowdingDistance;
    private int rank;

    Individual(short[] route, int birthday) {
        this.route = route;
        packingPlan = null;
        this.birthday = birthday;
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