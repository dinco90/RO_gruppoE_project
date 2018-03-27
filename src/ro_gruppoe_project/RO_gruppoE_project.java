/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro_gruppoe_project;

import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author Dennis, Claudia
 */
public class RO_gruppoE_project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        selectFile();
    }
    
    static private void selectFile(){
        JFileChooser chooser = new JFileChooser();
        
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
        }
    }

    public double distance(Customer c1, Customer c2){
        double d;
        d = Math.hypot(c1.getX()-c2.getX(), c1.getY()-c2.getY());
        return d;
    }
    
}
