package ro_gruppoe_project;

import java.util.ArrayList;

/**
 *
 */
public class Route {
    private ArrayList<Integer> routes=new ArrayList<Integer>();   // route
    private double costo;
    
    Route(Integer delivery, double costo){
        routes.add(0);
        routes.add(delivery);
        routes.add(0);
        
        this.costo = costo*2;
    }

    public void setCosto(double costo){
        this.costo=costo;
    }

    public Integer getLast(){
        return routes.get(routes.size()-2);
    }
}
