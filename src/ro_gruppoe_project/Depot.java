package ro_gruppoe_project;

public class Depot {
    private int x;
    private int y;
    private Vehicle[] vehicles;

    public Depot(int x, int y, int capacity, int n){
        this.x=x;
        this.y=y;
        vehicles=new Vehicle[n];
        for (int i=0;  i<n; i++){
            vehicles[i]=new Vehicle(capacity);
        }
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

    public Vehicle[] getVehicles() {
        return vehicles;
    }

    public Vehicle getVehicleAt(int i){
        return vehicles[i];
    }

    public int numberOfVehicles(){
        return vehicles.length;
    }
}
