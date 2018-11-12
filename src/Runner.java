import org.omg.PortableInterceptor.INACTIVE;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Runner {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        Evolution population = new Evolution("src/definitionFiles/hard_3.ttp", 200, 100,
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






//        Individual ind1 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind2 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind3 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind4 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind5 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind6 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind7 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind8 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind9 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind10 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind11 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind12 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind13 = new Individual(new int[] {12,3}, 1, 0);
//        Individual ind14 = new Individual(new int[] {12,3}, 1, 0);
//
//
//        ArrayList<Individual> pop = new ArrayList<>();
//        ind1.setFitnessTime(49352.0536);
//        ind1.setFitnessWage(1035518);
//        ind2.setFitnessTime(51616.84212);
//        ind2.setFitnessWage(1048712);
//        ind3.setFitnessTime(51183.79461);
//        ind3.setFitnessWage(1045614);
//        ind4.setFitnessTime(51142.66989);
//        ind4.setFitnessWage(1044738);
//        ind5.setFitnessTime(51287.67375);
//        ind5.setFitnessWage(1045639);
//        ind6.setFitnessTime(51259.14258);
//        ind6.setFitnessWage(1042324);
//        ind7.setFitnessTime(51626.79863);
//        ind7.setFitnessWage(1046231);
//        ind8.setFitnessTime(51406.36415);
//        ind8.setFitnessWage(1047458);
//        ind9.setFitnessTime(49795.29874);
//        ind9.setFitnessWage(1040301);
//        ind10.setFitnessTime(50210.37409);
//        ind10.setFitnessWage(1041329);
//        ind11.setFitnessTime(48452.37727);
//        ind11.setFitnessWage(1042890);
//        ind12.setFitnessTime(50903.72257);
//        ind12.setFitnessWage(1042993);
//        ind13.setFitnessTime(51534.587);
//        ind13.setFitnessWage(1045688);
//        ind14.setFitnessTime(51243.08562);
//        ind14.setFitnessWage(1045793);
//
//        pop.add(ind1);
//        pop.add(ind12);
//        pop.add(ind13);
//        pop.add(ind14);
//        pop.add(ind2);
//        pop.add(ind3);
//        pop.add(ind4);
//        pop.add(ind5);
//        pop.add(ind6);
//        pop.add(ind7);
//        pop.add(ind8);
//        pop.add(ind9);
//        pop.add(ind10);
//        pop.add(ind11);
//
//        ParetoFrontsGenerator p = new ParetoFrontsGenerator(new Point(1,1),new Point(1,1));
//        p.generateFronts(pop);


    }
}