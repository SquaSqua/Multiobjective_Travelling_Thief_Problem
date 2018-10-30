import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Evolution {
    private String definitionFile;
    private int dimension;
    private double[][] cities;

    private GreedyPackingPlan greedy;

    private int popSize;
    private int numOfGeners;
    private double crossProb;
    private double mutProb;
    private int tournamentSize;

    private ArrayList<Individual> population = new ArrayList<>();

    public Evolution(String definitionFile, int popSize, int numOfGeners, int tournamentSize, double crossProb, double mutProb) {
        this.definitionFile = definitionFile;
        readParameters();

        this.popSize = popSize;
        this.numOfGeners = numOfGeners;
        this.tournamentSize = tournamentSize;
        this.crossProb = crossProb;
        this.mutProb = mutProb;
    }

    private void readParameters() {
        BufferedReader reader;
        int[][] items;
        double[][] distances = createDistancesArray();
        int capacity;
        int numOfItems;
        double minSpeed;
        double maxSpeed;
        try {
            reader = new BufferedReader(new FileReader(definitionFile));
            reader.readLine();//PROBLEM NAME
            reader.readLine();//KNAPSACK DATA TYPE
            dimension = (int)getNumber(reader.readLine());
            numOfItems = (int)getNumber(reader.readLine());
            capacity = (int)getNumber(reader.readLine());
            minSpeed = getNumber(reader.readLine());
            maxSpeed = getNumber(reader.readLine());
            reader.readLine();//RENTING RATIO
            reader.readLine();//EDGE_WEIGHT_TYPE
            reader.readLine();//NODE_COORD_SECTION...
            cities = new double[dimension][3];
            items = new int[numOfItems][4];
            for (int i = 0; i < dimension; i++) {//filling out cities array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < 3; j++) {
                    cities[i][j] = Double.parseDouble(st.nextToken());
                }
            }
            reader.readLine();
            for (int i = 0; i < numOfItems; i++) {//filling out items array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < 4; j++) {
                    items[i][j] = Integer.parseInt(st.nextToken());
                }
                Point ideal = countPoint(true, distances, dimension, items);
                Point nadir = countPoint(false, distances, dimension, items);
            }
            greedy = new GreedyPackingPlan(minSpeed, maxSpeed, capacity, dimension, distances, items);
        } catch (FileNotFoundException fnfe) {
            System.out.println("A file doesn't exist or is in use now!");
        } catch (Exception e) {
            System.out.println("An error has occurred while reading data: " + e);
        }

    }

    //for time as y and wage as x
    private Point countPoint(boolean isIdeal, double[][] distances, int dimension, int[][] items) {
        double time;
        int wage;
        Point point;
        if(isIdeal){
            time = Double.MAX_VALUE;
            wage = Integer.MIN_VALUE;
            for(int i = 0; i < distances.length; i++) {
                for(int j = i + 1; j < distances[i].length; j++) {
                    if(time > distances[i][j] && distances[i][j] != 0) {
                        time = distances[i][j];
                    }
                }
            }
            for(int i = 0; i < items.length; i++) {
                if(wage < items[i][1]) {
                    wage = items[i][1];
                }
            }
            point = new Point(wage * items.length, time * dimension);
        }
        else {
            time = Double.MIN_VALUE;
            wage = Integer.MAX_VALUE;
            for(int i = 0; i < distances.length; i++) {
                for(int j = i + 1; j < distances[i].length; j++) {
                    if(time > distances[i][j] && distances[i][j] != 0) {
                        time = distances[i][j];
                    }
                }
            }
            for(int i = 0; i < items.length; i++) {
                if(wage < items[i][1]) {
                    wage = items[i][1];
                }
            }
            point = new Point(wage * items.length, time * dimension);
        }
        return point;
    }

    private double getNumber(String line) {
        Pattern p = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher m = p.matcher(line);
        m.find();
        return Double.parseDouble(m.group());
    }

    public void initialize() {
        for (int i = 0; i < popSize; i++) {
            population.add(generateRandomInd());
        }
    }

    public Individual generateRandomInd() {
        int[] route = new int[dimension + 1];
        ArrayList<Integer> routeList = new ArrayList<>();
        for (int i = 0; i < dimension; i++) {
            routeList.add(i);
        }
        Collections.shuffle(routeList);
        for (int i = 0; i < dimension; i++) {
            route[i] = routeList.get(i);
        }
        route[dimension] = route[0];
        Individual ind = new Individual(route, mutProb);
        ind.setPackingPlan(route);

        return ind;
    }

    //POBIERAM CROWDINGDISTANCE I RANK Z POPULACJI, GDZIE NIE SA WPISANE
    public Individual tournament() {
        Individual bestIndividual = population.get(0);//just any individual to initialize
        int bestRank = Integer.MAX_VALUE;
        Random rand = new Random();
        for(int i = 0; i < tournamentSize; i++) {
            Individual individual = population.get(rand.nextInt(popSize));
            int rank = individual.getRank();
            if (rank < bestRank) {
                bestRank = rank;
                bestIndividual = individual;
            }
            else if(rank == bestRank) {
                if(bestIndividual.getCrowdingDistance() < individual.getCrowdingDistance()) {
                    bestRank = rank;
                    bestIndividual = individual;
                }
            }
        }
        return bestIndividual;
    }

    public int[][] crossingOver(int[] parent1, int[] parent2) {
        int[] child1 = new int[parent1.length];
        int[] child2 = new int[parent1.length];
        if(Math.random() < crossProb) {
            int crossPoint = new Random().nextInt(parent1.length);
            for(int i = 0; i < crossPoint; i++) {
                child1[i] = parent1[i];
                child2[i] = parent2[i];
            }
            //rest for parent1
            boolean used = false;
            int from = 0;
            for(int empty = crossPoint; empty < child1.length - 1;) {
                for(int j = 0; j < empty; j++) {
                    if(child1[j] == parent2[from]){
                        used = true;
                        break;
                    }
                }
                if(!used) {
                    child1[empty] = parent2[from];
                    empty++;
                }
                used = false;
                from++;
            }
            child1[parent1.length - 1] = child1[0];

            //rest for parent2
            used = false;
            from = 0;
            for(int empty = crossPoint; empty < child2.length - 1;) {
                for(int j = 0; j < empty; j++) {
                    if(child2[j] == parent1[from]){
                        used = true;
                        break;
                    }
                }
                if(!used) {
                    child2[empty] = parent1[from];
                    empty++;
                }
                used = false;
                from++;
            }
            child2[parent2.length - 1] = child2[0];
        }
        else {
            child1 = parent1;
            child2 = parent2;
        }
        return new int[][] {child1, child2};
    }

    private double[][] createDistancesArray() {
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
}
