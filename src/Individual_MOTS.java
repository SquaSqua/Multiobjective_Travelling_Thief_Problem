import java.util.Objects;

public class Individual_MOTS extends Individual{

    private static final int HASH_INDEX = 0;
    private static final int INDIVIDUAL_INDEX = 1;
    private Object[][] tabuList = new Object[Multiobjective_Tabu_Search.tabuSize][2];
    private int indexOfOldest = 0;

    Individual_MOTS(short[] route, int birthday) {
        super(route, birthday);
    }

    Individual_MOTS(int dimension) {
        super(dimension);
    }

    void mutate() {
        int index = 0;
        short temp = route[index];
        route[index] = route[++index];
        route[index] = temp;
        setPackingPlanAndFitness();
    }

    boolean containsInTabu(Individual_MOTS individual) {
        Integer hash = individual.hashCode();
        for(Object[] i : tabuList) {
            if(hash == i[HASH_INDEX])
                return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(route);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object)
            return true;
        if(this.getClass() != object.getClass())
            return false;
        Individual_MOTS individual = (Individual_MOTS) object;
        return this.getFitnessWage() == individual.getFitnessWage() && this.getFitnessTime() == individual.getFitnessTime();
    }

    void addVisitedIndividual(Individual individual) {
        tabuList[indexOfOldest][HASH_INDEX] = individual.hashCode();
        tabuList[indexOfOldest][INDIVIDUAL_INDEX] = individual;
        indexOfOldest = indexOfOldest == tabuList.length - 1 ? 0 : ++indexOfOldest;
    }

    void reassignTabuList(Individual_MOTS toIndividual) {
        toIndividual.tabuList = this.tabuList;
    }
}
