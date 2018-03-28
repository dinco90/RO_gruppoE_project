package ro_gruppoe_project;

public class Depot {
    private int x;  //coordinata x
    private int y;  //coordinata y
    private Vehicle[] vehicles; //vettore dei veicoli

    /**
     * Costruttore
     * @param x Coordinata x
     * @param y Coordinata y
     * @param capacity Capacità massima di ogni veicolo
     * @param n Numero dei veicoli
     */
    public Depot(int x, int y, int capacity, int n){
        this.x=x;
        this.y=y;
        vehicles=new Vehicle[n];
        for (int i=0;  i<n; i++){
            vehicles[i]=new Vehicle(capacity);
        }
    }

    /**
     * Set coordinata x
     * @param x Valore di x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get coordinata x
     * @return Coordinata x
     */
    public int getX() {
        return x;
    }

    /**
     * Set coordinata y
     * @param y Valore di y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get coordinata y
     * @return Coordinata y
     */
    public int getY() {
        return y;
    }

    /**
     * Restituisce il vettore dei veicoli
     * @return Vettore dei veicoli
     */
    public Vehicle[] getVehicles() {
        return vehicles;
    }

    /**
     * Reistituisce il veicolo all'i-esima posizione
     * @param i Posizione
     * @return Veicolo
     */
    public Vehicle getVehicleAt(int i){
        return vehicles[i];
    }

    /**
     * Restituisce il numero dei veicoli
     * @return Numero veicoli
     */
    public int numberOfVehicles(){
        return vehicles.length;
    }
}
