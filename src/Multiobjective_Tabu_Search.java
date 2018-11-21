//import java.util.ArrayList;
//import java.util.Collections;
//
//public class Multiobjective_Tabu_Search {
//    int solutionSize;
//    int numOfGeners;
//    int tabuSize;
//    int dimension;
//    ArrayList<Individual> solutions;
//
//    Multiobjective_Tabu_Search(Configuration config, int solutionSize, int numOfGeners, int tabuSize) {
//        this.solutionSize = solutionSize;
//        this.numOfGeners = numOfGeners;
//        this.tabuSize = tabuSize;
//
//        readParameters(config);
//    }
//
//    void readParameters(Configuration config) {
//        dimension = config.getDimension();
//    }
//
//    private void initialize() {
//        for (int i = 0; i < solutionSize; i++) {
//            solutions.add(generateRandomInd());
//        }
//    }
//
//    private Individual generateRandomInd() {//tu skończylam
//        int[] route = new int[dimension + 1];
//        ArrayList<Integer> routeList = new ArrayList<>();
//        for (int i = 0; i < dimension; i++) {
//            routeList.add(i);
//        }
//        Collections.shuffle(routeList);
//        for (int i = 0; i < dimension; i++) {
//            route[i] = routeList.get(i);
//        }
//        route[dimension] = route[0];
//        Individual ind = new Individual(route);
//        ind.setPackingPlanAndFitness(greedy);
//        return ind;
//    }
//
//}
