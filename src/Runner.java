import org.omg.PortableInterceptor.INACTIVE;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Runner {
    public static void main(String[] args) {

        long start = System.currentTimeMillis();
        Evolution population = new Evolution("src/definitionFiles/medium_3.ttp", 200, 100,
                3, 0.5, 1);

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
    }
}