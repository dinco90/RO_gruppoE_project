/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ro_gruppoe_project;

import java.io.File;
import javax.swing.JFileChooser;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dennis, Claudia
 */
public class RO_gruppoE_project {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        RO_gruppoE_project.readFile(RO_gruppoE_project.selectFile());
    }

    static private String selectFile() {
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
        }
        return chooser.getSelectedFile().getAbsolutePath();
    }

    static private void readFile(String fileString) {

        BufferedReader br = null;
        FileReader fr = null;
        int lineCounter = 0;

        try {
            fr = new FileReader(fileString);
            
            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                lineCounter++;
                // switch
                switch(lineCounter){
                    case 1:
                        System.out.println(sCurrentLine);
                        break;
                    case 2:
                        System.out.println(sCurrentLine);
                        break;
                }
                //System.out.println(sCurrentLine);
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        System.out.println(lineCounter);
    }

    public double calculateDistance(Customer c1, Customer c2) {
        double d;
        d = Math.hypot(c1.getX() - c2.getX(), c1.getY() - c2.getY());
        return d;
    }

}
