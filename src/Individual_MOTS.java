
public class Individual_MOTS extends Individual{

    Integer[] tabu = new Integer[Multiobjective_Tabu_Search.tabuSize];
    int indexOfOldest = 0;
    int[] vectorLambda = new int[Multiobjective_Tabu_Search.NUMBER_OF_OBJECTIVES];

    Individual_MOTS(short[] route, int birthday) {
        super(route, birthday);
    }

    Individual_MOTS(int dimension) {
        super(dimension);
    }

    @Override
    void mutate(float mutProb) {
        if(Math.random() < mutProb) {
            int index = 0;
            short temp = route[index];
            route[index] = route[++index];
            route[index] = temp;
        }
        setPackingPlanAndFitness();
    }

    boolean contains(Individual individual) {
        Integer hash = individual.hashCode();
        for(Integer i : tabu) {
            if(hash == i)
                return true;
        }
        return false;
    }

    void addVisitedIndividual(Individual individual) {
        tabu[indexOfOldest] = individual.hashCode();
        indexOfOldest = indexOfOldest == tabu.length - 1 ? 0 : indexOfOldest++;
    }
}
