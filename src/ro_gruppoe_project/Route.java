package ro_gruppoe_project;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Route
 */
public class Route {

    //private int maxCapacity;    //Massima capacità del veicolo // IN DEPOT
    private int deliveryLoad;   //spazio utilizzato nel veicolo per i delivery
    private int pickupLoad;     //spazio utilizzato nel veicolo per i pickup


    private ArrayList<Integer> route = new ArrayList<Integer>();    // route: indice dei customer
    private double cost;    // costo del tragitto

    private boolean union=false;    // booleano che indica se una route linehaul è gia stata utita ad una backhaul
    
    boolean base = false;   //indica che se la route è di base (fissata)

    /**
     * Costruttore
     */
    public Route(){
        this.cost = 0;
    }
    
    /**
     * Costruttore
     * @param customer Customer che inizializza la route
     * @param deliveryLoad Delivery
     * @param pickupLoad Pickup
     */
    public Route(int customer, int deliveryLoad, int pickupLoad) {
        this.deliveryLoad = deliveryLoad;
        this.pickupLoad=pickupLoad;
        this.cost = 0;     // vuoto all'inizio
        route.add(customer);    //customer
    }

    /**
     * Get dello spazio utilizzato attualmente
     *
     * @return Spazio utilizzato
     */
    public int getDelivery() {
        return deliveryLoad;
    }
    
    /**
     * Aggiunge carico allo spazio utilizzato
     * @param addedDeliveryLoad Il carico aggiunto
     */
    public void addDelivery(int addedDeliveryLoad){
        deliveryLoad += addedDeliveryLoad;
    }

    /**
     * Imposta il costo della route
     * @param cost Costo calcolato
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Restituisce il costo dell'intera route
     * @return Costo route
     */
    public double getCost(){
        return this.cost;
    }

    /**
     * Veriica se un customer fa parte della route
     * @param customerToFind Customer da trovare
     * @return True o false a seconda se faccia parte della route
     */
    public boolean findCustomer(Integer customerToFind){
        for(Integer customer : route) {
            if(customerToFind.equals(customer)){
                return true;
            }
        }
        return false;
    }

    /**
     * Restituisce il primo customer della route
     * @return Primo customer
     */
    public int firstCustomer(){
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

        deliveryLoad += routeToMerge.getDelivery();
        pickupLoad += routeToMerge.getPickup();
        base= this.base || routeToMerge.base;
    }

    /**
     * Effettua il reverse di una route
     */
    public void reverse(){
        Collections.reverse(route);
    }

    /**
     * Viene richiamata tale funzione quando sono stati aggiunti i nodi backhaul
     */
    public void setUnion(){
        union=true;
    }

    /**
     * Verifica se si tratta di una route con soli linehaul
     * @return Flag che identifica l'unione tra linehaul e backhaul
     */
    public boolean isUnion(){
        return union;
    }

    /**
     * Pickup dei prodotti per route
     * @return Pickup
     */
    public int getPickup(){
        return pickupLoad;
    }
    
    /**
     * Aggiunge carico allo spazio utilizzato
     * @param addedPickupLoad Il carico aggiunto
     */
    public void addPickup(int addedPickupLoad){
        pickupLoad += addedPickupLoad;
    }
}
