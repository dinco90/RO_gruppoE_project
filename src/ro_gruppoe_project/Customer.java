package ro_gruppoe_project;

/**
 * Clienti
 */
public class Customer implements Comparable<Customer>{

    private int x;  //coordinata x
    private int y;  //coordinata y
    private int demand; //domanda - delivery
    private int supply; //offerta - pickup

    /**
     * Costruttore
     *
     * @param x Coordinata x
     * @param y Coordinata y
     * @param demand Domanda - delivery
     * @param supply Offerta - pickup
     */
    public Customer(int x, int y, int demand, int supply) {
        this.x = x;
        this.y = y;
        this.demand = demand;
        this.supply = supply;
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
     * Set Domanda
     *
     * @param demand Domanda
     */
    public void setDemand(int demand) {
        this.demand = demand;
    }

    /**
     * Get domanda
     *
     * @return Domanda
     */
    public int getDemand() {
        return demand;
    }

    /**
     * Set offerta
     *
     * @param supply Offerta
     */
    public void setSupply(int supply) {
        this.supply = supply;
    }

    /**
     * Get offerta
     *
     * @return Offerta
     */
    public int getSupply() {
        return supply;
    }
    
    public int compareTo(Customer customer){
        //descending order
        int ris = (customer.getDemand() + customer.getSupply()) - (this.getDemand() + this.getSupply());
        
        if (ris > 0) {
            return 1;
        } else if (ris < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
