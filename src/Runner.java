import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Runner {
    public static void main(String[] args) {

        Evolution population = new Evolution("src/definitionFiles/easy_0.ttp", 50, 50,
                3, 0.5, 0.02);
        String results = "results.csv";
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
    }
}