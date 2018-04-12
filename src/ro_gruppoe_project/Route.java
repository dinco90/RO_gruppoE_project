package ro_gruppoe_project;

import java.util.ArrayList;

/**
 * Route
 */
public class Route {

    private int maxCapacity;    //Massima capacità del veicolo
    private int usedCapacity;   //spazio utilizzato nel veicolo
    
    private ArrayList<Integer> route = new ArrayList<Integer>();   // route: indice dei customer
    private double costo; // costo del tragitto

    /**
     * Costruttore
     *
     */
    public Route() {
        this.usedCapacity = 0;    //vuoto all'inizio
        this.costo = 0;     // vuoto all'inizio
    }

    

    /**
     * Set dello spazio utilizzato del veicolo
     *
     * @param used Spazio da occupare
     */
    public void setUsed(int used) {
        this.usedCapacity = used;
    }

    /**
     * Get dello spazio utilizzato attualmente
     *
     * @return Spazio utilizzato
     */
    public int getUsed() {
        return usedCapacity;
    }

    /**
     * Aggiunge merce al veicolo
     *
     * @param p Quantità di merce aggiunta
     */
    public void addProducts(int p) {
        this.usedCapacity = this.usedCapacity + p;
    }
    
    public void setCosto(double costo) {
        this.costo = costo;
    }
    
    public double getCosto(){
        return this.costo;
    }

    public Integer getLast() {
        return route.get(route.size() - 2);
    }

    public boolean visitCustomers(Integer customer1, Integer customer2) {
        return route.contains(customer1) && route.contains(customer2);
    }
    
    public boolean findCustomer(Integer customerToFind){
        for(Integer customer : route) {
            if(customerToFind == customer){
                return true;
            }
        }
        return false;
    }
}
