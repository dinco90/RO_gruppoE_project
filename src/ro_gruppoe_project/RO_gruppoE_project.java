/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro_gruppoe_project;

/**
 *
 * @author Dennis, Claudia
 */
public class RO_gruppoE_project {

    public static void main(String[] args) {

        Manager manager = new Manager();

        // selezione e lettura del file
        manager.selectFile();
        manager.readFile();

        // Linehaul: crea la tabella delle distanze, crea la tabella dei savings, ordina i savings
        manager.createTableDistance();
        manager.createTableSavings();
        manager.setSortedSavingsLinehaul();
        manager.setSortedSavingsBackhaul();

        //
        //calcolo delle routes Sequaziale
        manager.initializeRoutes();
        manager.algoritmoClarkeWrightSequenziale();

        //
        // Backhaul: crea la tabella delle distanze, crea la tabella dei savings, ordina i savings



        //
        //calcolo dei costi
        manager.calculateCost();

        //
        //salvataggio risultati su file
        manager.writeFile();
    }
}
