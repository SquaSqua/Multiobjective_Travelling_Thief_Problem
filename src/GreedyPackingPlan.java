import java.util.ArrayList;

class GreedyPackingPlan {

    private double coefficient;
    private double maxSpeed;
    private int capacity;
    private int dimension;
    private double[][] distances;
    private int[][] items;
    private Integer[][] groupedItems;

    GreedyPackingPlan(Configuration config) {
        this.maxSpeed = config.getMaxSpeed();
        this.capacity = config.getCapacity();
        this.dimension = config.getDimension();
        this.distances = config.getDistances();
        this.items = config.getItems();

        coefficient = (maxSpeed - config.getMinSpeed()) / capacity;
        createGroupedItemsArray();
    }

    private double countTime(int[] route, int startIndex, double currentSpeed) {
        int endIndex = route.length - 1;
        return countTime(route, startIndex, endIndex, currentSpeed);
    }

    private double countTime(int[] route, int startIndex, int endIndex, double currentSpeed) {
        return countRoad(route, startIndex, endIndex) / currentSpeed;
    }

    private double countRoad(int[] route, int startIndex, int endIndex) {
        double completeDistance = 0;
        if(endIndex == route.length - 1) {
            for(int i = startIndex; i < endIndex - 1; ) {
                completeDistance += distances[route[i]][route[++i]];
            }
            completeDistance += distances[route.length - 2][0];
        }
        else {
            for(int i = startIndex; i < endIndex; i++) {
                completeDistance += distances[route[i]][route[i + 1]];
            }
        }

        return completeDistance;
    }

    private double countSpeed(double currentWeight) {
        return maxSpeed - (currentWeight * coefficient);
    }

    void settlePackingPlan(Individual individual) {
        int[] route = individual.getRoute();
        ArrayList<double[]> gainOfItems = countGain(route);
        int[] packingPlan = new int[items.length];
        int ind = 0;
        int currentWeight = 0;
        while(currentWeight < capacity && ind < gainOfItems.size()) {
            int rowNumber = (int)gainOfItems.get(ind)[0];
            if(items[rowNumber][2] + currentWeight <= capacity) {
                packingPlan[rowNumber] = 1;
                currentWeight += items[rowNumber][2];
            }
            ind++;
        }
        individual.setPackingPlan(packingPlan);
    }

    private ArrayList<double[]> countGain(int[] route) {
        ArrayList<double[]> gainOfItems = new ArrayList<>();
        for(int i = 0; i < items.length; i++) {
            int[] currentRow = items[i];
            gainOfItems.add(new double[] {i, currentRow[ConfigurationProvider.PROFIT_FROM_ITEM] /
                    (currentRow[ConfigurationProvider.WEIGHT_OF_ITEM]
                            * countTime(route, currentRow[ConfigurationProvider.CITY_OF_ITEM],
                            countSpeed(currentRow[ConfigurationProvider.WEIGHT_OF_ITEM])))});
        }
        gainOfItems.sort((double[] o1, double[] o2) ->
                o2[1] - o1[1] < 0 ? -1 : o2[1] > 0 ? 1 : 0);

        return gainOfItems;
    }

    void setFitnessForIndividual(Individual individual) {
        countFitnessTime(individual);
        countFitnessWage(individual);
    }

    private void countFitnessWage(Individual individual) {
        int[] packingPlan = individual.getPackingPlan();
        int totalWage = 0;
        for(int i = 0; i < packingPlan.length; i++) {
            if(packingPlan[i] == 1) {
                totalWage += items[i][ConfigurationProvider.PROFIT_FROM_ITEM];
            }
        }
        individual.setFitnessWage(totalWage);
    }

    private void countFitnessTime(Individual individual) {
        int[] route = individual.getRoute();
        int[] packingPlan = individual.getPackingPlan();
        double weight = 0;
        double time = 0;
        for(int currentPosition = 0; currentPosition < route.length - 1; ) {
            Integer[] currentCity = groupedItems[currentPosition];
            for (Integer item : currentCity) {
                if (packingPlan[item] == 1) {//taken
                    weight += items[item][ConfigurationProvider.WEIGHT_OF_ITEM];
                }
            }
            time += countTime(route, currentPosition, ++currentPosition, countSpeed(weight));
        }
        individual.setFitnessTime(time);
    }

    private void createGroupedItemsArray() {
        groupedItems = new Integer[dimension][];
        ArrayList<Integer>[] groupedItemsList = new ArrayList[dimension];
        for(int i = 0; i < dimension; i++) {
            groupedItemsList[i] = new ArrayList<>();
        }
        for (int[] item : items) {
            groupedItemsList[item[ConfigurationProvider.CITY_OF_ITEM] - 1].add(item[ConfigurationProvider.INDEX_OF_ITEM] - 1);
        }
        for(int i = 0; i < dimension; i++) {
            groupedItems[i] = new Integer[groupedItemsList[i].size()];
            for (int j = 0; j < groupedItemsList[i].size(); j++) {
                groupedItems[i][j] = groupedItemsList[i].get(j);
            }
        }
    }
}
