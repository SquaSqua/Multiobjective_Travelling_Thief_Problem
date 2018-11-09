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
    private ArrayList<Individual> archive = new ArrayList<>();

    //just for a try
    private String measures = "", firstPop = "\nFirst population\n", lastPop = "\nLast population\n";
    private StringBuilder sBMeasures = new StringBuilder(measures);
    private StringBuilder sBFirstPop = new StringBuilder(firstPop);
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
        ArrayList<ArrayList<Individual>> pareto = new ArrayList<>();
        initialize();
        archive = paretoGenerator.generateFrontsWithAssignments(population).get(0);
        for(int generation = 1; generation < numOfGeners; generation++) {
            population.addAll(generateOffspring(generation));//2N
            pareto = paretoGenerator.generateFrontsWithAssignments(population);
            archive.addAll(paretoGenerator.ignoreClones(archive, pareto.get(0)));
            archive = paretoGenerator.generateFrontsWithAssignments(archive).get(0);
            population = chooseNextGeneration(pareto);
            if(generation == 1) {
                printPopulation(sBFirstPop, archive);
            }
        }
        ArrayList<ArrayList<Individual>> paretoPop = paretoGenerator.generateFrontsWithAssignments(population);
        statistics(paretoPop);
        ArrayList<ArrayList<Individual>> paretoArch = paretoGenerator.generateFrontsWithAssignments(archive);
        statistics(paretoArch);
        sBFirstPop.append(printPopulation(sBlastPop, archive));
        sBMeasures.append(sBFirstPop);
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

    private ArrayList<Individual> generateOffspring(int generation) {
        ArrayList<Individual> offspring = new ArrayList<>();
        while(offspring.size() < popSize) {
            Individual[] children = matingPool(generation);
            offspring.add(children[0]);
            if(offspring.size() < popSize) {
                offspring.add(children[1]);
            }
        }
        return offspring;
    }

    //at this point population is already filled out with rank and crowding distance
    private Individual[] matingPool(int generation) {
        Individual parent1 = tournament();
        Individual parent2 = tournament();
        Individual[] children = cX(parent1, parent2, generation);
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
        Individual ind = new Individual(route, mutProb, 0);
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

//    private Individual[] crossOver(Individual parent1, Individual parent2) {
//        int[] parent1Route = parent1.getRoute();
//        int[] parent2Route = parent2.getRoute();
//
//        int[] child1 = new int[parent1Route.length];
//        int[] child2 = new int[parent1Route.length];
//        if(Math.random() < crossProb) {
//            int crossPoint = new Random().nextInt(parent1Route.length);
//            for(int i = 0; i < crossPoint; i++) {
//                child1[i] = parent1Route[i];
//                child2[i] = parent2Route[i];
//            }
//            //rest for parent1
//            boolean used = false;
//            int from = 0;
//            for(int empty = crossPoint; empty < child1.length - 1;) {
//                for(int j = 0; j < empty; j++) {
//                    if(child1[j] == parent2Route[from]){
//                        used = true;
//                        break;
//                    }
//                }
//                if(!used) {
//                    child1[empty] = parent2Route[from];
//                    empty++;
//                }
//                used = false;
//                from++;
//            }
//            child1[parent1Route.length - 1] = child1[0];
//
//            //rest for parent2
//            used = false;
//            from = 0;
//            for(int empty = crossPoint; empty < child2.length - 1;) {
//                for(int j = 0; j < empty; j++) {
//                    if(child2[j] == parent1Route[from]){
//                        used = true;
//                        break;
//                    }
//                }
//                if(!used) {
//                    child2[empty] = parent1Route[from];
//                    empty++;
//                }
//                used = false;
//                from++;
//            }
//            child2[parent2Route.length - 1] = child2[0];
//        }
//        else {
//            for(int i = 0; i < child1.length; i++) {
//                child1[i] = parent1Route[i];
//                child2[i] = parent2Route[i];
//            }
//        }
//        return new Individual[] {
//                new Individual(child1, mutProb),
//                new Individual(child2, mutProb)
//        };
//    }

    public Individual[] cX(Individual parent1, Individual parent2, int generation) {
        int[] p1 = parent1.getRoute();
        int[] p2 = parent2.getRoute();
        int[] ch1 = new int[p1.length];
        int[] ch2 = new int[p1.length];

        if(Math.random() < crossProb) {
            int[] route1 = new int[p1.length - 1];
            int[] route2 = new int[p1.length - 1];
            for(int i = 0; i < route1.length; i++) {
                route1[i] = p1[i];
                route2[i] = p2[i];
            }
            int[] child1 = new int[route1.length];
            int[] child2 = new int[route2.length];

            for(int i = 0; i < child1.length; i++) {
                child1[i] = -1;
                child2[i] = -1;
            }
            int beginningValue = route1[0];
            int currentInd = 0;

            boolean isSwapTurn = false;
            while(true) {
                assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                if(route1[currentInd] == route2[currentInd]) {
                    isSwapTurn = !isSwapTurn;
                }
                currentInd = findIndexOfaValue(route2[currentInd], route1);
                if(route2[currentInd] == beginningValue) {
                    assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                    currentInd = findFirstEmpty(child1);
                    if(currentInd == -1) {
                        break;
                    }
                    beginningValue = route1[currentInd];
                    isSwapTurn = !isSwapTurn;
                }
            }
            ch1 = addLastCity(child1);
            ch2 = addLastCity(child2);
        }
        else {
            for(int i = 0; i < ch1.length; i++) {
                ch1[i] = p1[i];
                ch2[i] = p2[i];
            }
        }
        return new Individual[]{
                new Individual(ch1, mutProb, generation),
                new Individual(ch2, mutProb, generation)
                };
    }

    private void assignGens(boolean isSwapTurn, int currentInd, int[] route1, int[] route2, int[] child1, int[] child2) {
        if(!isSwapTurn) {
            child1[currentInd] = route1[currentInd];
            child2[currentInd] = route2[currentInd];
        }
        else {
            child1[currentInd] = route2[currentInd];
            child2[currentInd] = route1[currentInd];
        }
    }

    private int[] addLastCity(int[] child) {
        int[] ch = new int[child.length + 1];
        for(int i = 0; i < child.length; i++) {
            ch[i] = child[i];
        }
        ch[ch.length - 1] = ch[0];
        return ch;
    }

    private int findFirstEmpty(int[] route) {
        int firstEmpty = -1;
        for(int i = 0; i < route.length; i++) {
            if(route[i] == -1) {
                firstEmpty = i;
                break;
            }
        }
        return firstEmpty;
    }

    private int findIndexOfaValue(int value, int[] route) {
        int index = -1;
        for(int i = 0; i < route.length; i++) {
            if(route[i] == value) {
                index = i;
                break;
            }
        }
        return index;
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

    private String printPopulation(StringBuilder sB, ArrayList<Individual> group) {
        int currentRank = 0;
        for(Individual i : group) {
            if(i.getRank() != currentRank) {
                currentRank++;
//                sB.append("\n");
            }
            sB.append(i.getFitnessWage()).append(", ").append(i.getFitnessTime()).append(", ").append(i.getBirthday());
                    //.append(", ").append(Arrays.toString(i.getRoute())).append(", ")
                    // .append(Arrays.toString(i.getPackingPlan()));
                    // for printing packingPlan and route
            sB.append("\n");
        }
        return sB.toString();
    }
}
