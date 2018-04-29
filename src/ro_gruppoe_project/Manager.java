package ro_gruppoe_project;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Manager {

    private String nameFile;
    private String pathFile;
    private Depot depot;    // deposito
    private Customer[] customers;   // vettore di customers
    private ArrayList<Integer> deliveries = new ArrayList<Integer>();   // lista di indici dei customer linehaul
    private ArrayList<Integer> pickups = new ArrayList<Integer>();  // lista di indici dei customer backhaul
    private double[][] tableDistances; // tabella delle distanze
    private double[][] tableSavings;   // tabella dei savings
    private ArrayList<SavingOccurrence> sortedSavingsLinehaul = new ArrayList<SavingOccurrence>();  // savings linehaul ordinati
    private ArrayList<SavingOccurrence> sortedSavingsBackhaul = new ArrayList<SavingOccurrence>();  // savings backhaul ordinati
    private ArrayList<SavingOccurrence> sortedSavingsUnion = new ArrayList<SavingOccurrence>();  // savings ordinati per l'unione di linehaul e backhaul
    private ArrayList<Route> routesLinehaul = new ArrayList<Route>();   // insieme delle routes Linehaul
    private ArrayList<Route> routesBackhaul = new ArrayList<Route>();   // insieme delle routes Backhaul
    private ArrayList<Route> routes = new ArrayList<Route>();    // copia delle routes

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
    public void writeFile(String algorithm) {
        int deliveryLoad = 0;
        int pickupLoad = 0;
        double totalCost = 0;
        String routeString = "";

        for (Route route : routesLinehaul) {
            totalCost += route.getCost();
        }

        try {
            new File("output").mkdirs();
            FileWriter writer = new FileWriter("output/Solution " + nameFile + " " + algorithm);
            // stampa titolo
            writer.write("Text File with Solution Of Problem: " + nameFile + "\r\n");
            writer.write("Algorithm: " + algorithm + "\r\n\r\n");
            // stampa dettagli problema
            writer.write("\r\nPROBLEM DETAILS:\r\n");
            writer.write("Customers: " + customers.length + "\r\n");
            writer.write("Max Load: " + depot.getMaxCapacity() + "\r\n");
            writer.write("Max Cost: " + "99999999999999" + "\r\n");
            // stampa dettagli soluzione
            writer.write("\r\nSOLUTION DETAILS:\r\n");
            writer.write("Total Cost: " + totalCost + "\r\n");  // cosa è il total cost? dalla soluzione: somma di tutti i costi delle singole route != total cost
            writer.write("Routes Of the Solution: " + routesLinehaul.size() + "\r\n\r\n");

            // stampa di tutte le route
            for (Route route : routesLinehaul) {
                writer.write("ROUTE " + routesLinehaul.indexOf(route) + ":\r\n");
                writer.write("Cost: " + route.getCost() + "\r\n");
                deliveryLoad = 0;
                pickupLoad = 0;
                routeString = "0 - ";
                // per ogni vertice della route
                for (Integer vertex : route.getRoute()) {
                    // calcola la somma di delivery di tutta la route
                    if (deliveries.contains(vertex)) {
                        deliveryLoad += customers[vertex].getDemand();
                    }
                    //// BACKHAUL NON ANCORA IMPLEMENTATO
                    // calcola la somma di pick-up di tutta la route
                    if (pickups.contains(vertex)) {
                        pickupLoad += customers[vertex].getSupply();
                    }
                    //// BACKHAUL NON ANCORA IMPLEMENTATO
                    // salva i vertici in una stringa da stampare alla fine
                    routeString += Integer.toString(vertex + 1) + " - ";
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
        for (int i = 0; i < customers.length + 1; i++) {
            // j=i+1 per creare la matrice simmetrica
            // customer.length+1 perché serve anche la distanza con il deposito
            for (int j = i + 1; j < customers.length + 1; j++) {
                if (i == 0) {
                    //distanza tra deposito e customer
                    d = calculateDistance(depot, customers[j - 1]);
                } else {
                    //distanza tra due customers
                    d = calculateDistance(customers[i - 1], customers[j - 1]);
                }

                tableDistances[i][j] = d;
                tableDistances[j][i] = d;
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
    public void initializeRoutesLinehaul() {
        for (Integer delivery : deliveries) {
            routesLinehaul.add(new Route(delivery, customers[delivery].getDemand(), 0));
        }
    }

    /**
     * Inizializza le routes backhaul iniziali
     */
    public void initializeRoutesBackhaul() {
        for (Integer pickup : pickups) {
            routesBackhaul.add(new Route(pickup, 0, customers[pickup].getSupply()));
        }
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo sequenziale
     */
    public void algoritmoClarkeWrightSequenziale() {
        //LINEHAUL

        // scorre la tabella dei savings
        for (SavingOccurrence occurrence : sortedSavingsLinehaul) {
            int routeI = findRoute(occurrence.i, true);
            int routeJ = findRoute(occurrence.j, true);
            boolean iFirst = routesLinehaul.get(routeI).firstCustomer() == occurrence.i;
            boolean iLast = routesLinehaul.get(routeI).lastCustomer() == occurrence.i;
            boolean jFirst = routesLinehaul.get(routeJ).firstCustomer() == occurrence.j;
            boolean jLast = routesLinehaul.get(routeJ).lastCustomer() == occurrence.j;

            // per fare il merge tra due route devono essere rispettate tre condizioni
            // condizione 1: le route di i e j devono essere diverse
            if ((routeI != routeJ)
                    && // condizione 2:  la somma dello spazio occupato dalle due route deve essere <= maxcapacity
                    (routesLinehaul.get(routeI).getDelivery() + routesLinehaul.get(routeJ).getDelivery() <= depot.getMaxCapacity())
                    && // condizione 3: i e j sono first o last
                    ((iFirst || iLast) && (jFirst || jLast))) {
                //si possono unire le due route
                if (iLast && jFirst) {
                    //i è last, j è first

                    //unisci j ad i ed elimina  poi j
                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                    routesLinehaul.remove(routeJ);
                } else {
                    if (jLast && iFirst) {
                        //j è last, i è first

                        //unisci i ad j ed elimina  poi i
                        routesLinehaul.get(routeJ).merge(routesLinehaul.get(routeI));
                        routesLinehaul.remove(routeI);
                    } else {
                        if ((iLast && jLast) || (iFirst && jFirst)) {
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
        if (routesBackhaul.size() > depot.numberOfVehicles()) {
            // scorre la tabella dei savings
            for (SavingOccurrence occurrence : sortedSavingsBackhaul) {
                int routeI = findRoute(occurrence.i, false);
                int routeJ = findRoute(occurrence.j, false);
                boolean iFirst = routesBackhaul.get(routeI).firstCustomer() == occurrence.i ? true : false;
                boolean iLast = routesBackhaul.get(routeI).lastCustomer() == occurrence.i ? true : false;
                boolean jFirst = routesBackhaul.get(routeJ).firstCustomer() == occurrence.j ? true : false;
                boolean jLast = routesBackhaul.get(routeJ).lastCustomer() == occurrence.j ? true : false;

                // per fare il merge tra due route devono essere rispettate tre condizioni
                // condizione 1: le route di i e j devono essere diverse
                if ((routeI != routeJ)
                        && // condizione 2:  la somma dello spazio occupato dalle due route deve essere <= maxcapacity
                        (routesBackhaul.get(routeI).getPickupLoad() + routesBackhaul.get(routeJ).getPickupLoad() <= depot.getMaxCapacity())
                        && // condizione 3: i e j sono first o last
                        ((iFirst || iLast) && (jFirst || jLast))) {
                    //si possono unire le due route
                    if (iLast && jFirst) {
                        //i è last, j è first

                        //unisci j ad i ed elimina  poi j
                        routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));
                        routesBackhaul.remove(routeJ);
                    } else {
                        if (jLast && iFirst) {
                            //j è last, i è first

                            //unisci i ad j ed elimina  poi i
                            routesBackhaul.get(routeJ).merge(routesBackhaul.get(routeI));
                            routesBackhaul.remove(routeI);
                        } else {
                            if ((iLast && jLast) || (iFirst && jFirst)) {
                                // si effettua il reverse di una delle due route
                                routesBackhaul.get(routeJ).reverse();

                                //unisci j invertito ad i ed elimina  poi j
                                routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));
                                routesBackhaul.remove(routeJ);
                            }
                        }
                    }
                }
            }
        }

        setSortedSavings();
        //UNIONE LINEHAUL E BACKHAUL

        unionRoutesSequenziale();

    }

    /**
     * Trova la route di cui fa parte il customer
     *
     * @param customerToFind Il customer di cui si vuole cercare la route
     * @param linehaul Flag impostato a true se il customer è linehaul, false
     * altrimenti
     * @return L'indice della route di cui fa parte il customer
     */
    public int findRoute(int customerToFind, boolean linehaul) {
        if (linehaul) {
            for (Route route : routesLinehaul) {
                if (route.findCustomer(customerToFind)) {
                    return routesLinehaul.indexOf(route);
                }
            }
        } else {
            for (Route route : routesBackhaul) {
                if (route.findCustomer(customerToFind)) {
                    return routesBackhaul.indexOf(route);
                }
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

    /**
     * Riordina i savings in ordine decrescente per i first e last delle routes
     * linehaul e backhaul
     */
    public void setSortedSavings() {
        int i;  //riga
        int j;   //colonna

        for (Route routeL : routesLinehaul) {
            for (Route routeB : routesBackhaul) {
                i = routeL.firstCustomer();
                j = routeB.firstCustomer();
                sortedSavingsUnion.add(new SavingOccurrence(i, j, tableSavings[i][j]));
                if (routeL.getRoute().size() > 1) {
                    i = routeL.lastCustomer();
                    sortedSavingsUnion.add(new SavingOccurrence(i, j, tableSavings[i][j]));

                    if (routeB.getRoute().size() > 1) {
                        j = routeL.lastCustomer();
                        sortedSavingsUnion.add(new SavingOccurrence(i, j, tableSavings[i][j]));

                        i = routeL.firstCustomer();
                        sortedSavingsUnion.add(new SavingOccurrence(i, j, tableSavings[i][j]));
                    }
                }
            }
        }

        //riorndina i savings in ordine decrescente
        Collections.sort(sortedSavingsUnion, (so1, so2) -> Double.compare(so1.s, so2.s));

        Collections.reverse(sortedSavingsUnion);
    }

    /**
     * Unione delle routes linehaul e backhaul
     */
    public void unionRoutesSequenziale() {
        routesLinehaul.addAll(routesBackhaul);

        // scorre la tabella dei savings
        for (SavingOccurrence occurrence : sortedSavingsUnion) {
            int routeI = findRoute(occurrence.i, true);
            int routeJ = findRoute(occurrence.j, true);
            boolean iFirst = routesLinehaul.get(routeI).firstCustomer() == occurrence.i;
            boolean iLast = routesLinehaul.get(routeI).lastCustomer() == occurrence.i;
            boolean jFirst = routesLinehaul.get(routeJ).firstCustomer() == occurrence.j;
            boolean jLast = routesLinehaul.get(routeJ).lastCustomer() == occurrence.j;

            boolean iIsUnion = routesLinehaul.get(routeI).isUnion() || routesLinehaul.get(routeJ).isUnion();

            // per fare il merge tra due route devono essere rispettate tre condizioni
            // condizione 1: le route di i e j devono essere diverse ed la route i non deve essere stata unita in precedenza
            if ((routeI != routeJ)
                    && // coondizione 2: la routeIdeve contenere solo linehaul
                    (!iIsUnion)
                    && // condizione 3: i e j sono first o last
                    ((iFirst || iLast) && (jFirst || jLast))) {
                //si possono unire le due route

                if (iFirst && jLast) {
                    // si effettua il reverse della route di i
                    routesLinehaul.get(routeI).reverse();

                    // si effettua il reverse della route di j
                    routesLinehaul.get(routeJ).reverse();
                } else {
                    if (iLast && jLast) {
                        // si effettua il reverse della route di j
                        routesLinehaul.get(routeJ).reverse();
                    } else {
                        if (iFirst && jFirst) {
                            // si effettua il reverse della route di i
                            routesLinehaul.get(routeI).reverse();
                        }
                    }
                }
                //unisci j ad i ed elimina  poi j
                routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                routesLinehaul.remove(routeJ);

                // segna la route come completa perché contiene linehaul e backhaul
                routesLinehaul.get(routeI).setUnion();
            }
        }
    }

    /**
     * Copia le route sequenziali e svuota routeLinehaul e routeBackhaul
     */
    public void copyRoutes() {
        routes.addAll(routesLinehaul);

        routesLinehaul.clear();
        routesBackhaul.clear();
    }

    /**
     * Algoritmo Clarke & Wright parallelo
     *
     * CLAUDIA
     */
    public void parallelAlgorithm() {
        ArrayList<Integer> checked = new ArrayList<Integer>();    //customer collegati ad ogni iterazione. Svuotato ogni volta che si riprende a leggere a leggere i saving dall'inizio

        //
        //DA VEDERE COME INIZIARE LA PRIMA ITERAZIONE forse con un while(...)
        for (SavingOccurrence savingOccurrence : sortedSavingsLinehaul) {
            checked.add(savingOccurrence.i);
            checked.add(savingOccurrence.j);

            if (!savingOccurrence.c) {

                //
                //verificare le condizioni e se possibile unire le route di i e j
                //savingOccurrence.checked();   //segnare il seving come utilizzato nell'iterazione
            }
        }
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo parallelo
     *
     * DENNIS
     * NON FUNZIONANTE
     */
    public void algoritmoClarkeWrightParallelo() {
        int k = 0;  // indice per scorrimento sortedSavingsLinehaul
        boolean currentSavingFlag = false;   // flag per uscire da ciclo dato che le routes vanno popolate in parallelo
        ArrayList<Integer> usedCustomers = new ArrayList<Integer>();    // lista dei customer già utilizzati
        
        // se i savings correnti i o j sono primo o ultimo nella route corrente
        boolean iFirst = false;
        boolean jFirst = false;
        boolean iLast = false;
        boolean jLast = false;

        routes.clear();
        // crea un numero di routes pari al numero di veicoli
        for (int i = 0; i < depot.numberOfVehicles(); i++) {
            routes.add(new Route());
        }

        // eseguire il ciclo fino a quando non sono stati visitati tutti i customers
        while (usedCustomers.size() < deliveries.size()) {
            k=0;
            // stampa test
            System.out.println("customers usati: " + usedCustomers.size());
            // scorre lista di route
            for (Route route : routes) {
                k=0;
                currentSavingFlag = false;
                // per ogni route scorre la lista di saving e aggiunge il primo utilizzabile
                while (k < sortedSavingsLinehaul.size() && !currentSavingFlag) {
                    // stampa test
                    System.out.println("saving corrente: " + sortedSavingsLinehaul.get(k).i + " - " + sortedSavingsLinehaul.get(k).j);
                    
                    // se i savings correnti i o j sono già stati inseriti in una route
                    boolean iUsed = usedCustomers.contains(sortedSavingsLinehaul.get(k).i);
                    boolean jUsed = usedCustomers.contains(sortedSavingsLinehaul.get(k).j);

                    /*
                    *** INSERIRE CALCOLO DEL COSTO, ECC
                     */
                    
                    // se la route non è vuota
                    if (!route.getRoute().isEmpty() && !currentSavingFlag){
                        iFirst = route.firstCustomer() == sortedSavingsLinehaul.get(k).i;
                        jFirst = route.firstCustomer() == sortedSavingsLinehaul.get(k).j;
                        iLast = route.lastCustomer() == sortedSavingsLinehaul.get(k).i;
                        jLast = route.lastCustomer() == sortedSavingsLinehaul.get(k).j;

                        // se primo customer di route corrisponde al saving corrente i e j non è stato visitato
                        if (iFirst && !jUsed && !currentSavingFlag) {
                            // aggiunge il customer j in testa
                            route.getRoute().add(0, sortedSavingsLinehaul.get(k).j);

                            usedCustomers.add(sortedSavingsLinehaul.get(k).j);

                            currentSavingFlag = true;
                        } 
                        // se primo customer di route corrisponde al saving corrente j e i non è stato visitato
                        else if (jFirst && !iUsed && !currentSavingFlag) {
                            // aggiunge il customer i in testa
                            route.getRoute().add(0, sortedSavingsLinehaul.get(k).i);

                            usedCustomers.add(sortedSavingsLinehaul.get(k).i);

                            currentSavingFlag = true;
                        } 
                        // se ultimo customer di route corrisponde al saving corrente i e j non è stato visitato
                        else if (iLast && !jUsed && !currentSavingFlag) {
                            // aggiunge il customer j in coda
                            route.getRoute().add(route.getRoute().size() - 1, sortedSavingsLinehaul.get(k).j);

                            usedCustomers.add(sortedSavingsLinehaul.get(k).j);

                            currentSavingFlag = true;
                        }
                        // se ultimo customer di route corrisponde al saving corrente j e i non è stato visitato
                        else if (jLast && !iUsed && !currentSavingFlag) {
                            // aggiunge il customer i in coda
                            route.getRoute().add(route.getRoute().size() - 1, sortedSavingsLinehaul.get(k).i);

                            usedCustomers.add(sortedSavingsLinehaul.get(k).i);

                            currentSavingFlag = true;
                        }
                    }
                    // se route è vuota, i e j non sono ancora stati usati
                    else if (route.getRoute().isEmpty() && !iUsed && !jUsed && !currentSavingFlag) {
                        route.getRoute().add(sortedSavingsLinehaul.get(k).i);
                        route.getRoute().add(sortedSavingsLinehaul.get(k).j);

                        usedCustomers.add(sortedSavingsLinehaul.get(k).i);
                        usedCustomers.add(sortedSavingsLinehaul.get(k).j);

                        currentSavingFlag = true;
                    }

                    k++;
                }
            }
        }

        // stampa routes di controllo
        int x = 0;
        for (Route route : routes) {

            System.out.print("\nroute " + x + ": ");
            for (Integer rotta : route.getRoute()) {
                System.out.print(rotta + " - ");
            }
            x++;
        }
        // stampa routes di controllo
    }
}
