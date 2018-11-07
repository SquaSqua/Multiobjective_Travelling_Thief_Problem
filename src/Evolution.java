import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Evolution {

    private String definitionFile;
    private int dimension;
    private double[][] cities;

    private GreedyPackingPlan greedy;
    private ParetoFrontsGenerator paretoGenerator;

    private int popSize;
    private int numOfGeners;
    private double crossProb;
    private double mutProb;
    private int tournamentSize;

    private ArrayList<Individual> population = new ArrayList<>();

    //just for a try
    private String measures = "", firstPop = "\nFirst population\n", lastPop = "\nLast population\n";
    private StringBuilder sBMeasures = new StringBuilder(measures);
    private StringBuilder sBfirstPop = new StringBuilder(firstPop);
    private StringBuilder sBlastPop = new StringBuilder(lastPop);

    Evolution(String definitionFile, int popSize, int numOfGeners, int tournamentSize, double crossProb, double mutProb) {
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
            double[][] distances = createDistancesArray();
            for (int i = 0; i < numOfItems; i++) {//filling out items array
                StringTokenizer st = new StringTokenizer(reader.readLine(), " \t");
                for(int j = 0; j < 4; j++) {
                    items[i][j] = Integer.parseInt(st.nextToken());
                }
                Point ideal = countPoint(true, distances, dimension, items);
                Point nadir = countPoint(false, distances, dimension, items);
                paretoGenerator = new ParetoFrontsGenerator(ideal, nadir);
            }
            greedy = new GreedyPackingPlan(minSpeed, maxSpeed, capacity, dimension, distances, items);
        } catch (FileNotFoundException fnfe) {
            System.out.println("A file doesn't exist or is in use now!");
        } catch (Exception e) {
            System.out.println("An error has occurred while reading data: " + e);
        }

    }

    String evolve() {

        initialize();
        for(int generation = 1; generation < numOfGeners; generation++) {
            paretoGenerator.generateFrontsWithAssignments(population);
            population.addAll(generateOffspring());
            ArrayList<ArrayList<Individual>> pareto = paretoGenerator.generateFrontsWithAssignments(population);
            statistics(pareto);//todo which statistics should I print?
            population = chooseNextGeneration(pareto);
            if(generation == 1) {
                printPopulation(sBfirstPop);
            }
        }
        sBfirstPop.append(printPopulation(sBlastPop));
        sBMeasures.append(sBfirstPop);
        measures = sBMeasures.toString();
        return measures;
    }

    private ArrayList<Individual> chooseNextGeneration(ArrayList<ArrayList<Individual>> pareto) {
        ArrayList<Individual> nextGeneration = new ArrayList<>();
        while(nextGeneration.size() < popSize) {
            if(pareto.get(0).size() <= popSize - nextGeneration.size()) {
                nextGeneration.addAll(pareto.get(0));
                pareto.remove(0);
            }
            else {
                ArrayList<Individual> firstFront = pareto.get(0);
                firstFront.sort(new CrowdingDistanceComparator());
                for(Individual ind : firstFront) {
                    if(nextGeneration.size() < popSize) {
                        nextGeneration.add(ind);
                    }
                }
            }
        }
        return nextGeneration;
    }

    private ArrayList<Individual> generateOffspring() {
        ArrayList<Individual> offspring = new ArrayList<>();
        while(offspring.size() < popSize) {
            Individual[] children = matingPool();
            offspring.add(children[0]);
            if(offspring.size() < popSize) {
                offspring.add(children[1]);
            }
        }
        return offspring;
    }

    //at this point population is already filled out with rank and crowding distance
    private Individual[] matingPool() {
        Individual parent1 = tournament();
        Individual parent2 = tournament();
        Individual[] children = crossOver(parent1.getRoute(), parent2.getRoute());
//        children[0].setPackingPlanAndFitness(greedy);//tu cos sie zmienia
//        System.out.println(Arrays.toString(children[0].getPackingPlan()));
        children[0].mutation(greedy);
        children[1].mutation(greedy);
        return children;
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
            for (int[] item : items) {
                if (wage < item[1]) {
                    wage = item[1];
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
            for (int[] item : items) {
                if (wage > item[1]) {
                    wage = item[1];
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

    private void initialize() {
        for (int i = 0; i < popSize; i++) {
            population.add(generateRandomInd());
        }
    }

    private Individual generateRandomInd() {
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
        ind.setPackingPlanAndFitness(greedy);
        return ind;
    }

    private Individual tournament() {

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
                    bestIndividual = individual;
                }
            }
        }
        return bestIndividual;
    }

    private Individual[] crossOver(int[] parent1, int[] parent2) {
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
            for(int i = 0; i < child1.length; i++) {
                child1[i] = parent1[i];
                child2[i] = parent2[i];
            }
        }
        return new Individual[] {
                new Individual(child1, mutProb),
                new Individual(child2, mutProb)
        };
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

    private void statistics(ArrayList<ArrayList<Individual>> pareto) {
        sBMeasures.append(paretoGenerator.ED_measure(pareto)).append(", ")
                .append(paretoGenerator.PFS_measure(pareto)).append(", ")
                .append(paretoGenerator.HV_measure(pareto));
        sBMeasures.append("\n");
    }

    private String printPopulation(StringBuilder sB) {
        int currentRank = 0;
        for(Individual i : population) {
            if(i.getRank() != currentRank) {
                currentRank++;
                sB.append("\n");
            }
            sB.append(i.getFitnessTime()).append(", ").append(i.getFitnessWage()).append(", ")
                    .append(Arrays.toString(i.getRoute())).append(", ").append(Arrays.toString(i.getPackingPlan()));
            sB.append("\n");
        }
        return sB.toString();
    }
}
