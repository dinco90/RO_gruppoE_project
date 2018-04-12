package ro_gruppoe_project;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Manager {

    private String nameFile;
    private Depot depot;    //deposito
    private Customer[] customers;   //vettore di customers
    private ArrayList<Integer> deliveries = new ArrayList<Integer>();   //lista di indici dei customer linehaul
    private ArrayList<Integer> pickups = new ArrayList<Integer>();  //lista di indici dei customer backhaul
    private double[][] tableDistancesLinehaul; //tabella delle distanze dei Linehaul
    private double[][] tableDistancesBackhaul; //tabella delle distanze dei Backhaul
    private double[][] tableSavingsLinehaul;   //tabella dei savings dei Linehaul
    private double[][] tableSavingsBackhaul;   //tabella dei savings dei Backhaul
    private ArrayList<SavingOccurrence> sortedSavingsLinehaul = new ArrayList<SavingOccurrence>();  //savings linehaul ordinati
    private ArrayList<SavingOccurrence> sortedSavingsBackhaul = new ArrayList<SavingOccurrence>();  //savings backhaul ordinati
    private ArrayList<Integer> backhaul;    //pickups + last linehaul
    private ArrayList<Route> routes = new ArrayList<Route>();   //insieme delle routes

    /**
     * Selezione del file in input
     */
    public void selectFile() {
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
        }
        nameFile = chooser.getSelectedFile().getAbsolutePath();
    }

    /**
     * Legge il file selezionato in precendenza per estrarre i dati del problema
     */
    public void readFile() {

        BufferedReader br = null;
        FileReader fr = null;
        int lineCounter = 0;

        int numeroVeicoli = 0;

        try {
            fr = new FileReader(nameFile);

            br = new BufferedReader(fr);

            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                lineCounter++;
                // switch: in base al numero di riga salva il valore
                switch (lineCounter) {
                    case 1: // numero customer
                        // crea l'array della lunghezza del numero di customer
                        customers = new Customer[Integer.parseInt(sCurrentLine)];
                        System.out.println("numero customer: " + sCurrentLine);
                        break;
                    case 2: // ??? default
                        System.out.println("default: " + sCurrentLine);
                        break;
                    case 3: // numero furgoni
                        numeroVeicoli = Integer.parseInt(sCurrentLine);
                        System.out.println("numero furgoni: " + sCurrentLine);
                        break;
                    case 4: // deposito
                        // estraggo i dati dalla linea
                        String[] partsD = sCurrentLine.split("   ");
                        int xD = Integer.parseInt(partsD[0]);
                        int yD = Integer.parseInt(partsD[1]);
                        // il terzo elemento non viene usato
                        int capacity = Integer.parseInt(partsD[3]);

                        // crea il deposito con i valori
                        depot = new Depot(xD, yD, capacity, numeroVeicoli);
                        break;
                    default: // customers
                        // estraggo i dati dalla linea
                        String[] partsC = sCurrentLine.split("   ");
                        int xC = Integer.parseInt(partsC[0]);
                        int yC = Integer.parseInt(partsC[1]);
                        int delivery = Integer.parseInt(partsC[2]);
                        int pickup = Integer.parseInt(partsC[3]);
                        // l'ultimo elemento della riga non viene usato

                        // aggiunge l'indice del customer nella relativa lista
                        if (delivery != 0) {
                            deliveries.add(lineCounter - 5);
                        }
                        if (pickup != 0) {
                            pickups.add(lineCounter - 5);
                        }

                        // aggiunge il customer all'array
                        customers[lineCounter - 5] = new Customer(xC, yC, delivery, pickup);
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
        System.out.println("numero righe: " + lineCounter);
    }

    /**
     * Calcola la distanza tra due customers
     *
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
     *
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
    public void createTableDistanceLinehaul() {
        // customers.length+1 perché serve anche la distanza con il deposito
        tableDistancesLinehaul = new double[customers.length + 1][customers.length + 1];

        double d;

        // deliveries.size()+1 perché serve anche la distanza con il deposito
        for (int i = 0; i < deliveries.size() + 1; i++) {
            // j=i+1 per creare la matrice simmetrica
            // deliveries.size()+1 perché serve anche la distanza con il deposito
            for (int j = i + 1; j < deliveries.size() + 1; j++) {
                if (i == 0) {
                    //distanza tra deposito e customer
                    d = calculateDistance(depot, customers[j - 1]);
                } else {
                    //distanza tra due customers
                    d = calculateDistance(customers[i - 1], customers[j - 1]);
                }

                tableDistancesLinehaul[i][j] = d;
                tableDistancesLinehaul[j][i] = d;
            }
        }
    }

    /**
     * Crea la tabella delle distanze tra i customer che richiedono un pickup
     */
    public void createTableDistanceBackhaul() {
        // +1 perché serve anche la distanza con il deposito
        tableDistancesBackhaul = new double[backhaul.size() + 1][backhaul.size() + 1];

        double d;

        // backhaul.size()+1 perché serve anche la distanza con il deposito
        for (int i = 0; i < backhaul.size() + 1; i++) {
            // j=i+1 per creare la matrice simmetrica
            // backhaul.size()+1 perché serve anche la distanza con il deposito
            for (int j = i + 1; j < backhaul.size() + 1; j++) {
                if (i == 0) {
                    //distanza tra deposito e customer
                    d = calculateDistance(depot, customers[backhaul.get(j - 1)]);
                } else {
                    //distanza tra due customers
                    d = calculateDistance(customers[backhaul.get(i - 1)], customers[backhaul.get(j - 1)]);
                }

                tableDistancesBackhaul[i][j] = d;
                tableDistancesBackhaul[j][i] = d;
            }
        }
    }

    /**
     * Inizializzazione della lista dei backhaul
     */
    public void initializeBackhaul() {
        //inizializza i backhaul con la lista dei customers che richiedono il pickup
        backhaul = new ArrayList<>(pickups);

        //si aggiungono ai backhaul i customer finali delle route dei linehaul
        for (Route route : routes) {
            backhaul.add(route.getLast());
        }
    }

    /**
     * Calcola il singolo saving della coordinata [i,j]
     *
     * @param i Indice della riga
     * @param j Indice della colonna
     * @param table Tabella delle distanze
     * @return Il saving calcolato in base alla regola
     */
    public double calculateSaving(int i, int j, double[][] table) {
        double s;
        s = table[i][0] + table[0][j] - table[i][j];
        return s;
    }

    /**
     * Crea la tabella dei savings linehaul
     */
    public void createTableSavingsLinehaul() {
        //creazione della tabella dei savings linehaul
        tableSavingsLinehaul = new double[deliveries.size()][deliveries.size()];

        double s;

        //si popola la tabella dei saving linehaul
        for (int i = 1; i < deliveries.size(); i++) {
            for (int j = i + 1; j < deliveries.size(); j++) {
                s = calculateSaving(i, j, tableDistancesLinehaul);
                tableSavingsLinehaul[i][j] = s;
                tableSavingsLinehaul[j][i] = s;
            }
        }
    }

    /**
     * Crea la tabella dei savings backhaul
     */
    public void createTableSavingsBackhaul() {
        //creazione della tabella dei savings backhaul
        tableSavingsBackhaul = new double[backhaul.size()][backhaul.size()];

        double s;

        //si popola la tabella dei saving backhaul
        for (int i = 1; i < backhaul.size(); i++) {
            for (int j = i + 1; j < backhaul.size(); j++) {
                s = calculateSaving(i, j, tableDistancesBackhaul);
                tableSavingsBackhaul[i][j] = s;
                tableSavingsBackhaul[j][i] = s;
            }
        }
    }

    /**
     * Riordina i savings linehaul in ordine decrescente
     */
    public void setSortedSavingsLinehaul() {
        //estrazione delle occorrenze dei savings dalla tabella con le relative righe e colonne di riferimento
        for (int i = 1; i < tableSavingsLinehaul.length; i++) {
            for (int j = i + 1; j < tableSavingsLinehaul.length; j++) {
                sortedSavingsLinehaul.add(new SavingOccurrence(i, j, tableSavingsLinehaul[i][j]));
            }
        }

        //riorndina i savings in ordine decrescente
        Collections.sort(sortedSavingsLinehaul, (so1, so2) -> Double.compare(so1.s, so2.s));
    }

    /**
     * Riordina i savings backhaul in ordine decrescente
     */
    public void setSortedSavingsBackhaul() {
        //estrazione delle occorrenze dei savings dalla tabella con le relative righe e colonne di riferimento
        for (int i = 1; i < tableSavingsBackhaul.length; i++) {
            for (int j = i + 1; j < tableSavingsBackhaul.length; j++) {
                sortedSavingsBackhaul.add(new SavingOccurrence(i, j, tableSavingsBackhaul[i][j]));
            }
        }

        //riorndina i savings in ordine decrescente
        Collections.sort(sortedSavingsBackhaul, (so1, so2) -> Double.compare(so1.s, so2.s));
    }

    /**
     * Restituisce la distanza tra il deposito e un customer
     *
     * @param customer Indice del customer nell'arraylist "customers"
     * @return Distanza
     */
    public double getDistance(Integer customer) {
        boolean l = true;
        double d = 0;

        if (pickups.contains(customer)) {
            l = false;
        }

        if (l) {
            d = tableDistancesLinehaul[0][(deliveries.indexOf(customer)) + 1];
        } else {
            d = tableDistancesBackhaul[0][pickups.indexOf(customer) + 1];
        }

        return d;
    }

    /**
     * Restituisce la distanza tra due customer
     *
     * @param first Indice del primo customer nell'arraylist "customers"
     * @param second Indice del secondo customer nell'arraylist "customers"
     * @return Distanza
     */
    public double getDistance(Integer first, Integer second) {
        boolean l = true;
        double d = 0;

        if (pickups.contains(first) && pickups.contains(second)) {
            l = false;
        }

        if (l) {
            d = tableDistancesLinehaul[deliveries.indexOf(first) + 1][deliveries.indexOf(second) + 1];
        } else {
            d = tableDistancesBackhaul[pickups.indexOf(first) + 1][pickups.indexOf(second) + 1];
        }

        return d;
    }

    /**
     * Inizializza le routes linehaul iniziali
     */
    /*
    public void initializeRoutes() {
        for (Integer delivery : deliveries) {
            routes.add(new Route(delivery, getDistance(delivery)));
        }
    }
     */
    
    /**
     * Esegue l'algoritmo Clarke & Wright in modo sequenziale
     */
    public void algoritmoClarkeWrightSequenziale() {

        // scorre la tabella dei savings
        for (SavingOccurrence occurrence : sortedSavingsLinehaul) {

            if ((twoCustomersInRoute(occurrence.i, occurrence.j)) && 
                    (verifyCapacity(occurrence.i, occurrence.j)) && 
                    (false)) {
                ;
            }
        }

    }

    /**
     * Verifica se due customers si trovano nella route
     * @param i Primo indice del customer
     * @param j Secondo indice del customer
     * @return True o false a seconda del fatto che facciano parte della stessa route
     */
    public boolean twoCustomersInRoute(int i, int j) {
        boolean flag = false;
        int k = 0;
        while (!flag && k < routes.size()) {
            flag = routes.get(k).visitCustomers(i, j);
            k++;
        }
        return flag;
    }
    
    /**
     * Trova la route di cui fa parte il customer
     * @param customerToFind Il customer di cui cercare la route
     * @return L'indice della route di cui fa parte il customer
     */
    public int findRoute(int customerToFind) {
        for (Route route : routes) {
            if(route.findCustomer(customerToFind)){
                return routes.indexOf(route);
            }
        }
        return -1;
    }
    
    /**
     * Verifica se lo spazio è sufficiente per il nuovo customer
     * @param i Primo indice del customer
     * @param j Secondo indice del customer
     * @return True o false a seconda se lo spazio è sufficiente o meno
     */
    public boolean verifyCapacity(int i, int j){
        if(routes.get(findRoute(i)).getUsed() + customers[j].getDemand() + customers[j].getSupply()<= depot.getMaxCapacity() ){
            return true;
        }
        else{
            return false;
        }
    }
}