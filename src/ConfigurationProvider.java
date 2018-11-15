import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConfigurationProvider {

    Configuration readFile(String definitionFile) {
        Configuration config = new Configuration();
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
            config.setDimension(dimension);
            numOfItems = (int)getNumber(reader.readLine());
            config.setCapacity((int)getNumber(reader.readLine()));
            config.setMinSpeed(getNumber(reader.readLine()));
            config.setMaxSpeed(getNumber(reader.readLine()));
            config.setRentingRatio(getNumber(reader.readLine()));
            reader.readLine();//EDGE_WEIGHT_TYPE
            reader.readLine();//NODE_COORD_SECTION...
            cities = new double[dimension][3];//0. - index, 1. - x, 2. - y
            for (int i = 0; i < dimension; i++) {//filling out cities array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < 3; j++) {
                    cities[i][j] = Double.parseDouble(st.nextToken());
                }
            }
            distances = createDistancesArray(dimension, cities);
            config.setDistances(distances);
            reader.readLine();
            items = new int[numOfItems][4];//0. - INDEX, 1. - PROFIT, 2. - WEIGHT, 3. - ASSIGNED NODE NUMBER
            for (int i = 0; i < numOfItems; i++) {//filling out items array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < 4; j++) {
                    items[i][j] = Integer.parseInt(st.nextToken());
                }
            }
            config.setItems(items);
            config.setIdeal(countPoint(true, distances, dimension, items));
            config.setNadir(countPoint(false, distances, dimension, items));
        } catch (FileNotFoundException fnfe) {
            System.out.println("A file doesn't exist or is in use now!");
        } catch (Exception e) {
            System.out.println("An error has occurred while reading data: " + e);
        }
        return config;
    }

    private double[][] createDistancesArray(int dimension, double[][] cities) {
        double[][] distances = new double[dimension][dimension];
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                double distance;
                if (i == j) {
                    distance = 0;
                } else {
                    distance = Math.sqrt(Math.abs(cities[i][1] - cities[j][1])
                            + Math.abs(cities[i][2] - cities[j][2]));
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
        m.find();
        return Double.parseDouble(m.group());
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
                if (wage < item[1]) {
                    wage = item[1];
                }
            }
            point = new Point(wage * items.length, time * dimension);
        } else {
            time = Double.MIN_VALUE;
            wage = Integer.MAX_VALUE;
            for (int i = 0; i < distances.length; i++) {
                for (int j = i + 1; j < distances[i].length; j++) {
                    if (time > distances[i][j] && distances[i][j] != 0) {
                        time = distances[i][j];
                    }
                }
            }
            for (int[] item : items) {
                if (wage > item[1]) {
                    wage = item[1];
                }
            }
            point = new Point(wage * items.length, time * dimension);
        }
        return point;
    }
}
