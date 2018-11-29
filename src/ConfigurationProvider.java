import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConfigurationProvider {

    private static final char NUMBER_OF_INFO_PER_CITY = 3;
    private static final char COORDINATE_X_OF_CITY = 1;
    private static final char COORDINATE_Y_OF_CITY = 2;

    private static final char NUMBER_OF_INFO_PER_ITEM = 4;
    static final char INDEX_OF_ITEM = 0;
    static final char PROFIT_FROM_ITEM = 1;
    static final char WEIGHT_OF_ITEM = 2;
    static final char CITY_OF_ITEM = 3;

    void readFile(String definitionFile) {
        int dimension;
        int numOfItems;
        double[][] cities;
        int[][] items;
        double[][] distances;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(definitionFile));
            reader.readLine();//PROBLEM NAME
            reader.readLine();//KNAPSACK DATA TYPE
            dimension = (int)getNumber(reader.readLine());
            Configuration.setDimension(dimension);
            numOfItems = (int)getNumber(reader.readLine());
            Configuration.setCapacity((int)getNumber(reader.readLine()));
            Configuration.setMinSpeed(getNumber(reader.readLine()));
            Configuration.setMaxSpeed(getNumber(reader.readLine()));
            Configuration.setRentingRatio(getNumber(reader.readLine()));
            reader.readLine();//EDGE_WEIGHT_TYPE
            reader.readLine();//NODE_COORD_SECTION...
            cities = new double[dimension][NUMBER_OF_INFO_PER_CITY];
            for (int i = 0; i < dimension; i++) {//filling out cities array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < NUMBER_OF_INFO_PER_CITY; j++) {
                    cities[i][j] = Double.parseDouble(st.nextToken());
                }
            }
            distances = createDistancesArray(dimension, cities);
            Configuration.setDistances(distances);
            reader.readLine();
            items = new int[numOfItems][NUMBER_OF_INFO_PER_ITEM];
            for (int i = 0; i < numOfItems; i++) {//filling out items array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < NUMBER_OF_INFO_PER_ITEM; j++) {
                    items[i][j] = Integer.parseInt(st.nextToken());
                }
            }
            Configuration.setItems(items);
            Configuration.setIdeal(countPoint(true, distances, dimension, items));
            Configuration.setNadir(countPoint(false, distances, dimension, items));
        } catch (FileNotFoundException fnfe) {
            System.out.println("A file doesn't exist or is in use now!");
        } catch (Exception e) {
            System.out.println("An error has occurred while reading data: " + e);
        }
    }

    private double[][] createDistancesArray(int dimension, double[][] cities) {
        double[][] distances = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                double distance;
                if (i == j) {
                    distance = 0;
                } else {
                    double a = cities[i][COORDINATE_X_OF_CITY] - cities[j][COORDINATE_X_OF_CITY];
                    double b = cities[i][COORDINATE_Y_OF_CITY] - cities[j][COORDINATE_Y_OF_CITY];
                    distance = Math.sqrt( a * a + b * b);
                }
                distances[i][j] = distance;
                distances[j][i] = distance;//redundant
            }
        }
        return distances;
    }

    private double getNumber(String line) {
        Pattern p = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher m = p.matcher(line);
        return m.find() ? Double.parseDouble(m.group()) : 0;
    }

    //for time as y and wage as x
    private Point countPoint(boolean isIdeal, double[][] distances, int dimension, int[][] items) {
        double time;
        int wage;
        Point point;
        if (isIdeal) {
            time = Double.MAX_VALUE;
            wage = Integer.MIN_VALUE;
            for (int i = 0; i < distances.length; i++) {
                for (int j = i + 1; j < distances[i].length; j++) {
                    if (time > distances[i][j] && distances[i][j] != 0) {
                        time = distances[i][j];
                    }
                }
            }
            for (int[] item : items) {
                if (wage < item[PROFIT_FROM_ITEM]) {
                    wage = item[PROFIT_FROM_ITEM];
                }
            }
            point = new Point(wage * items.length, time * dimension);
        } else {
            time = Double.MIN_VALUE;
            wage = Integer.MAX_VALUE;
            for (int i = 0; i < distances.length; i++) {
                for (int j = i + 1; j < distances[i].length; j++) {
                    if (time < distances[i][j] && distances[i][j] != 0) {
                        time = distances[i][j];
                    }
                }
            }
            for (int[] item : items) {
                if (wage > item[PROFIT_FROM_ITEM]) {
                    wage = item[PROFIT_FROM_ITEM];
                }
            }
            point = new Point(wage * items.length, time * dimension);
        }
        return point;
    }
}
