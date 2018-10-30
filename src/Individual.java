import java.util.Random;

public class Individual {
    private int[] route;
    private int[] packingPlan;
    private int fitnessTime;
    private double fitnessWage;

    private double mutProb;

    //fields accessed only through methods in ParetoFrontGenerator
    private double crowdingDistance;
    private int rank;

    public Individual(int[] route, double mutProb) {
        this.route = route;
        this.mutProb = mutProb;
        packingPlan = null;
    }

    public void mutation() {
        for(int i = 0; i < route.length - 2; i++) {
            if(Math.random() < mutProb) {
                int swapIndex = new Random().nextInt(route.length - 1);
                int temp = route[i];
                route[i] = route[swapIndex];
                route[swapIndex] = temp;
            }
            route[route.length - 1] = route[0];
        }
    }

    public int compareTo(Individual o) {
        return (int) Math.signum(Math.signum(fitnessTime - o.fitnessTime) + Math.signum(fitnessWage - o.fitnessWage));
    }


    //getters
    public int[] getRoute() {
        return route;
    }

    public int[] getPackingPlan() {
        return packingPlan;
    }

    public int getFitnessTime() {
        return fitnessTime;
    }

    public double getFitnessWage() {
        return fitnessWage;
    }

    public double getCrowdingDistance() {
        return crowdingDistance;
    }

    public int getRank() {
        return rank;
    }


    //setters
    public void setRoute(int[] route) {
        this.route = route;
    }

    public void setPackingPlan(int[] packingPlan) {
        this.packingPlan = packingPlan;
    }

    public void setFitnessTime(int fitnessTime) {
        this.fitnessTime = fitnessTime;
    }

    public void setFitnessWage(double fitnessWage) {
        this.fitnessWage = fitnessWage;
    }

    public void setCrowdingDistance(double crowdingDistance) {
        this.crowdingDistance = crowdingDistance;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

}