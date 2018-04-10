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

        Manager manager=new Manager();

        //// si crea un manager (new Manager) e tutti i metodi (tutto l'algoritmo) viene svolto da tale classe
        //// i metodi 'createTableDistanceLinehaul()' e 'createTableSavings()' sono di Manager, quindi:
        //// manager.createTableDistanceLinehaul();
        //// manager.createTableSavings(tableSavingsLinehaul, tableDistancesLinehaul, deliveries);
        
        //roProjectE.createTableDistanceLinehaul();
        //bisogna risolvere il problema del static main
        //roProjectE.createTableSavings(tableSavingsLinehaul, tableDistancesLinehaul, deliveries);


        manager.selectFile();
        manager.readFile();

        manager.createTableDistanceLinehaul();
        manager.createTableSavingsLinehaul();
        manager.setSortedSavingsLinehaul();

        //
        //calcolo delle routes linehaul
        //

        manager.inizializationBackhaul();
        manager.createTableDistanceBackhaul();
        manager.createTableSavingsBackhaul();
        manager.setSortedSavingsBackhaul();

        //
        //calcolo delle routes backhaul
        //

        //
        //salvataggio risultati su file
        //

    }






}
