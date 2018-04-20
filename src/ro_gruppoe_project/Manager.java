package ro_gruppoe_project;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manager {

    private String nameFile;
    private String pathFile;
    private Depot depot;    //deposito
    private Customer[] customers;   //vettore di customers
    private ArrayList<Integer> deliveries = new ArrayList<Integer>();   //lista di indici dei customer linehaul
    private ArrayList<Integer> pickups = new ArrayList<Integer>();  //lista di indici dei customer backhaul
    private double[][] tableDistances; //tabella delle distanze
    private double[][] tableSavings;   //tabella dei savings
    private ArrayList<SavingOccurrence> sortedSavingsLinehaul = new ArrayList<SavingOccurrence>();  //savings linehaul ordinati
    private ArrayList<SavingOccurrence> sortedSavingsBackhaul = new ArrayList<SavingOccurrence>();  //savings backhaul ordinati
    private ArrayList<Integer> backhaul;    //pickups + last e first linehaul
    private ArrayList<Route> routesLinehaul = new ArrayList<Route>();   //insieme delle routes Linehaul
    private ArrayList<Route> routesBackhaul = new ArrayList<Route>();   //insieme delle routes Backhaul



    /**
     * Selezione del file in input
     */
    public void selectFile() {
        JFileChooser chooser = new JFileChooser();

        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            nameFile = chooser.getSelectedFile().getName();
            System.out.println("You chose to open this file: " + nameFile);
        }
        pathFile = chooser.getSelectedFile().getAbsolutePath();
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
            fr = new FileReader(pathFile);

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
     * Scrive il file dei risultati nella cartella "output"
     */
    public void writeFile() {
        int deliveryLoad=0;
        int pickupLoad=0;
        String routeString="";        
        
        try {
            new File("output").mkdirs();
            FileWriter writer = new FileWriter("output/Solution " + nameFile);
            // stampa titolo
            writer.write("Text File with Solution Of Problem: " + nameFile + "\r\n\r\n");
            // stampa dettagli problema
            writer.write("\r\nPROBLEM DETAILS:\r\n");
            writer.write("Customers: " + customers.length + "\r\n");
            writer.write("Max Load: " + depot.getMaxCapacity() + "\r\n");
            writer.write("Max Cost: " + "???" + "\r\n");
            // stampa dettagli soluzione
            writer.write("\r\nSOLUTION DETAILS:\r\n");
            writer.write("Total Cost: " + "???" + "\r\n");  // cosa è il total cost? dalla soluzione: somma di tutti i costi delle singole route != total cost
            writer.write("Routes Of the Solution: " + routesLinehaul.size() + "\r\n\r\n");

            // stampa di tutte le route
            for (Route route : routesLinehaul) {
                writer.write("ROUTE " + routesLinehaul.indexOf(route) + ":\r\n");
                writer.write("Cost: " + route.getCost() + "\r\n");
                deliveryLoad=0;
                pickupLoad=0;
                routeString="0 - ";
                // per ogni vertice della route
                for (Integer vertex : route.getRoute()){
                    // calcola la somma di delivery di tutta la route
                    if (deliveries.contains(vertex)){
                        deliveryLoad += customers[vertex].getDemand();
                    }
                    //// BACKHAUL NON ANCORA IMPLEMENTATO
                    // calcola la somma di pick-up di tutta la route
                    if (pickups.contains(vertex)){
                        pickupLoad += customers[vertex].getSupply();
                    }
                    //// BACKHAUL NON ANCORA IMPLEMENTATO
                    // salva i vertici in una stringa da stampare alla fine
                    routeString += Integer.toString(vertex+1) + " - ";
                }
                routeString += "0";

                writer.write("Delivery Load: " + deliveryLoad + "\r\n");
                writer.write("Pick-Up Load: " + pickupLoad + "\r\n");
                writer.write("Customers in Route: " + route.getRoute().size() + "\r\n");
                writer.write("Vertex Sequence: " + "\r\n" + routeString);
                
                writer.write("\r\n\r\n");
                // metodo in Route che restituisce una stringa con tutti i customer
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * Crea la tabella delle distanze tra i customer
     */
    public void createTableDistance() {
        // customers.length+1 perché serve anche la distanza con il deposito
        tableDistances = new double[customers.length + 1][customers.length + 1];

        double d;

        // customer.length+1 perché serve anche la distanza con il deposito
        for (int i = -1; i < customers.length; i++) {
            // j=i+1 per creare la matrice simmetrica
            // customer.length+1 perché serve anche la distanza con il deposito
            for (int j = i + 1; j < customers.length; j++) {
                if (i == -1) {
                    //distanza tra deposito e customer
                    d = calculateDistance(depot, customers[j]);
                } else {
                    //distanza tra due customers
                    d = calculateDistance(customers[i], customers[j]);
                }

                tableDistances[i+1][j+1] = d;
                tableDistances[j+1][i+1] = d;
            }
        }
    }

    /**
     * Calcola il singolo saving della coordinata [i,j]
     *
     * @param i Indice della riga
     * @param j Indice della colonna
     * @return Il saving calcolato in base alla regola
     */
    public double calculateSaving(int i, int j) {
        double s;
        s = tableDistances[i][0] + tableDistances[0][j] - tableDistances[i][j];
        return s;
    }

    /**
     * Crea la tabella dei savings
     */
    public void createTableSavings() {
        //creazione della tabella dei savings
        tableSavings = new double[customers.length][customers.length];

        double s;

        //si popola la tabella dei saving
        for (int i = 0; i < customers.length; i++) {
            for (int j = i + 1; j < customers.length; j++) {
                s = calculateSaving(i + 1, j + 1);
                tableSavings[i][j] = s;
                tableSavings[j][i] = s;
            }
        }
    }

    /**
     * Riordina i savings linehaul in ordine decrescente
     */
    public void setSortedSavingsLinehaul() {
        //estrazione delle occorrenze dei savings dalla tabella con le relative righe e colonne di riferimento
        for (int i = 0; i < deliveries.size(); i++) {
            for (int j = i + 1; j < deliveries.size(); j++) {
                sortedSavingsLinehaul.add(new SavingOccurrence(deliveries.get(i), deliveries.get(j), tableSavings[deliveries.get(i)][deliveries.get(j)]));
            }
        }

        //riorndina i savings in ordine decrescente
        Collections.sort(sortedSavingsLinehaul, (so1, so2) -> Double.compare(so1.s, so2.s));

        Collections.reverse(sortedSavingsLinehaul);
    }

    /**
     * Riordina i savings backhaul in ordine decrescente
     */
    public void setSortedSavingsBackhaul() {
        //estrazione delle occorrenze dei savings dalla tabella con le relative righe e colonne di riferimento
        for (int i = 0; i < pickups.size(); i++) {
            for (int j = i + 1; j < pickups.size(); j++) {
                sortedSavingsBackhaul.add(new SavingOccurrence(pickups.get(i), pickups.get(j), tableSavings[pickups.get(i)][pickups.get(j)]));
            }
        }

        //riorndina i savings in ordine decrescente
        Collections.sort(sortedSavingsBackhaul, (so1, so2) -> Double.compare(so1.s, so2.s));

        Collections.reverse(sortedSavingsBackhaul);
    }

    /**
     * Inizializza le routes linehaul iniziali
     */
    public void initializeRoutes() {
        for (Integer delivery : deliveries) {
            routesLinehaul.add(new Route(delivery, customers[delivery].getDemand() + customers[delivery].getSupply()));
        }
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo sequenziale
     */
    public void algoritmoClarkeWrightSequenziale() {
        //LINEHAUL

        // scorre la tabella dei savings
        for (SavingOccurrence occurrence : sortedSavingsLinehaul) {
            int routeI = findRoute(occurrence.i);
            int routeJ = findRoute(occurrence.j);
            boolean iFirst= routesLinehaul.get(routeI).firstCustomer() == occurrence.i ? true : false;
            boolean iLast=routesLinehaul.get(routeI).lastCustomer() == occurrence.i ? true : false;
            boolean jFirst=routesLinehaul.get(routeJ).firstCustomer() == occurrence.j ? true : false;
            boolean jLast=routesLinehaul.get(routeJ).lastCustomer() == occurrence.j ? true : false;



            // per fare il merge tra due route devono essere rispettate tre condizioni
            // condizione 1: le route di i e j devono essere diverse
            if ((routeI != routeJ)
                    && // condizione 2:  la somma dello spazio occupato dalle due route deve essere <= maxcapacity
                    (routesLinehaul.get(routeI).getUsed() + routesLinehaul.get(routeJ).getUsed() <= depot.getMaxCapacity())
                    && // condizione 3: i e j sono first o last
                    ((iFirst || iLast) && (jFirst || jLast))){
                //si possono unire le due route
                if (iLast && jFirst) {
                    //i è last, j è first

                    //unisci j ad i ed elimina  poi j
                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                    routesLinehaul.remove(routeJ);
                } else {
                    if (jLast && iFirst){
                        //j è last, i è first

                        //unisci i ad j ed elimina  poi i
                        routesLinehaul.get(routeJ).merge(routesLinehaul.get(routeI));
                        routesLinehaul.remove(routeI);
                    } else {
                        if ((iLast && jLast) || (iFirst && jFirst)){
                            // si effettua il reverse di una delle due route
                            routesLinehaul.get(routeJ).reverse();

                            //unisci j invertito ad i ed elimina  poi j
                            routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                            routesLinehaul.remove(routeJ);
                        }
                    }
                }

            }
        }

        //BACKHAUL


    }

    
    /**
     * Trova la route di cui fa parte il customer
     *
     * @param customerToFind Il customer di cui si vuole cercare la route
     * @return L'indice della route di cui fa parte il customer
     */
    public int findRoute(int customerToFind) {
        for (Route route : routesLinehaul) {
            if (route.findCustomer(customerToFind)) {
                return routesLinehaul.indexOf(route);
            }
        }
        return -1;
    }

    /**
     * Calcola il costo totale di ogni route
     */
    public void calculateCost() {
        double cost = 0;

        for (Route route : routesLinehaul) {
            ArrayList<Integer> listCustomer = route.getRoute();

            if (listCustomer.size() > 1) {
                //route composta da più customer
                for (int i = 0; i < listCustomer.size() - 1; i++) {
                    //somma tutti i costi tra i customer
                    cost += tableDistances[listCustomer.get(i) + 1][listCustomer.get(i + 1)];
                }
                //somma i costi tra i first e i last con il depot
                cost += tableDistances[0][listCustomer.get(0) + 1] + tableDistances[0][listCustomer.get(listCustomer.size() - 1) + 1];
            } else {
                //route composta da un solo customer
                cost += tableDistances[0][listCustomer.get(0) + 1] * 2;
            }

            route.setCost(cost);
            cost = 0;
        }
    }

}
