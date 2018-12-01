import java.util.ArrayList;

class Multiobjective_Tabu_Search implements IMetaheuristics {

    private int numberOfSolutions;
    private int numberOfGeners;
    private int dimension;
    static int tabuSize;
    private ArrayList<Individual> solutions;

    Multiobjective_Tabu_Search(int numberOfSolutions, int numberOfGeners, int tabuSize) {
        this.numberOfSolutions = numberOfSolutions;
        this.numberOfGeners = numberOfGeners;
        Multiobjective_Tabu_Search.tabuSize = tabuSize;
        dimension = Configuration.getDimension();
    }

    private void initialize() {
        for (int i = 0; i < numberOfSolutions; i++) {
            solutions.add(new Individual_NSGA_II(dimension));
        }
    }

    public String run() {
        initialize();
        for(Individual individual : solutions) {

        }
        return " ";
    }
}
