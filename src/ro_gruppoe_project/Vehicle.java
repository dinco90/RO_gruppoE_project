package ro_gruppoe_project;

/**
 * Veicolo
 */
public class Vehicle {
    private int maxCapacity;    //Massima capacità del veicolo
    private int used;   //spazio utilizzato nel veicolo

    /**
     * Costruttore
     * @param maxCapacity Massima capacità del veicolo
     */
    public Vehicle(int maxCapacity){
        this.maxCapacity=maxCapacity;
        this.used=0;    //vuoto all'inizio
    }

    /**
     * Restituisce la capacità massima
     * @return Capacità massima
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Set dello spazio utilizzato del veicolo
     * @param used Spazio da occupare
     */
    public void setUsed(int used) {
        this.used = used;
    }

    /**
     * Get dello spazio utilizzato attualmente
     * @return Spazio utilizzato
     */
    public int getUsed() {
        return used;
    }

    /**
     * Aggiunge merce al veicolo
     * @param p Quantità di merce aggiunta
     */
    public void addProducts(int p){
        this.used=this.used+p;
    }
}
