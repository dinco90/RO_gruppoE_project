package ro_gruppoe_project;

/**
 * Deposito
 */
public class Depot {

    private int x;  //coordinata x
    private int y;  //coordinata y
    private int vehicles; //numero dei veicoli
    private int maxCapacity;

    /**
     * Costruttore
     *
     * @param x Coordinata x
     * @param y Coordinata y
     * @param capacity Capacità massima di ogni veicolo
     * @param n Numero dei veicoli
     */
    public Depot(int x, int y, int capacity, int n) {
        this.x = x;
        this.y = y;
        this.vehicles = n;
        this.maxCapacity = capacity;
    }

    /**
     * Set coordinata x
     *
     * @param x Valore di x
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Get coordinata x
     *
     * @return Coordinata x
     */
    public int getX() {
        return x;
    }

    /**
     * Set coordinata y
     *
     * @param y Valore di y
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Get coordinata y
     *
     * @return Coordinata y
     */
    public int getY() {
        return y;
    }

    /**
     * Restituisce il numero dei veicoli
     *
     * @return Numero veicoli
     */
    public int numberOfVehicles() {
        return vehicles;
    }
    
    /**
     * Restituisce la capacità massima del veicolo
     *
     * @return Capacità massima del veicolo
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
}