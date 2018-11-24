import java.util.ArrayList;

class GreedyPackingPlan {

    private static double coefficient;
    private static double maxSpeed;
    private static int capacity;
    private static int dimension;
    private static double[][] distances;
    private static int[][] items;
    private static Integer[][] groupedItems;

    GreedyPackingPlan() {
        maxSpeed = Configuration.getMaxSpeed();
        capacity = Configuration.getCapacity();
        dimension = Configuration.getDimension();
        distances = Configuration.getDistances();
        items = Configuration.getItems();

        coefficient = (maxSpeed - Configuration.getMinSpeed()) / capacity;
        createGroupedItemsArray();
    }

    private static double countTime(short[] route, int startIndex, double currentSpeed) {
        int endIndex = route.length - 1;
        return countTime(route, startIndex, endIndex, currentSpeed);
    }

    private static double countTime(short[] route, int startIndex, int endIndex, double currentSpeed) {
        return countRoad(route, startIndex, endIndex) / currentSpeed;
    }

    private static double countRoad(short[] route, int startIndex, int endIndex) {
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

    private static double countSpeed(double currentWeight) {
        return maxSpeed - (currentWeight * coefficient);
    }

    static void settlePackingPlan(Individual individual) {
        short[] route = individual.getRoute();
        ArrayList<double[]> gainOfItems = countGain(route);
        boolean[] packingPlan = new boolean[items.length];
        int ind = 0;
        int currentWeight = 0;
        while(currentWeight < capacity && ind < gainOfItems.size()) {
            int rowNumber = (int)gainOfItems.get(ind)[0];
            if(items[rowNumber][2] + currentWeight <= capacity) {
                packingPlan[rowNumber] = true;
                currentWeight += items[rowNumber][2];
            }
            ind++;
        }
        individual.setPackingPlan(packingPlan);
    }

    private static ArrayList<double[]> countGain(short[] route) {
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

    static void setFitnessForIndividual(Individual individual) {
        countFitnessTime(individual);
        countFitnessWage(individual);
    }

    private static void countFitnessWage(Individual individual) {
        boolean[] packingPlan = individual.getPackingPlan();
        int totalWage = 0;
        for(int i = 0; i < packingPlan.length; i++) {
            if(packingPlan[i]) {
                totalWage += items[i][ConfigurationProvider.PROFIT_FROM_ITEM];
            }
        }
        individual.setFitnessWage(totalWage);
    }

    private static void countFitnessTime(Individual individual) {
        short[] route = individual.getRoute();
        boolean[] packingPlan = individual.getPackingPlan();
        double weight = 0;
        double time = 0;
        for(int currentPosition = 0; currentPosition < route.length - 1; ) {
            Integer[] currentCity = groupedItems[currentPosition];
            for (Integer item : currentCity) {
                if (packingPlan[item]) {//taken
                    weight += items[item][ConfigurationProvider.WEIGHT_OF_ITEM];
                }
            }
            time += countTime(route, currentPosition, ++currentPosition, countSpeed(weight));
        }
        individual.setFitnessTime(time);
    }

    private static void createGroupedItemsArray() {
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
