import org.omg.PortableInterceptor.INACTIVE;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Runner {
    public static void main(String[] args) {

//        long start = System.currentTimeMillis();
//        Evolution population = new Evolution("src/definitionFiles/hard_3.ttp", 200, 100,
//                6, 0.5, 0.2);
//
//        Date date = new Date();
//        String dateAndTime = date.getMonth() + "_"  + date.getDay()
//                + "_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getSeconds();
//        String results = "results_" + dateAndTime + ".csv";
//        try
//        {
//            PrintWriter out = new PrintWriter(results);
//            out.println(population.evolve());
//            out.close();
//        }
//        catch (FileNotFoundException ex)
//        {
//            System.out.println("Nie da się utworzyć pliku!");
//        }
//        long milliseconds = System.currentTimeMillis() - start;
//        System.out.println(milliseconds / 1000 + "." + milliseconds % 1000 + "s");

        Individual ind1 = new Individual(new int[] {1,2,3}, 1, 0);
        Individual ind2 = new Individual(new int[] {1,2,3}, 1, 0);

        ind1.setFitnessTime(10);
        ind1.setFitnessWage(100);

        ind2.setFitnessTime(9);
        ind2.setFitnessWage(100);

        System.out.println(ind1.compareTo(ind2));
    }
}