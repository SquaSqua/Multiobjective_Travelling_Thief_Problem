import java.util.Comparator;

public class ObjectiveFrontComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual o1, Individual o2) {
        return Integer.compare(o1.getFitnessWage(), o2.getFitnessWage());
    }
}