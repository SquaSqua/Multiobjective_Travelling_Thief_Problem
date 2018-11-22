public class Configuration {

    private int dimension;
    private int capacity;
    private double minSpeed;
    private double maxSpeed;
    private double rentingRatio;

    private double[][] distances;
    private int[][] items;
    private Point ideal, nadir;

//    getters
    public int getCapacity() {
        return capacity;
    }
    public int getDimension() {
        return dimension;
    }
    double getMinSpeed() {
        return minSpeed;
    }
    double getMaxSpeed() {
        return maxSpeed;
    }
    double[][] getDistances() {
        return distances;
    }
    public int[][] getItems() {
        return items;
    }
    Point getIdeal() {
        return ideal;
    }
    Point getNadir() {
        return nadir;
    }


    //setters
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    void setMinSpeed(double minSpeed) {
        this.minSpeed = minSpeed;
    }
    void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }
    void setRentingRatio(double rentingRatio) {
        this.rentingRatio = rentingRatio;
    }
    void setDistances(double[][] distances) {
        this.distances = distances;
    }
    public void setItems(int[][] items) {
        this.items = items;
    }
    void setIdeal(Point ideal) {
        this.ideal = ideal;
    }
    void setNadir(Point nadir) {
        this.nadir = nadir;
    }

}
