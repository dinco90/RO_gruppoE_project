package ro_gruppoe_project;

import java.util.ArrayList;
import java.util.Collection;

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
     * @param customer Customer che inizializza la route
     * @param goods Merce che richiede il customer
     */
    public Route(int customer, int goods) {
        this.usedCapacity = goods;    //vuoto all'inizio
        this.costo = 0;     // vuoto all'inizio
        route.add(customer);    //customer
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

    /**
     * Imposta il costo della route
     * @param costo Costo calcolato
     */
    public void setCosto(double costo) {
        this.costo = costo;
    }

    /**
     * Restituisce il costo dell'intera route
     * @return Costo route
     */
    public double getCosto(){
        return this.costo;
    }

    /**
     * Verifica se due customer fanno parte della stessa route
     * @param customer1 Indice primo customer
     * @param customer2 Indice secondo customer
     * @return True o false se sono presenti entrambi
     */
    public boolean visitCustomers(Integer customer1, Integer customer2) {
        return route.contains(customer1) && route.contains(customer2);
    }

    /**
     * Veriica se un customer fa parte della route
     * @param customerToFind Customer da trovare
     * @return True o false a seconda se faccia parte della route
     */
    public boolean findCustomer(Integer customerToFind){
        for(Integer customer : route) {
            if(customerToFind == customer){
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce il primo customer della route
     * @return Primo customer
     */
    public int firtCustomer(){
        return route.get(0);
    }

    /**
     * Restituisce l'ultimo customer della route
     * @return Ultimo customer
     */
    public int lastCustomer(){
        return route.get(route.size()-1);
    }

    /**
     * Restituisce la lista di customer che fanno parte della route
     * @return Route
     */
    public ArrayList<Integer> getRoute(){
        return route;
    }

    /**
     * Unisce una route in coda e modifica lo spazio utilizzato
     * @param routeToMerge Route da unire in coda
     */
    public void merge (Route routeToMerge){
        route.addAll(routeToMerge.getRoute());

        usedCapacity += routeToMerge.getUsed();
    }
}
