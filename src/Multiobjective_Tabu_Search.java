import java.util.ArrayList;

class Multiobjective_Tabu_Search implements IMetaheuristics {

    public static final int NUMBER_OF_OBJECTIVES = 2;
    private int numberOfSolutions;
    private int numberOfGeners;
    private int dimension;
    static int tabuSize;
    private ArrayList<Individual> solutions;
    private double[] distanceNormalizingFactor_Time = new double[NUMBER_OF_OBJECTIVES];
    private double[] distanceNormalizingFactor_Wage = new double[NUMBER_OF_OBJECTIVES];

    Multiobjective_Tabu_Search(int numberOfSolutions, int numberOfGeners, int tabuSize) {
        solutions = new ArrayList<>();
        this.numberOfSolutions = numberOfSolutions;
        this.numberOfGeners = numberOfGeners;
        Multiobjective_Tabu_Search.tabuSize = tabuSize;
        dimension = Configuration.getDimension();
    }

    private void initialize() {
        for (int i = 0; i < numberOfSolutions; i++) {
            solutions.add(new Individual_MOTS(dimension));
            solutions.get(solutions.size() - 1).setPackingPlanAndFitness();
        }
    }

    public String run() {
        initialize();
        rangeNormalization();
        for(int generation = 0; generation < numberOfGeners; generation++) {
            for(int i = 0; i < solutions.size(); i++) {
                double[] lambdaVector = new double[2];
                for(int j = 0; j < solutions.size(); j++) {
                    int compared_j = solutions.get(j).compareTo(solutions.get(i));
                    if(compared_j != -1 && !solutions.get(j).equals(solutions.get(i))) {
                        double weight = weight(solutions.get(i), solutions.get(j));
                        if(solutions.get(i).getFitnessWage() > solutions.get(j).getFitnessWage()) {
                            lambdaVector[0] += weight * (distanceNormalizingFactor_Wage[1] - distanceNormalizingFactor_Wage[0]);
                        }
                        if(solutions.get(i).getFitnessTime() > solutions.get(j).getFitnessTime()) {
                            lambdaVector[1] += weight * (distanceNormalizingFactor_Time[1] - distanceNormalizingFactor_Time[0]);
                        }
                    }
                }
            }
        }
        return " ";
    }

    void rangeNormalization() {
        distanceNormalizingFactor_Time[0] = Configuration.getIdeal().y;
        distanceNormalizingFactor_Time[1] = Configuration.getNadir().y;
        distanceNormalizingFactor_Wage[0] = Configuration.getNadir().x;
        distanceNormalizingFactor_Wage[1] = Configuration.getIdeal().x;
    }

    double weight(Individual ind_i, Individual ind_j) {
        double weight = (distanceNormalizingFactor_Time[1] - distanceNormalizingFactor_Time[0])
                * Math.abs(ind_i.getFitnessTime() - ind_j.getFitnessTime());
        weight += (distanceNormalizingFactor_Wage[1] - distanceNormalizingFactor_Wage[0])
                * Math.abs(ind_i.getFitnessWage() - ind_j.getFitnessWage());
        return 1 / weight;

    }
}
