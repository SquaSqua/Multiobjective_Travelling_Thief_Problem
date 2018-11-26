import java.util.*;

class Evolution implements IMetaheuristics {

    private int dimension;
    private int popSize;
    private int numOfGeners;
    private float crossProb;
    private float mutProb;
    private int tournamentSize;

    private ArrayList<Individual> population = new ArrayList<>();

    private String measures = "", firstPopFront = "\nFirst population front\n",
            middlePopFront = "\nMiddle population front\n", lastPopFront = "\nLast population front\n";
    private StringBuilder sBMeasures = new StringBuilder(measures);
    private StringBuilder sBFirstPopFront = new StringBuilder(firstPopFront);
    private StringBuilder sBMiddlePopFront = new StringBuilder(middlePopFront);
    private StringBuilder sBLastPopFront = new StringBuilder(lastPopFront);

    Evolution(int popSize, int numOfGeners, int tournamentSize, float crossProb, float mutProb) {
        this.popSize = popSize;
        this.numOfGeners = numOfGeners;
        this.tournamentSize = tournamentSize;
        this.crossProb = crossProb;
        this.mutProb = mutProb;

        dimension = Configuration.getDimension();
    }

    /**
     * cos robi
     * @return zwraca...
     */
    public String run() {
        ArrayList<ArrayList<Individual>> paretoFronts;
        initialize();
        for (int generation = 1; generation < numOfGeners; generation++) {
            ParetoFrontsGenerator.generateFrontsWithAssignments(population);
            population.addAll(generateOffspring(generation));
            paretoFronts = ParetoFrontsGenerator.generateFrontsWithAssignments(population);
            population = chooseNextGeneration(paretoFronts);
            if(generation == 1) {
//                appendPopulationToStringBuilder(sBFirstPopFront);
                appendParetoFrontToStringBuilder(sBFirstPopFront);
                statistics(paretoFronts);
            }
        }
        paretoFronts = ParetoFrontsGenerator.generateFrontsWithAssignments(population);
        statistics(paretoFronts);
        sBMeasures.append(Configuration.getNadir().x).append(", ").append(Configuration.getNadir().y).append("\n");
        sBMeasures.append(Configuration.getIdeal().x).append(", ").append(Configuration.getIdeal().y).append("\n");
//        appendPopulationToStringBuilder(sBLastPopFront);
        appendParetoFrontToStringBuilder(sBLastPopFront);
        sBMiddlePopFront.append(sBLastPopFront);
        sBFirstPopFront.append(sBMiddlePopFront);
        sBMeasures.append(sBFirstPopFront);
        measures = sBMeasures.toString();
        return measures;
    }

    private void initialize() {
        for (int i = 0; i < popSize; i++) {
            population.add(new Individual_NSGA_II(dimension));
            population.get(population.size() - 1).setPackingPlanAndFitness();
        }
    }

    private ArrayList<Individual_NSGA_II> generateOffspring(int generation) {
        ArrayList<Individual_NSGA_II> offspring = new ArrayList<>();
        while (offspring.size() < popSize) {
            Individual_NSGA_II[] children = matingPool(generation);
            offspring.add(children[0]);
            if (offspring.size() < popSize) {
                offspring.add(children[1]);
            }
        }
        return offspring;
    }

    //at this point population is already filled out with rank and crowding distance
    private Individual_NSGA_II[] matingPool(int generation) {
        Individual_NSGA_II parent1 = tournament();
        Individual_NSGA_II[] children = parent1.cycleCrossing(tournament(), crossProb, generation);
        children[0].mutate(mutProb);
        children[1].mutate(mutProb);
        return children;
    }

    private Individual_NSGA_II tournament() {

        Individual_NSGA_II bestIndividual = (Individual_NSGA_II)population.get(0);//just any individual to initialize
        int bestRank = Integer.MAX_VALUE;
        Random rand = new Random();
        for (int i = 0; i < tournamentSize; i++) {
            Individual_NSGA_II individual = (Individual_NSGA_II)population.get(rand.nextInt(popSize));
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

    private ArrayList<Individual> chooseNextGeneration(ArrayList<ArrayList<Individual>> pareto) {
        ArrayList<ArrayList<Individual>> temporaryPareto = new ArrayList<>(pareto);
        ArrayList<Individual> nextGeneration = new ArrayList<>();
        while (nextGeneration.size() < popSize) {
            if (temporaryPareto.get(0).size() <= popSize - nextGeneration.size()) {
                nextGeneration.addAll(temporaryPareto.get(0));
                temporaryPareto.remove(0);
            } else {
                ArrayList<Individual> firstFront = temporaryPareto.get(0);
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

    private void statistics(ArrayList<ArrayList<Individual>> pareto) {
        sBMeasures.append(ParetoFrontsGenerator.ED_measure(pareto)).append(", ")
                .append(ParetoFrontsGenerator.PFS_measure(pareto)).append(", ")
                .append(ParetoFrontsGenerator.HV_measure(pareto));
        sBMeasures.append("\n");
    }

//    private void appendPopulationToStringBuilder(StringBuilder sB) {
//        int currentRank = 0;
//        sB.append("Czas podrozy").append(", ").append("Zarobek").append(", ").append("Stworzony w generacji\n");
//        for (Individual i : population) {
//            if (i.getRank() != currentRank) {
//                currentRank++;
//                sB.append("\n");
//            }
//            sB.append(i.getFitnessTime()).append(", ").append(i.getFitnessWage()).append(", ").append(i.getBirthday());
////                    .append(Arrays.toString(i.getRoute())).append(", ").append(Arrays.toString(i.getPackingPlan()));
//            sB.append("\n");
//        }
//    }

    private void appendParetoFrontToStringBuilder(StringBuilder sB) {
        sB.append("Czas podrozy").append(", ").append("Zarobek").append(", ").append("Stworzony w generacji\n");
        for (Individual i : population) {
            if (i.getRank() == 0) {
                sB.append(i.getFitnessTime()).append(", ").append(i.getFitnessWage()).append(", ").append(i.getBirthday());
//                    .append(Arrays.toString(i.getRoute())).append(", ").append(Arrays.toString(i.getPackingPlan()));
                sB.append("\n");
            }
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
