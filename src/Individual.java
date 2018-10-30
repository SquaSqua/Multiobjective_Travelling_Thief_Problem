import java.util.Random;

public class Individual {
    private int[] route;
    private int[] packingPlan;
    private int fitnessTime;
    private double fitnessWage;

    private double mutProb;

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

    public int[] getRoute() {
        return route;
    }

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

    public double getFitnessWage() {
        return fitnessWage;
    }

    public int getFitnessTime() {
        return fitnessTime;
    }
}