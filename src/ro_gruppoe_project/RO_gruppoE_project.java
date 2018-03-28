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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dennis, Claudia
 */
public class RO_gruppoE_project {

    // class members
    Depot depot;    //deposito
    Customer[] customers;   //vettore di customers
    ArrayList<Integer> deliveries = new ArrayList<Integer>();   //lista di indici dei customer linehaul
    ArrayList<Integer> pickups = new ArrayList<Integer>();  //lista di indici dei customer backhaul
    double [][] tableDistancesLinehaul; //tabella delle distanze dei Linehaul
    double [][] tableSavings;   //tabella dei savings

    // main
    public static void main(String[] args) {

        RO_gruppoE_project roProjectE = new RO_gruppoE_project();
        roProjectE.readFile(roProjectE.selectFile());

        roProjectE.createTableDistanceLinehaul();
        roProjectE.createTableSavings();
    }

    // methods

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

    /**
     * Legge il file in input per estrarre i dati del problema
     * @param fileString Path del file scelto dall'utente
     */
    private void readFile(String fileString) {

        BufferedReader br = null;
        FileReader fr = null;
        int lineCounter = 0;

        int numeroVeicoli=0;

        try {
            fr = new FileReader(fileString);

            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                lineCounter++;
                // switch
                switch (lineCounter) {
                    case 1: // numero customer
                        customers = new Customer[Integer.parseInt(sCurrentLine)];
                        System.out.println(sCurrentLine);
                        break;
                    case 2: // ???
                        System.out.println(sCurrentLine);
                        break;
                    case 3: // numero furgoni
                        numeroVeicoli = Integer.parseInt(sCurrentLine);
                        System.out.println(sCurrentLine);
                        break;
                    case 4: // deposito
                        // estraggo i dati dalla linea
                        String[] partsD = sCurrentLine.split("   ");
                        int xD = Integer.parseInt(partsD[0]);
                        int yD = Integer.parseInt(partsD[1]);
                        int capacity = Integer.parseInt(partsD[3]);

                        // crea il deposito con i valori
                        depot = new Depot(xD, yD, capacity, numeroVeicoli);
                        break;
                    default: // customers
                        // estraggo i dati dalla linea
                        String[] partsC = sCurrentLine.split("   ");
                        int xC = Integer.parseInt(partsC[0]);
                        int yC = Integer.parseInt(partsC[1]);
                        int delivery  = Integer.parseInt(partsC[2]);
                        int pickup = Integer.parseInt(partsC[3]);
                        // l'ultimo elemento della riga non viene usato
                        
                        // aggiunge l'indice del customer nella relativa lista
                        if (delivery != 0){
                            deliveries.add(lineCounter-5);
                        }
                        if (pickup != 0){
                            pickups.add(lineCounter-5);
                        }
                        
                        // aggiunge il customer all'array
                        customers[lineCounter-5] = new Customer(xC, yC, delivery, pickup);
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

    /**
     * Calcola la distanza tra due customers
     * @param c1 Primo customer
     * @param c2 Secondo customer
     * @return La distanza tra i customers
     */
    public double calculateDistance(Customer c1, Customer c2) {
        double d;
        d = Math.hypot(c1.getX() - c2.getX(), c1.getY() - c2.getY());
        return d;
    }

    /**
     * Calcola la distanza tra il deposito e il customer
     * @param c1 Deposito
     * @param c2 Secondo customer
     * @return La distanza tra deposito e customer
     */
    public double calculateDistance(Depot c1, Customer c2) {
        double d;
        d = Math.hypot(c1.getX() - c2.getX(), c1.getY() - c2.getY());
        return d;
    }

    /**
     * Crea la tabella delle distanze tra i customer che richiedono un delivery
     */
    public void createTableDistanceLinehaul(){
        tableDistancesLinehaul=new double[customers.length+1][customers.length+1];

        double d;

        for (int i=0; i<deliveries.size()+1; i++){
            for (int j=i+1; j<deliveries.size()+1; j++){
                if (i==0){
                    d=calculateDistance(depot, customers[j-1]);
                }else{
                    d=calculateDistance(customers[i-1], customers[j-1]);
                }

                tableDistancesLinehaul[i][j]=d;
                tableDistancesLinehaul[j][i]=d;
            }
        }
    }

    /**
     * Calcola il singolo saving della coordinata [i,j]
     * @param i Indice della riga
     * @param j Indice della colonna
     * @param table Tabella delle distanze
     * @return Il saving calcolato in base alla regola
     */
    public double calculateSaving(int i, int j, double [][] table) {
        double s;
        s=table[i][0] + table[0][j] - table[i][j];
        return s;
    }

    /**
     * Crea la tabella dei savings
     */
    public void createTableSavings(){
        tableSavings=new double[customers.length][customers.length];

        double s;

        for (int i=1; i<customers.length; i++){
            for (int j=i+1; j<customers.length; j++){
                s=calculateSaving(i, j, tableDistancesLinehaul);
                tableSavings[i][j]=s;
                tableSavings[j][i]=s;
            }
        }
    }

}
