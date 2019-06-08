import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

public class Runner {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String definitionFile = "src/definitionFiles/hard_0.ttp";
        ConfigurationProvider configProvider = new ConfigurationProvider();
        configProvider.readFile(definitionFile);

        Evolution population = new Evolution(100, 300, 6, 0.5f, 0.02f);
//        Multiobjective_Tabu_Search mots = new Multiobjective_Tabu_Search(4, 250, 25, 1000);

        searchForPareto(population);
//        searchForPareto(mots);
        countTimeUpHere(start);
    }

    private static void searchForPareto(IMetaheuristics algorithm) {
        try {
            PrintWriter out = new PrintWriter(giveName(algorithm));
            out.println(algorithm.run());
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Nie da się utworzyć pliku!");
        }
    }

    private static String giveName(IMetaheuristics algorithm) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        String dateAndTime = date.getMonth() + "_" + date.getDayOfMonth()
                + "__" + time.getHour() + "_" + time.getMinute() + "_" + time.getSecond();
        return dateAndTime + "_" + algorithm.getClass().getName() + ".csv";
    }

    private static void countTimeUpHere(long start) {
        long milliseconds = System.currentTimeMillis() - start;
            System.out.println((int)Math.floor((float)milliseconds / 60000) + " min "
                    + (milliseconds % 60000) / 1000 + "." + milliseconds % 1000 + "s");
    }
}