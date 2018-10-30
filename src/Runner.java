import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Runner {
    public static void main(String[] args) {
        Evolution population = new Evolution("src/definitionFiles/easy_0.ttp", 10, 2,
                2, 0, 0);
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