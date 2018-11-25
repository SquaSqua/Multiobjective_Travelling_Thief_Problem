import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class Runner {
    public static void main(String[] args) {
        String definitionFile = "src/definitionFiles/hard_3.ttp";
        long start = System.currentTimeMillis();
        ConfigurationProvider configProvider = new ConfigurationProvider();
        configProvider.readFile(definitionFile);
        Evolution population = new Evolution(100, 40, 6, 0.5f, 0.2f);

        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        String dateAndTime = date.getMonth() + "_" + date.getDayOfMonth()
                + "__" + time.getHour() + "_" + time.getMinute() + "_" + time.getSecond();
        String results = "results_" + dateAndTime + ".csv";
        try {
            PrintWriter out = new PrintWriter(results);
            out.println(population.evolve());
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Nie da się utworzyć pliku!");
        }
        long milliseconds = System.currentTimeMillis() - start;
        System.out.println(milliseconds / 1000 + "." + milliseconds % 1000 + "s");
    }
}
//todo wywolywanie glownej metody powinno nazywac sie tak samo dla kazdego algorytmu i przy pomocy abstract
//todo w zaleznosci od parametru enum albo String przekazywac o jaki algorytm nam chodzi i wtedy ma sie wywolac konkretna