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
