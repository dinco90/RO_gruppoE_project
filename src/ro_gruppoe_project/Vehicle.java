package ro_gruppoe_project;

public class Vehicle {
    private int maxCapacity;
    private int used;

    public Vehicle(int maxCapacity){
        this.maxCapacity=maxCapacity;
        this.used=0;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public int getUsed() {
        return used;
    }

    public void addProducts(int p){
        this.used=this.used+p;
    }
}
