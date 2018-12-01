
public class Individual_MOTS extends Individual{

    Integer[] tabu = new Integer[Multiobjective_Tabu_Search.tabuSize];
    int current

    Individual_MOTS(short[] route, int birthday) {
        super(route, birthday);
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

    public void addVisitedIndividual() {

    }
}
