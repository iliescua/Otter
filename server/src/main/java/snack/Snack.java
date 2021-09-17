package snack;

public class Snack {
    private final String name;
    private final String id;
    private final int stock;
    private final int capacity;
    private double cost;
    private int orderAmnt;

    public Snack(String name, int stock, int capacity, String id, double cost, int orderAmnt) {
        this.name = name;
        this.id = id;
        this.stock = stock;
        this.capacity = capacity;
        this.cost = cost;
        this.orderAmnt = orderAmnt;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getOrderAmnt() {
        return orderAmnt;
    }

    public void setOrderAmnt(int orderAmnt) {
        this.orderAmnt = orderAmnt;
    }

    public int getStock() {
        return stock;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
