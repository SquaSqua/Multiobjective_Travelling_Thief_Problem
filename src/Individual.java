import java.util.Arrays;
import java.util.Random;

class Individual {

    private int[] route;
    private int[] packingPlan;
    private double fitnessTime;
    private int fitnessWage;
    private int birthday;

    private double mutProb;

    //fields accessed only through methods in ParetoFrontGenerator
    private double crowdingDistance;
    private int rank;

    Individual(int[] route, double mutProb, int birthday) {
        this.route = route;
        this.mutProb = mutProb;
        this.birthday = birthday;
        packingPlan = null;
    }

    void mutation(GreedyPackingPlan greedy) {
        for(int i = 0; i < route.length - 2; i++) {
            if(Math.random() < mutProb) {
                int swapIndex = new Random().nextInt(route.length - 1);
                int temp = route[i];
                route[i] = route[swapIndex];
                route[swapIndex] = temp;
            }
        }
        route[route.length - 1] = route[0];

        setPackingPlanAndFitness(greedy);
//        System.out.println(Arrays.toString(packingPlan));
    }

    int compareTo(Individual o) {
        return (int) Math.signum(Math.signum(fitnessTime - o.fitnessTime) + Math.signum(fitnessWage - o.fitnessWage));
    }


    //getters
    int[] getRoute() {
        return route;
    }

    int[] getPackingPlan() {
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
    void setPackingPlan(int[] packingPlan) {
        this.packingPlan = packingPlan;
    }

    void setPackingPlanAndFitness(GreedyPackingPlan greedy) {
        greedy.settlePackingPlan(this);
        greedy.setFitnessForIndividual(this);
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