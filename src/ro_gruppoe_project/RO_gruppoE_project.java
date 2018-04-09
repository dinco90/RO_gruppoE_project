/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro_gruppoe_project;

import javax.swing.JFileChooser;

/**
 *
 * @author Dennis, Claudia
 */
public class RO_gruppoE_project {

    public static void main(String[] args) {

        RO_gruppoE_project roProjectE = new RO_gruppoE_project();
        Manager manager=new Manager(roProjectE.selectFile());

        //// si crea un manager (new Manager) e tutti i metodi (tutto l'algoritmo) viene svolto da tale classe
        //// i metodi 'createTableDistanceLinehaul()' e 'createTableSavings()' sono di Manager, quindi:
        //// manager.createTableDistanceLinehaul();
        //// manager.createTableSavings(tableSavingsLinehaul, tableDistancesLinehaul, deliveries);
        
        //roProjectE.createTableDistanceLinehaul();
        //bisogna risolvere il problema del static main
        //roProjectE.createTableSavings(tableSavingsLinehaul, tableDistancesLinehaul, deliveries);

    }

    /**
     * Selezione del file in input
     * @return Il path del file
     */
    private String selectFile() {
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
        }
        return chooser.getSelectedFile().getAbsolutePath();
    }




}
