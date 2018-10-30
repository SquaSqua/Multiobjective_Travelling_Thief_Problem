import java.util.ArrayList;

public class GreedyPackingPlan {

    private double coefficient;
    private double maxSpeed;
    private int capacity;
    private int dimension;
    private double[][] distances;
    private int[][] items;
    private ArrayList<double[]> gainOfItems;
    private Integer[][] groupedItems;

    public GreedyPackingPlan(double minSpeed, double maxSpeed, int capacity, int dimension, double[][] distances, int[][] items) {
        this.maxSpeed = maxSpeed;
        this.capacity = capacity;
        this.dimension = dimension;
        this.distances = distances;
        this.items = items;

        coefficient = (maxSpeed - minSpeed) / capacity;
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

    public double countSpeed(double currentWeight) {
        return maxSpeed - (currentWeight * coefficient);
    }

    private int[] setPackingPlan(int[] route) {
        countGain(route);
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
        return packingPlan;
    }

    //for each item gain is counted as a v - R(t - tb), where v - value of one item, R - renting ratio,
    // t - time of carrying this one item with no more items from the point it was placed to,
    //tb - basic time of travel from the chosen point with empty knapsack
    private void countGain(int[] route) {
        for(int i = 0; i < items.length; i++) {
            int[] currentRow = items[i];
            gainOfItems.add(new double[] {i, currentRow[1] /
                    (currentRow[2] * countTime(route, currentRow[3], countSpeed(currentRow[2])))});
        }
        gainOfItems.sort((double[] o1, double[] o2) ->
                o2[1] - o1[1] < 0 ? -1 : o2[1] > 0 ? 1 : 0);
    }

    public int countFitnessWage(int[] packingPlan) {
        int totalWage = 0;
        for(int i = 0; i < packingPlan.length; i++) {
            if(packingPlan[i] == 1) {
                totalWage += items[i][1];
            }
        }
        return totalWage;
    }

    public double countFitnessTime(int[] route, int[] packingPlan) {
        double weight = 0;
        double time = 0;
        for(int currentPosition = 0; currentPosition < route.length - 1; ) {
            Integer[] currentCity = groupedItems[currentPosition];
            for(int j = 0; j < currentCity.length; j++) {
                if(packingPlan[currentCity[j]] == 1) {
                    weight += items[currentCity[j]][2];
                }
            }
            time += countTime(route, currentPosition, ++currentPosition, countSpeed(weight));
        }
        return time;
    }

    public void createGroupedItemsArray() {
        groupedItems = new Integer[dimension][];
        ArrayList<Integer>[] groupedItemsList = new ArrayList[dimension];
        for(int i = 0; i < dimension; i++) {
            groupedItemsList[i] = new ArrayList<>();
        }
        for(int i = 0; i < items.length; i++) {
            groupedItemsList[items[i][3] - 1].add(items[i][0] - 1);
        }
        for(int i = 0; i < dimension; i++) {
            groupedItems[i] = new Integer[groupedItemsList[i].size()];
            for (int j = 0; j < groupedItemsList[i].size(); j++) {
                groupedItems[i][j] = groupedItemsList[i].get(j);
            }
        }
    }
}
