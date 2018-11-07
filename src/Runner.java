import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class Runner {
    public static void main(String[] args) {

        Evolution population = new Evolution("src/definitionFiles/medium_0.ttp", 100, 100,
                3, 0.5, 0.02);
        Date date = new Date();
        String dateAndTime = date.getMonth() + "_"  + date.getDay()
                + "_" + date.getHours() + "_" + date.getMinutes() + "_" + date.getSeconds();
        String results = "results_" + dateAndTime + ".csv";
        try
        {
            PrintWriter out = new PrintWriter(results);
            long start = System.currentTimeMillis();
            out.println(population.evolve());
            long milliseconds = System.currentTimeMillis() - start;
            System.out.println(milliseconds / 1000 + "." + milliseconds % 1000 + "s");
            out.close();
        }
        catch (FileNotFoundException ex)
        {
            System.out.println("Nie da się utworzyć pliku!");
        }
    }
}