import java.util.ArrayList;

class Multiobjective_Tabu_Search implements IMetaheuristics {

    private static final int NUMBER_OF_OBJECTIVES = 2;
    private static final int WAGE_INDEX = 0;
    private static final int TIME_INDEX = 1;
    private static final int MIN_FROM_RANGE = 0;
    private static final int MAX_FROM_RANGE = 1;
    private int numberOfSolutions;
    private int numberOfGeners;
    private int neighbourhoodSize;
    private int dimension;
    static int tabuSize;
    private ArrayList<Individual_MOTS> solutions;
    private double[] distanceNormalizingFactor_Time = new double[NUMBER_OF_OBJECTIVES];
    private double[] distanceNormalizingFactor_Wage = new double[NUMBER_OF_OBJECTIVES];
    private double[] pi;

    Multiobjective_Tabu_Search(int numberOfSolutions, int numberOfGeners, int neighbourhoodSize, int tabuSize) {
        solutions = new ArrayList<>();
        this.numberOfSolutions = numberOfSolutions;
        this.numberOfGeners = numberOfGeners;
        this.neighbourhoodSize = neighbourhoodSize;
        Multiobjective_Tabu_Search.tabuSize = tabuSize;
        dimension = Configuration.getDimension();
        rangeNormalization();
        pi = countPi();
    }

    private void initialize() {
        for (int i = 0; i < numberOfSolutions; i++) {
            solutions.add(new Individual_MOTS(dimension));
            solutions.get(solutions.size() - 1).setPackingPlanAndFitness();
        }
    }

    public String run() {
        initialize();
        for (int generation = 0; generation < numberOfGeners; generation++) {
            for (int i = 0; i < solutions.size(); i++) {
                Individual_MOTS individual_i = solutions.get(i);
                double[] lambdaVector = new double[NUMBER_OF_OBJECTIVES];
                for (Individual_MOTS individual_j : solutions) {
                    int compared_j = individual_j.compareTo(individual_i);
                    if (compared_j != -1 && !individual_j.equals(individual_i)) {
                        double weight = weight(individual_i, individual_j);
                        if (individual_i.getFitnessWage() > individual_j.getFitnessWage()) {
                            lambdaVector[0] += weight * (distanceNormalizingFactor_Wage[1] - distanceNormalizingFactor_Wage[0]);
                        }
                        if (individual_i.getFitnessTime() > individual_j.getFitnessTime()) {
                            lambdaVector[1] += weight * (distanceNormalizingFactor_Time[1] - distanceNormalizingFactor_Time[0]);
                        }
                    }
                }
                if (lambdaVector[WAGE_INDEX] == 0 && lambdaVector[TIME_INDEX] == 0) {
                    lambdaVector = setRandomLambda();
                }
                normalizeVector(lambdaVector);
                Individual_MOTS winner = findBestNeighbour(individual_i, generation, lambdaVector);
                solutions.set(i, winner);
            }
        }
        return " ";
    }

    private void rangeNormalization() {
        distanceNormalizingFactor_Time[MIN_FROM_RANGE] = Configuration.getIdeal().y;
        distanceNormalizingFactor_Time[MAX_FROM_RANGE] = Configuration.getNadir().y;
        distanceNormalizingFactor_Wage[MIN_FROM_RANGE] = Configuration.getNadir().x;
        distanceNormalizingFactor_Wage[MAX_FROM_RANGE] = Configuration.getIdeal().x;
    }

    private double[] countPi() {
        double[] pi = new double[NUMBER_OF_OBJECTIVES];
        pi[WAGE_INDEX] = 1 / (distanceNormalizingFactor_Wage[MAX_FROM_RANGE] - distanceNormalizingFactor_Wage[MIN_FROM_RANGE]);
        pi[TIME_INDEX] = 1 / (distanceNormalizingFactor_Time[MAX_FROM_RANGE] - distanceNormalizingFactor_Time[MIN_FROM_RANGE]);
        return pi;
    }

    private double weight(Individual ind_i, Individual ind_j) {
        double weight = pi[WAGE_INDEX]
                * Math.abs(ind_i.getFitnessWage() - ind_j.getFitnessWage());
        weight += pi[TIME_INDEX]
                * Math.abs(ind_i.getFitnessTime() - ind_j.getFitnessTime());
        return 1 / weight;

    }

    private double[] setRandomLambda() {
        return new double[]{Math.random(), Math.random()};
    }

    private void normalizeVector(double[] vector) {
        double value = 0;
        for (double component : vector) {
            value += component * component;
        }
        value = Math.sqrt(value);

        for (int i = 0; i < vector.length; i++) {
            vector[i] = vector[i] / value;
        }
    }

    private Individual_MOTS findBestNeighbour(Individual_MOTS individual, int birthday, double[] lambdaVector) {
        Individual_MOTS best = individual;
        double meritOFBest = best.countMerit(lambdaVector, distanceNormalizingFactor_Wage[1], distanceNormalizingFactor_Time[1]);
        for (int i = 0; i < neighbourhoodSize; i++) {
            short[] route = new short[individual.getRoute().length];
            System.arraycopy(individual.getRoute(), 0, route, 0, route.length);
            Individual_MOTS neighbour = new Individual_MOTS(route, birthday);
            neighbour.mutate();
            if (!individual.containsInTabu(neighbour)) {
                neighbour.setPackingPlanAndFitness();
                double merit = neighbour.countMerit(lambdaVector, distanceNormalizingFactor_Wage[1], distanceNormalizingFactor_Time[1]);
                if (meritOFBest < merit) {
                    meritOFBest = merit;
                    best = neighbour;
                }
            }
        }
        return best;
    }
}