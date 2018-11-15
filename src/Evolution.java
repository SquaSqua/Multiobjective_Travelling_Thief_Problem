import java.util.*;

class Evolution {

    private int dimension;

        private GreedyPackingPlan greedy;
    private ParetoFrontsGenerator paretoGenerator;

    private int popSize;
    private int numOfGeners;
    private double crossProb;
    private double mutProb;
    private int tournamentSize;

    private ArrayList<Individual> population = new ArrayList<>();

    private String measures = "", firstPop = "\nFirst population\n", lastPop = "\nLast population\n";
    private StringBuilder sBMeasures = new StringBuilder(measures);
    private StringBuilder sBFirstPop = new StringBuilder(firstPop);
    private StringBuilder sBLastPop = new StringBuilder(lastPop);

    Evolution(String definitionFile, int popSize, int numOfGeners, int tournamentSize, double crossProb, double mutProb) {
        this.popSize = popSize;
        this.numOfGeners = numOfGeners;
        this.tournamentSize = tournamentSize;
        this.crossProb = crossProb;
        this.mutProb = mutProb;

        readParameters(definitionFile);
    }

    private void readParameters(String definitionFile) {
        ConfigurationProvider configProvider = new ConfigurationProvider();
        Configuration config = configProvider.readFile(definitionFile);
        greedy = new GreedyPackingPlan(config);
        dimension = config.getDimension();
        paretoGenerator = new ParetoFrontsGenerator(config.getIdeal(), config.getNadir());
    }

    String evolve() {

        ArrayList<ArrayList<Individual>> pareto = new ArrayList<>();
        initialize();
        for (int generation = 1; generation < numOfGeners; generation++) {
            paretoGenerator.generateFrontsWithAssignments(population);
            population.addAll(generateOffspring(generation));
            pareto = paretoGenerator.generateFrontsWithAssignments(population);
            population = chooseNextGeneration(pareto);
            if(generation == 1) {
                appendPopulationToStringBuilder(sBFirstPop);
            }
        }
        statistics(pareto);
        appendPopulationToStringBuilder(sBLastPop);
        sBFirstPop.append(sBLastPop);
        sBMeasures.append(sBFirstPop);
        measures = sBMeasures.toString();
        return measures;
    }

    private ArrayList<Individual> chooseNextGeneration(ArrayList<ArrayList<Individual>> pareto) {
        ArrayList<Individual> nextGeneration = new ArrayList<>();
        while (nextGeneration.size() < popSize) {
            if (pareto.get(0).size() <= popSize - nextGeneration.size()) {
                nextGeneration.addAll(pareto.get(0));
                pareto.remove(0);
            } else {
                ArrayList<Individual> firstFront = pareto.get(0);
                firstFront.sort(new CrowdingDistanceComparator());
                for (Individual ind : firstFront) {
                    if (nextGeneration.size() < popSize) {
                        nextGeneration.add(ind);
                    }
                }
            }
        }
        return nextGeneration;
    }

    private ArrayList<Individual> generateOffspring(int generation) {
        ArrayList<Individual> offspring = new ArrayList<>();
        while (offspring.size() < popSize) {
            Individual[] children = matingPool(generation);
            offspring.add(children[0]);
            if (offspring.size() < popSize) {
                offspring.add(children[1]);
            }
        }
        return offspring;
    }

    //at this point population is already filled out with rank and crowding distance
    private Individual[] matingPool(int generation) {
        Individual parent1 = tournament();
        Individual parent2 = tournament();
        Individual[] children = cycleCrossing(parent1, parent2, generation);
//        children[0].setPackingPlanAndFitness(greedy);//tu cos sie zmienia
//        System.out.println(Arrays.toString(children[0].getPackingPlan()));
        children[0].mutation(greedy);
        children[1].mutation(greedy);
        return children;
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
        for (int i = 0; i < tournamentSize; i++) {
            Individual individual = population.get(rand.nextInt(popSize));
            int rank = individual.getRank();
            if (rank < bestRank) {
                bestRank = rank;
                bestIndividual = individual;
            } else if (rank == bestRank) {
                if (bestIndividual.getCrowdingDistance() < individual.getCrowdingDistance()) {
                    bestIndividual = individual;
                }
            }
        }
        return bestIndividual;
    }

    private Individual[] cycleCrossing(Individual parent1, Individual parent2, int generation) {
        int[] p1 = parent1.getRoute();
        int[] p2 = parent2.getRoute();
        int[] ch1 = new int[p1.length];
        int[] ch2 = new int[p1.length];

        if (Math.random() < crossProb) {
            int[] route1 = new int[p1.length - 1];
            int[] route2 = new int[p1.length - 1];
            for (int i = 0; i < route1.length; i++) {
                route1[i] = p1[i];
                route2[i] = p2[i];
            }
            int[] child1 = new int[route1.length];
            int[] child2 = new int[route2.length];

            for (int i = 0; i < child1.length; i++) {
                child1[i] = -1;
                child2[i] = -1;
            }
            int beginningValue = route1[0];
            int currentInd = 0;

            boolean isSwapTurn = false;
            while (true) {
                assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                if (route1[currentInd] == route2[currentInd]) {
                    isSwapTurn = !isSwapTurn;
                }
                currentInd = findIndexOfaValue(route2[currentInd], route1);
                if (route2[currentInd] == beginningValue) {
                    assignGens(isSwapTurn, currentInd, route1, route2, child1, child2);
                    currentInd = findFirstEmpty(child1);
                    if (currentInd == -1) {
                        break;
                    }
                    beginningValue = route1[currentInd];
                    isSwapTurn = !isSwapTurn;
                }
            }
            ch1 = addLastCity(child1);
            ch2 = addLastCity(child2);
        } else {
            for (int i = 0; i < ch1.length; i++) {
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
        if (!isSwapTurn) {
            child1[currentInd] = route1[currentInd];
            child2[currentInd] = route2[currentInd];
        } else {
            child1[currentInd] = route2[currentInd];
            child2[currentInd] = route1[currentInd];
        }
    }

    private int[] addLastCity(int[] child) {
        int[] ch = new int[child.length + 1];
        System.arraycopy(child, 0, ch, 0, child.length);
        ch[ch.length - 1] = ch[0];
        return ch;
    }

    private int findFirstEmpty(int[] route) {
        int firstEmpty = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == -1) {
                firstEmpty = i;
                break;
            }
        }
        return firstEmpty;
    }

    private int findIndexOfaValue(int value, int[] route) {
        int index = -1;
        for (int i = 0; i < route.length; i++) {
            if (route[i] == value) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void statistics(ArrayList<ArrayList<Individual>> pareto) {
        sBMeasures.append(paretoGenerator.ED_measure(pareto)).append(", ")
                .append(paretoGenerator.PFS_measure(pareto)).append(", ")
                .append(paretoGenerator.HV_measure(pareto));
        sBMeasures.append("\n");
    }

    private void appendPopulationToStringBuilder(StringBuilder sB) {
        int currentRank = 0;
        sB.append("Czas podrozy").append(", ").append("Zarobek").append(", ").append("Stworzony w generacji\n");
        for (Individual i : population) {
            if (i.getRank() != currentRank) {
                currentRank++;
                sB.append("\n");
            }
            sB.append(i.getFitnessTime()).append(", ").append(i.getFitnessWage()).append(", ").append(i.getBirthday());
//                    .append(Arrays.toString(i.getRoute())).append(", ").append(Arrays.toString(i.getPackingPlan()));
            sB.append("\n");
        }
    }

    //nie dziala bo pareto jest juz modyfikowane przy chooseNextGeneration
    //da sie latwo naprawic wywolujac jeszcze raz generateFronts na populacji i przekazujac to nowe pareto,
    //ale zwyczajnie sie to nie oplaca
//    private String printPopulationHorizontally(StringBuilder sB, ArrayList<ArrayList<Individual>> pareto) {
//        int maxLength = 0;
//        for (int i = 0; i < pareto.size(); i++) {
//            ArrayList<Individual> currentFront = pareto.get(i);
//            if (maxLength < currentFront.size()) {
//                maxLength = currentFront.size();
//            }
//        }
//        for (int i = 0; i < 1/*maxLength*/; i++) {//number of row
//            for (int j = 0; j < pareto.size(); j++) {//j - number of front
//                if (pareto.get(j).size() <= i) {
//                    sB.append(",,");
//                } else {
//                    sB.append(pareto.get(j).get(i).getFitnessTime()).append(", ").append(pareto.get(j).get(i).getFitnessWage()).append(", ");
//                }
//            }
//            sB.append("\n");
//        }
//        return sB.toString();
//    }
}
