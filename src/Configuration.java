public class Configuration {

    private static int dimension;
    private static int capacity;
    private static double minSpeed;
    private static double maxSpeed;
    private static double rentingRatio;

    private static double[][] distances;
    private static int[][] items;
    private static Point ideal, nadir;

//    getters
    public static int getCapacity() {
        return capacity;
    }
    public static int getDimension() {
        return dimension;
    }
    public static int[][] getItems() {
        return items;
    }

    static double getMinSpeed() {
        return minSpeed;
    }
    static double getMaxSpeed() {
        return maxSpeed;
    }

    static double[][] getDistances() {
        return distances;
    }

    static Point getIdeal() {
        return ideal;
    }
    static Point getNadir() {
        return nadir;
    }


    //setters
    public static void setDimension(int dimension) { Configuration.dimension = dimension; }
    public static void setCapacity(int capacity) {
        Configuration.capacity = capacity;
    }
    public static void setItems(int[][] items) {
        Configuration.items = items;
    }

    static void setMinSpeed(double minSpeed) {
        Configuration.minSpeed = minSpeed;
    }
    static void setMaxSpeed(double maxSpeed) {
        Configuration.maxSpeed = maxSpeed;
    }
    static void setRentingRatio(double rentingRatio) {
        Configuration.rentingRatio = rentingRatio;
    }
    static void setDistances(double[][] distances) {
        Configuration.distances = distances;
    }
    static void setIdeal(Point ideal) {
        Configuration.ideal = ideal;
    }
    static void setNadir(Point nadir) {
        Configuration.nadir = nadir;
    }

}
