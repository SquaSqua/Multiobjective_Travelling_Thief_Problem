import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Date;

public class Runner {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        Evolution population = new Evolution("src/definitionFiles/hard_3.ttp", 10, 10,
                6, 0.5, 0.2);

        Date date = new Date();
        String dateAndTime = date.getMonth() + "_"  + date.getDay()
                + "_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getSeconds();
        String results = "results_" + dateAndTime + ".csv";
        try
        {
            PrintWriter out = new PrintWriter(results);
            out.println(population.evolve());
            out.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Nie da się utworzyć pliku!");
        }
        long milliseconds = System.currentTimeMillis() - start;
        System.out.println(milliseconds / 1000 + "." + milliseconds % 1000 + "s");
//        ArrayList<Individual> pop = new ArrayList<>();
//        for(int i = 0; i < 7; i++) {
//            pop.add(new Individual(new int[] {1,2},1,1));
//        }
//
//        pop.get(0).setFitnessTime(40);
//        pop.get(1).setFitnessTime(20);
//        pop.get(2).setFitnessTime(10);
//        pop.get(3).setFitnessTime(60);
//        pop.get(4).setFitnessTime(45);
//        pop.get(5).setFitnessTime(25);
//        pop.get(6).setFitnessTime(15);
//
//        pop.get(0).setFitnessWage(10);
//        pop.get(1).setFitnessWage(25);
//        pop.get(2).setFitnessWage(40);
//        pop.get(3).setFitnessWage(20);
//        pop.get(4).setFitnessWage(45);
//        pop.get(5).setFitnessWage(60);
//        pop.get(6).setFitnessWage(80);
//
//        ParetoFrontsGenerator p = new ParetoFrontsGenerator(new Point(1,1), new Point(1,1));
//        p.generateFrontsWithAssignments(pop);
//        Individual ind = new Individual(new int[] {1,2},1,1);
//        ind.setFitnessWage(15);
//        ind.setFitnessTime(20);
//        pop.add(ind);
//        p.generateFrontsWithAssignments(pop);
//        pop.toArray();


    }
}