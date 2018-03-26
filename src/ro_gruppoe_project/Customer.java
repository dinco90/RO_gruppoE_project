package ro_gruppoe_project;

public class Customer {
    private int x;
    private int y;
    private int demand;
    private int supply;

    public Customer(int x, int y, int demand, int supply){
        this.x=x;
        this.y=y;
        this.demand=demand;
        this.supply=supply;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return y;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getDemand() {
        return demand;
    }

    public void setSupply(int supply) {
        this.supply = supply;
    }

    public int getSupply() {
        return supply;
    }
}


