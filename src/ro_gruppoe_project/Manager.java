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
    private ArrayList<Customer> customers = new ArrayList<Customer>();   // vettore di customers
    private ArrayList<Customer> customersSorted = new ArrayList<Customer>();   // vettore di customers ordinati per capacità
    private ArrayList<Integer> deliveries = new ArrayList<Integer>();   // lista di indici dei customer linehaul
    private ArrayList<Integer> pickups = new ArrayList<Integer>();  // lista di indici dei customer backhaul
    private double[][] tableDistances; // tabella delle distanze
    private double[][] tableSavings;   // tabella dei savings
    private ArrayList<SavingOccurrence> sortedSavingsLinehaul = new ArrayList<SavingOccurrence>();  // savings linehaul ordinati
    private ArrayList<SavingOccurrence> sortedSavingsBackhaul = new ArrayList<SavingOccurrence>();  // savings backhaul ordinati
    private ArrayList<SavingOccurrence> sortedSavingsUnion = new ArrayList<SavingOccurrence>();  // savings ordinati per l'unione di linehaul e backhaul
    private ArrayList<Route> routesLinehaul = new ArrayList<Route>();   // insieme delle routesSequenziale Linehaul
    private ArrayList<Route> routesBackhaul = new ArrayList<Route>();   // insieme delle routesSequenziale Backhaul
    private ArrayList<Route> routesSequenziale = new ArrayList<Route>();    // copia delle routesSequenziale finale
    private ArrayList<Route> routesParallelo = new ArrayList<Route>();    // copia delle routesParallelo finale
    private double totalCost = 0;

    // tempo di esecuzione
    long startTime;
    long endTime;
    long executionTime;

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
                        System.out.println("numero customer: " + sCurrentLine);
                        break;
                    case 2: // ??? default
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
                        customers.add(new Customer(xC, yC, delivery, pickup));
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
    }

    /**
     * Scrive il file dei risultati nella cartella "output"
     *
     * @param algorithm Algoritmo sequenziale o parallelo
     */
    public void writeFile(String algorithm) {
        int deliveryLoad = 0;
        int pickupLoad = 0;
        String routeString = "";

        try {
            new File("output").mkdirs();
            FileWriter writer = new FileWriter("output/Solution " + algorithm + " " + nameFile);
            // stampa titolo
            writer.write("Text File with Solution Of Problem: " + nameFile + "\r\n");
            writer.write("Algorithm: " + algorithm + "\r\n");
            writer.write("Execution time: " + executionTime + " ms.\r\n\r\n");
            // stampa dettagli problema
            writer.write("\r\nPROBLEM DETAILS:\r\n");
            writer.write("Customers: " + customers.size() + "\r\n");
            writer.write("Max Load: " + depot.getMaxCapacity() + "\r\n");
            writer.write("Max Cost: " + "99999999999999???" + "\r\n");
            // stampa dettagli soluzione
            writer.write("\r\nSOLUTION DETAILS:\r\n");
            writer.write("Total Cost: " + totalCost + "\r\n");
            writer.write("Routes Of the Solution: " + routesLinehaul.size() + "\r\n\r\n");

            // scrittura di tutte le route
            for (Route route : routesLinehaul) {
                writer.write("ROUTE " + routesLinehaul.indexOf(route) + ":\r\n");
                writer.write("Cost: " + route.getCost() + "\r\n");
                deliveryLoad = 0;
                pickupLoad = 0;
                routeString = "0 - ";
                // per ogni vertice della route
                for (Integer vertex : route.getRoute()) {
                    // Linehaul: calcola la somma di delivery di tutta la route
                    if (deliveries.contains(vertex)) {
                        deliveryLoad += customers.get(vertex).getDemand();
                    }
                    // Backhaul: calcola la somma di pick-up di tutta la route
                    if (pickups.contains(vertex)) {
                        pickupLoad += customers.get(vertex).getSupply();
                    }
                    // salva i vertici in una stringa da stampare alla fine
                    routeString += Integer.toString(vertex + 1) + " - ";
                }
                routeString += "0";

                writer.write("Delivery Load: " + deliveryLoad + "\r\n");
                writer.write("Pick-Up Load: " + pickupLoad + "\r\n");
                writer.write("Customers in Route: " + route.getRoute().size() + "\r\n");
                writer.write("Vertex Sequence: " + "\r\n" + routeString);

                writer.write("\r\n\r\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Restituisce il nome del file in input
     *
     * @return Nome del file
     */
    public String getNameFile() {
        return nameFile;
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
        tableDistances = new double[customers.size() + 1][customers.size() + 1];

        double d;

        // customer.length+1 perché serve anche la distanza con il deposito
        for (int i = 0; i < customers.size() + 1; i++) {
            // j=i+1 per creare la matrice simmetrica
            // customer.length+1 perché serve anche la distanza con il deposito
            for (int j = i + 1; j < customers.size() + 1; j++) {
                if (i == 0) {
                    // distanza tra deposito e customer
                    d = calculateDistance(depot, customers.get(j - 1));
                } else {
                    // distanza tra due customers
                    d = calculateDistance(customers.get(i - 1), customers.get(j - 1));
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
        // creazione della tabella dei savings
        tableSavings = new double[customers.size()][customers.size()];

        double s;

        // si popola la tabella dei saving
        for (int i = 0; i < customers.size(); i++) {
            for (int j = i + 1; j < customers.size(); j++) {
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
        // estrazione delle occorrenze dei savings dalla tabella con le relative righe e colonne di riferimento
        for (int i = 0; i < deliveries.size(); i++) {
            for (int j = i + 1; j < deliveries.size(); j++) {
                sortedSavingsLinehaul.add(new SavingOccurrence(deliveries.get(i), deliveries.get(j), tableSavings[deliveries.get(i)][deliveries.get(j)]));
            }
        }

        // riordina i savings in ordine decrescente
        Collections.sort(sortedSavingsLinehaul, (so1, so2) -> Double.compare(so1.s, so2.s));

        Collections.reverse(sortedSavingsLinehaul);
    }

    /**
     * Riordina i savings backhaul in ordine decrescente
     */
    public void setSortedSavingsBackhaul() {
        // estrazione delle occorrenze dei savings dalla tabella con le relative righe e colonne di riferimento
        for (int i = 0; i < pickups.size(); i++) {
            for (int j = i + 1; j < pickups.size(); j++) {
                sortedSavingsBackhaul.add(new SavingOccurrence(pickups.get(i), pickups.get(j), tableSavings[pickups.get(i)][pickups.get(j)]));
            }
        }

        // riordina i savings in ordine decrescente
        Collections.sort(sortedSavingsBackhaul, (so1, so2) -> Double.compare(so1.s, so2.s));

        Collections.reverse(sortedSavingsBackhaul);
    }

    /**
     * Inizializza le routesSequenziale linehaul iniziali
     */
    public void initializeRoutesLinehaul() {
        for (Integer delivery : deliveries) {
            routesLinehaul.add(new Route(delivery, customers.get(delivery).getDemand(), 0));
        }
    }

    /**
     * Inizializza le routesSequenziale backhaul iniziali
     */
    public void initializeRoutesBackhaul() {
        for (Integer pickup : pickups) {
            routesBackhaul.add(new Route(pickup, 0, customers.get(pickup).getSupply()));
        }
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
        totalCost = 0;

        for (Route route : routesLinehaul) {
            ArrayList<Integer> listCustomer = route.getRoute();

            if (listCustomer.size() > 1) {
                // route composta da più customer
                for (int i = 0; i < listCustomer.size() - 1; i++) {
                    // somma tutti i costi tra i customer
                    cost += tableDistances[listCustomer.get(i) + 1][listCustomer.get(i + 1) + 1];
                }
                // somma i costi tra i first e i last con il depot
                cost += tableDistances[0][listCustomer.get(0) + 1] + tableDistances[0][listCustomer.get(listCustomer.size() - 1) + 1];
            } else {
                // route composta da un solo customer
                cost += tableDistances[0][listCustomer.get(0) + 1] * 2;
            }

            route.setCost(cost);
            totalCost += cost;
            cost = 0;
        }
    }

    /**
     * Riordina i savings in ordine decrescente per i first e last delle
     * routesSequenziale linehaul e backhaul
     */
    public void setSortedSavings() {
        int lineFirst;
        int lineLast;

        int backFirst;
        int backLast;

        for (Route routeL : routesLinehaul) {
            lineFirst = routeL.firstCustomer();

            for (Route routeB : routesBackhaul) {
                backFirst = routeB.firstCustomer();
                sortedSavingsUnion.add(new SavingOccurrence(lineFirst, backFirst, tableSavings[lineFirst][backFirst]));
                if (routeL.getRoute().size() > 1) {
                    lineLast = routeL.lastCustomer();
                    sortedSavingsUnion.add(new SavingOccurrence(lineLast, backFirst, tableSavings[lineFirst][backFirst]));

                    backLast = routeB.lastCustomer();

                    if (routeB.getRoute().size() > 1) {
                        sortedSavingsUnion.add(new SavingOccurrence(lineFirst, backLast, tableSavings[lineFirst][backLast]));
                    }

                    if (routeL.getRoute().size() > 1 && routeB.getRoute().size() > 1) {
                        sortedSavingsUnion.add(new SavingOccurrence(lineLast, backLast, tableSavings[lineLast][backLast]));
                    }
                }
            }
        }

        // riorndina i savings in ordine decrescente
        Collections.sort(sortedSavingsUnion, (so1, so2) -> Double.compare(so1.s, so2.s));

        Collections.reverse(sortedSavingsUnion);
    }

    /**
     * Unione delle routes linehaul e backhaul
     */
    public void unionRoutes() {
        // variabili d'appoggio per le condizioni
        int routeI;
        int routeJ;
        boolean iFirst;
        boolean iLast;
        boolean jFirst;
        boolean jLast;

        routesLinehaul.addAll(routesBackhaul);

        int size = routesLinehaul.size();

        // finché tutte le routes non sono unite
        while (size > depot.numberOfVehicles()) {
            // scorre la tabella dei savings
            for (SavingOccurrence occurrence : sortedSavingsUnion) {
                routeI = findRoute(occurrence.i, true);
                routeJ = findRoute(occurrence.j, true);
                iFirst = routesLinehaul.get(routeI).firstCustomer() == occurrence.i;
                iLast = routesLinehaul.get(routeI).lastCustomer() == occurrence.i;
                jFirst = routesLinehaul.get(routeJ).firstCustomer() == occurrence.j;
                jLast = routesLinehaul.get(routeJ).lastCustomer() == occurrence.j;

                boolean iIsUnion = routesLinehaul.get(routeI).isUnion() || routesLinehaul.get(routeJ).isUnion();

                // per fare il merge tra due route devono essere rispettate tre condizioni
                // condizione 1: le route di i e j devono essere diverse ed la route i non deve essere stata unita in precedenza
                if ((routeI != routeJ)
                        && // coondizione 2: la route deve contenere solo linehaul
                        (!iIsUnion)
                        && // condizione 3: i e j sono first o last
                        ((iFirst || iLast) && (jFirst || jLast))) {
                    // si possono unire le due route

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
                    // unisci j ad i
                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));

                    // segna la route come completa perché contiene linehaul e backhaul
                    routesLinehaul.get(routeI).setUnion();

                    // elimina  poi j
                    routesLinehaul.remove(routeJ);

                    size = routesLinehaul.size();
                }
            }
        }
    }

    /**
     * Copia le route sequenziali e svuota routeLinehaul e routeBackhaul
     *
     * @param sequenziale Indica se si fa la copia dei risultati dell'algoritmo
     * sequenziale o parallelo
     */
    public void copyRoutes(boolean sequenziale) {
        if (sequenziale) {
            routesSequenziale.addAll(routesLinehaul);
        } else {
            routesParallelo.addAll(routesLinehaul);
        }

        routesLinehaul.clear();
        routesBackhaul.clear();
    }

    public void sortCustomer() {
        customersSorted.addAll(customers);

        Collections.sort(customersSorted);
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo sequenziale
     *
     * @return Il tempo di esecuzione
     */
    public long algoritmoClarkeWrightSequenziale() {
        // startTime time
        startTime = System.currentTimeMillis();

        ArrayList<Integer> usedCustomers = new ArrayList<>();    // lista di customer inseriti nelle route
        int currentFirst = 0; // primo customer della route che si sta popolando
        int currentLast = 0;  // ultimo customer della route che si sta popolando
        int k = 0;

        // variabili d'appoggio per le condizioni
        int routeI = 0;
        int routeJ = 0;
        boolean iFirst = false;
        boolean iLast = false;
        boolean jFirst = false;
        boolean jLast = false;

        boolean cond1 = false;
        boolean cond2 = false;
        boolean cond3 = false;
        boolean cond4 = false;
        boolean cond5 = false;
        boolean cond6 = false;

        // LINEHAUL SEQUENZIALE
        while (routesLinehaul.size() > depot.numberOfVehicles()) {
            // si identificano i primi first e last della route da creare
            for (SavingOccurrence occurrence : sortedSavingsLinehaul) {
                if (!usedCustomers.contains(occurrence.i) && !usedCustomers.contains(occurrence.j)) {
                    routeI = findRoute(occurrence.i, true);
                    routeJ = findRoute(occurrence.j, true);

                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                    routesLinehaul.remove(routeJ);

                    usedCustomers.add(occurrence.i);
                    usedCustomers.add(occurrence.j);

                    currentFirst = occurrence.i;
                    currentLast = occurrence.j;

                    break;  // uscita forzata perché viene identificata la prima coppia che non è stata ancora inserita nelle route
                }
            }

            // scorre la tabella dei savings
            for (k = 0; k < sortedSavingsLinehaul.size(); k++) {
                SavingOccurrence occurrence = sortedSavingsLinehaul.get(k);
                cond1 = occurrence.i == currentFirst;
                cond2 = occurrence.i == currentLast;
                cond3 = occurrence.j == currentFirst;
                cond4 = occurrence.j == currentLast;
                cond5 = usedCustomers.contains(occurrence.i);
                cond6 = usedCustomers.contains(occurrence.j);

                if ((((cond1) != (cond2))
                        != ((cond3) != (cond4)))
                        && (cond5 != cond6) && routesLinehaul.size() > depot.numberOfVehicles()) {

                    routeI = findRoute(occurrence.i, true);
                    routeJ = findRoute(occurrence.j, true);
                    iFirst = routesLinehaul.get(routeI).firstCustomer() == occurrence.i;
                    iLast = routesLinehaul.get(routeI).lastCustomer() == occurrence.i;
                    jFirst = routesLinehaul.get(routeJ).firstCustomer() == occurrence.j;
                    jLast = routesLinehaul.get(routeJ).lastCustomer() == occurrence.j;

                    // per fare il merge tra due route devono essere rispettate tre condizioni
                    // condizione 1: le route di i e j devono essere diverse
                    if ((routeI != routeJ)
                            && // condizione 2:  la somma dello spazio occupato dalle due route deve essere <= maxcapacity
                            (routesLinehaul.get(routeI).getDelivery() + routesLinehaul.get(routeJ).getDelivery() <= depot.getMaxCapacity())
                            && // condizione 3: i e j sono first o last
                            ((iFirst || iLast) && (jFirst || jLast))) {
                        // si possono unire le due route
                        if (iLast && jFirst) {
                            // i è last, j è first

                            // unisci j ad i ed elimina  poi j
                            routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));

                            currentFirst = routesLinehaul.get(routeI).firstCustomer();
                            currentLast = routesLinehaul.get(routeI).lastCustomer();

                            routesLinehaul.remove(routeJ);
                        } else {
                            if (jLast && iFirst) {
                                // j è last, i è first

                                // unisci i ad j ed elimina  poi i
                                routesLinehaul.get(routeJ).merge(routesLinehaul.get(routeI));

                                currentFirst = routesLinehaul.get(routeJ).firstCustomer();
                                currentLast = routesLinehaul.get(routeJ).lastCustomer();

                                routesLinehaul.remove(routeI);
                            } else {
                                if ((iLast && jLast) || (iFirst && jFirst)) {
                                    // si effettua il reverse di una delle due route
                                    routesLinehaul.get(routeJ).reverse();

                                    // unisci j invertito ad i ed elimina  poi j
                                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));

                                    currentFirst = routesLinehaul.get(routeI).firstCustomer();
                                    currentLast = routesLinehaul.get(routeI).lastCustomer();

                                    routesLinehaul.remove(routeJ);
                                }
                            }
                        }
                        // si aggiunge il nuovo customer alla lista di quelli già presenti nelle route
                        if (usedCustomers.contains(occurrence.i)) {
                            usedCustomers.add(occurrence.j);
                        } else {
                            usedCustomers.add(occurrence.i);
                        }
                        k = 0;    // riparte dal saving maggiore

                        if (routesLinehaul.size() == depot.numberOfVehicles()) {
                            break;
                        }
                    }
                }
            }
        }

        // BACKHAUL SEQUENZIALE
        usedCustomers.clear();
        currentFirst = 0; // primo customer della route che si sta popolando
        currentLast = 0;  // ultimo customer della route che si sta popolando
        k = 0;

        // variabili d'appoggio per le condizioni
        routeI = 0;
        routeJ = 0;
        iFirst = false;
        iLast = false;
        jFirst = false;
        jLast = false;

        cond1 = false;
        cond2 = false;
        cond3 = false;
        cond4 = false;
        cond5 = false;
        cond6 = false;

        while (routesBackhaul.size() > depot.numberOfVehicles()) {
            // si identificano i primi first e last  della route da creare
            for (SavingOccurrence occurrence : sortedSavingsBackhaul) {
                if (!usedCustomers.contains(occurrence.i) && !usedCustomers.contains(occurrence.j)) {
                    routeI = findRoute(occurrence.i, false);
                    routeJ = findRoute(occurrence.j, false);

                    routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));
                    routesBackhaul.remove(routeJ);

                    usedCustomers.add(occurrence.i);
                    usedCustomers.add(occurrence.j);

                    currentFirst = occurrence.i;
                    currentLast = occurrence.j;

                    break;  // uscita forzata perché viene identificata la prima coppia che non è stata ancora inserita nelle route
                }
            }

            // scorre la tabella dei savings
            for (k = 0; k < sortedSavingsBackhaul.size(); k++) {
                SavingOccurrence occurrence = sortedSavingsBackhaul.get(k);
                cond1 = occurrence.i == currentFirst;
                cond2 = occurrence.i == currentLast;
                cond3 = occurrence.j == currentFirst;
                cond4 = occurrence.j == currentLast;
                cond5 = usedCustomers.contains(occurrence.i);
                cond6 = usedCustomers.contains(occurrence.j);

                if ((((cond1) != (cond2))
                        != ((cond3) != (cond4)))
                        && (cond5 != cond6) && routesBackhaul.size() > depot.numberOfVehicles()) {

                    routeI = findRoute(occurrence.i, false);
                    routeJ = findRoute(occurrence.j, false);
                    iFirst = routesBackhaul.get(routeI).firstCustomer() == occurrence.i;
                    iLast = routesBackhaul.get(routeI).lastCustomer() == occurrence.i;
                    jFirst = routesBackhaul.get(routeJ).firstCustomer() == occurrence.j;
                    jLast = routesBackhaul.get(routeJ).lastCustomer() == occurrence.j;

                    // per fare il merge tra due route devono essere rispettate tre condizioni
                    // condizione 1: le route di i e j devono essere diverse
                    if ((routeI != routeJ)
                            && // condizione 2:  la somma dello spazio occupato dalle due route deve essere <= maxcapacity
                            (routesBackhaul.get(routeI).getPickup() + routesBackhaul.get(routeJ).getPickup() <= depot.getMaxCapacity())
                            && // condizione 3: i e j sono first o last
                            ((iFirst || iLast) && (jFirst || jLast))) {
                        // si possono unire le due route
                        if (iLast && jFirst) {
                            // i è last, j è first

                            // unisci j ad i ed elimina  poi j
                            routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));

                            currentFirst = routesBackhaul.get(routeI).firstCustomer();
                            currentLast = routesBackhaul.get(routeI).lastCustomer();

                            routesBackhaul.remove(routeJ);
                        } else {
                            if (jLast && iFirst) {
                                // j è last, i è first

                                // unisci i ad j ed elimina  poi i
                                routesBackhaul.get(routeJ).merge(routesBackhaul.get(routeI));

                                currentFirst = routesBackhaul.get(routeJ).firstCustomer();
                                currentLast = routesBackhaul.get(routeJ).lastCustomer();

                                routesBackhaul.remove(routeI);
                            } else {
                                if ((iLast && jLast) || (iFirst && jFirst)) {
                                    // si effettua il reverse di una delle due route
                                    routesBackhaul.get(routeJ).reverse();

                                    // unisci j invertito ad i ed elimina  poi j
                                    routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));

                                    currentFirst = routesBackhaul.get(routeI).firstCustomer();
                                    currentLast = routesBackhaul.get(routeI).lastCustomer();

                                    routesBackhaul.remove(routeJ);
                                }
                            }
                        }
                        // si aggiunge il nuovo customer alla lista di quelli già presenti nelle route
                        if (usedCustomers.contains(occurrence.i)) {
                            usedCustomers.add(occurrence.j);
                        } else {
                            usedCustomers.add(occurrence.i);
                        }
                        k = 0;    // riparte dal saving maggiore

                        if (routesBackhaul.size() == depot.numberOfVehicles()) {
                            break;
                        }
                    }
                }
            }
        }

        setSortedSavings();
        // UNIONE LINEHAUL E BACKHAUL
        unionRoutes();

        // endTime time
        endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;
        return executionTime;
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo parallelo
     *
     * @return Il tempo di esecuzione
     */
    public long algoritmoClarkeWrightParallelo() {
        // startTime time
        startTime = System.currentTimeMillis();

        // lista delle routesSequenziale utilizzate almeno una volta
        ArrayList<Route> usedRoutes = new ArrayList<>();
        // lista delle ultime route usate (per tenere conto di quali routesSequenziale non utilizzare e rendere l'algoritmo parallelo)
        ArrayList<Route> usedRoutesTurn = new ArrayList<>();
        // contatore dei savings utilizzati
        int counterSavings = 0;
        // routesSequenziale dei savings correnti
        int routeI = 0;
        int routeJ = 0;
        // se i savings correnti i o j sono primo o ultimo nella route corrente
        boolean iFirst = false;
        boolean jFirst = false;
        boolean iLast = false;
        boolean jLast = false;
        // se la richiesta è minore della capacità massima
        boolean ijCapacity = false;
        // se le routesSequenziale sono state utilizzate nel turno corrente
        boolean condI = false;
        boolean condJ = false;
        // se le routesParallele sono state utilizzate almeno una volta
        boolean condUsedI = false;
        boolean condUsedJ = false;
        // se le routesParallele sono routes base (ossia le prime che vanno a formare una route perché richiedono più spazio
        boolean condBaseI = false;
        boolean condBaseJ = false;
        // indice
        int k = 0;

        // indici di supporto/appoggio
        int indexVehicles = 0, indexCustomers = 0;
        int r, index;

        // fix delle prime N route di base dando priorità ai customer che richiedono una capacità maggiore
        while (indexVehicles < depot.numberOfVehicles() && indexCustomers < customersSorted.size()) {
            if (customersSorted.get(indexCustomers).getDemand() > 0) {
                index = customers.indexOf(customersSorted.get(indexCustomers));
                r = findRoute(index, true);
                routesLinehaul.get(r).base = true;
                usedRoutes.add(routesLinehaul.get(r));

                indexVehicles++;
            }
            indexCustomers++;
        }

        // LINEHAUL PARALLELO
        while (routesLinehaul.size() > depot.numberOfVehicles()) {
            // routesSequenziale dei savings correnti
            routeI = findRoute(sortedSavingsLinehaul.get(k).i, true);
            routeJ = findRoute(sortedSavingsLinehaul.get(k).j, true);
            // se i savings correnti i o j sono primo o ultimo nella route corrente            
            iFirst = routesLinehaul.get(routeI).firstCustomer() == sortedSavingsLinehaul.get(k).i;
            jFirst = routesLinehaul.get(routeJ).firstCustomer() == sortedSavingsLinehaul.get(k).j;
            iLast = routesLinehaul.get(routeI).lastCustomer() == sortedSavingsLinehaul.get(k).i;
            jLast = routesLinehaul.get(routeJ).lastCustomer() == sortedSavingsLinehaul.get(k).j;
            // se la richiesta è minore della capacità massima
            ijCapacity = depot.getMaxCapacity() >= (routesLinehaul.get(routeI).getDelivery() + routesLinehaul.get(routeJ).getDelivery());
            // se le routesParallelo sono state utilizzate nel turno corrente
            condI = usedRoutesTurn.contains(routesLinehaul.get(routeI));
            condJ = usedRoutesTurn.contains(routesLinehaul.get(routeJ));
            // se le routesParallelo sono state utilizzate almeno una volta
            condUsedI = usedRoutes.contains(routesLinehaul.get(routeI));
            condUsedJ = usedRoutes.contains(routesLinehaul.get(routeJ));
            // se la route è di base
            condBaseI = routesLinehaul.get(routeI).base;
            condBaseJ = routesLinehaul.get(routeJ).base;

            // salta saving corrente se non sono rispettate le condizioni
            if ((!(condI || condJ)) && (routeI != routeJ) && ijCapacity && (!condUsedI || !condUsedJ) && (condBaseI != condBaseJ)) {
                // iFirst - jLast: unisce i ad j ed elimina poi i
                if (iFirst && jLast) {
                    counterSavings++;

                    routesLinehaul.get(routeJ).merge(routesLinehaul.get(routeI));
                    usedRoutes.add(routesLinehaul.get(routeJ));
                    usedRoutesTurn.add(routesLinehaul.get(routeJ));
                    routesLinehaul.remove(routeI);
                } // iLast - jFirst: unisce j ad i ed elimina poi j
                else if (iLast && jFirst) {
                    counterSavings++;

                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                    usedRoutes.add(routesLinehaul.get(routeI));
                    usedRoutesTurn.add(routesLinehaul.get(routeI));
                    routesLinehaul.remove(routeJ);
                } // iFirst - jFirst OR iLast - jLast: effettua il reverse di j e unisce j invertito ad i ed elimina poi j
                else if ((iFirst && jFirst) || (iLast && jLast)) {
                    counterSavings++;

                    routesLinehaul.get(routeJ).reverse();
                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                    usedRoutes.add(routesLinehaul.get(routeI));
                    usedRoutesTurn.add(routesLinehaul.get(routeI));
                    routesLinehaul.remove(routeJ);
                }
            }
            k++;

            // se sono stati modificati il numero di routes pari al numero di veicoli (rende il parallelo)
            // azzera l'ArrayList, azzera il contatore e riparte dal primo saving
            if (counterSavings == depot.numberOfVehicles()) {
                usedRoutesTurn.clear();
                counterSavings = 0;
                k = 0;
            }
            // se si è giunti alla fine dei saving si riparte dal primo
            if (k == sortedSavingsLinehaul.size()) {
                usedRoutesTurn.clear();
                counterSavings = 0;
                k = 0;
            }
        }

        // BACKHAUL PARALLELO
        // lista delle routesSequenziale utilizzate almeno una volta
        usedRoutes = new ArrayList<>();
        // lista delle ultime route usate (per tenere conto di quali routesSequenziale non utilizzare e rendere l'algoritmo parallelo)
        usedRoutesTurn = new ArrayList<>();
        // contatore dei savings utilizzati
        counterSavings = 0;
        // routesSequenziale dei savings correnti
        routeI = 0;
        routeJ = 0;
        // se i savings correnti i o j sono primo o ultimo nella route corrente
        iFirst = false;
        jFirst = false;
        iLast = false;
        jLast = false;
        // se la richiesta è minore della capacità massima
        ijCapacity = false;
        // se le routesSequenziale sono state utilizzate nel turno corrente
        condI = false;
        condJ = false;
        // se le routesSequenziale sono state utilizzate almeno una volta
        condUsedI = false;
        condUsedJ = false;
        // se le routesParallele sono routes base (ossia le prime che vanno a formare una route perché richiedono più spazio
        condBaseI = false;
        condBaseJ = false;
        // indice
        k = 0;

        // indici di supporto/appoggio
        indexVehicles = 0;
        indexCustomers = 0;

        // fix delle prime N route di base dando priorità ai customer che richiedono una capacità maggiore
        while (indexVehicles < depot.numberOfVehicles() && indexCustomers < customersSorted.size()) {
            if (customersSorted.get(indexCustomers).getSupply() > 0) {
                index = customers.indexOf(customersSorted.get(indexCustomers));
                r = findRoute(index, false);
                routesBackhaul.get(r).base = true;
                usedRoutes.add(routesBackhaul.get(r));

                indexVehicles++;
            }
            indexCustomers++;
        }

        while (routesBackhaul.size() > depot.numberOfVehicles()) {
            // routesSequenziale dei savings correnti
            routeI = findRoute(sortedSavingsBackhaul.get(k).i, false);
            routeJ = findRoute(sortedSavingsBackhaul.get(k).j, false);
            // se i savings correnti i o j sono primo o ultimo nella route corrente            
            iFirst = routesBackhaul.get(routeI).firstCustomer() == sortedSavingsBackhaul.get(k).i;
            jFirst = routesBackhaul.get(routeJ).firstCustomer() == sortedSavingsBackhaul.get(k).j;
            iLast = routesBackhaul.get(routeI).lastCustomer() == sortedSavingsBackhaul.get(k).i;
            jLast = routesBackhaul.get(routeJ).lastCustomer() == sortedSavingsBackhaul.get(k).j;
            // se la richiesta è minore della capacità massima
            ijCapacity = depot.getMaxCapacity() >= (routesBackhaul.get(routeI).getPickup() + routesBackhaul.get(routeJ).getPickup());
            // se le routesSequenziale sono state utilizzate nel turno corrente
            condI = usedRoutesTurn.contains(routesBackhaul.get(routeI));
            condJ = usedRoutesTurn.contains(routesBackhaul.get(routeJ));
            // se le routesSequenziale sono state utilizzate almeno una volta
            condUsedI = usedRoutes.contains(routesBackhaul.get(routeI));
            condUsedJ = usedRoutes.contains(routesBackhaul.get(routeJ));
            // se la route è di base
            condBaseI = routesBackhaul.get(routeI).base;
            condBaseJ = routesBackhaul.get(routeJ).base;

            // salta saving corrente se non sono rispettate le condizioni
            if ((!(condI || condJ)) && (routeI != routeJ) && ijCapacity && (!condUsedI || !condUsedJ) && (condBaseI != condBaseJ)) {
                // iFirst - jLast: unisce i ad j ed elimina poi i
                if (iFirst && jLast) {
                    counterSavings++;

                    routesBackhaul.get(routeJ).merge(routesBackhaul.get(routeI));
                    usedRoutes.add(routesBackhaul.get(routeJ));
                    usedRoutesTurn.add(routesBackhaul.get(routeJ));
                    routesBackhaul.remove(routeI);
                } // iLast - jFirst: unisce j ad i ed elimina poi j
                else if (iLast && jFirst) {
                    counterSavings++;

                    routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));
                    usedRoutes.add(routesBackhaul.get(routeI));
                    usedRoutesTurn.add(routesBackhaul.get(routeI));
                    routesBackhaul.remove(routeJ);
                } // iFirst - jFirst OR iLast - jLast: effettua il reverse di j e unisce j invertito ad i ed elimina poi j
                else if ((iFirst && jFirst) || (iLast && jLast)) {
                    counterSavings++;

                    routesBackhaul.get(routeJ).reverse();
                    routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));
                    usedRoutes.add(routesBackhaul.get(routeI));
                    usedRoutesTurn.add(routesBackhaul.get(routeI));
                    routesBackhaul.remove(routeJ);
                }
            }
            k++;

            // se soo stati modificati il numero di routes pari al numero di veicoli (rende il parallelo)
            // azzera l'ArrayList, azzera il contatore e riparte dal primo saving
            if (counterSavings == depot.numberOfVehicles()) {
                usedRoutesTurn.clear();
                counterSavings = 0;
                k = 0;
            }

            // se si è giunti alla fine dei saving si riparte dal primo
            if (k == sortedSavingsBackhaul.size()) {
                usedRoutesTurn.clear();
                counterSavings = 0;
                k = 0;
            }
        }

        setSortedSavings();
        // UNIONE LINEHAUL E BACKHAUL
        unionRoutes();

        // endTime time
        endTime = System.currentTimeMillis();
        executionTime = endTime - startTime;
        return executionTime;
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo parallelo
     *
     * ERROR (vedi file A2.txt)
     */
    /*
    public void algoritmoClarkeWrightParalleloERROR() {
        int k = 0;  // indice per scorrimento sortedSavingsLinehaul
        boolean currentSavingFlag = false;   // routesUnited per uscire da ciclo dato che le routesSequenziale vanno popolate in parallelo
        ArrayList<Integer> usedCustomers = new ArrayList<>();    // lista dei customer già utilizzati

        // se i savings correnti i o j sono primo o ultimo nella route corrente
        boolean iFirst = false;
        boolean jFirst = false;
        boolean iLast = false;
        boolean jLast = false;

        // LINEHAUL PARALLELO ERROR
        // un veicolo per ogni customer
        if (deliveries.size() <= depot.numberOfVehicles()) {
            initializeRoutesLinehaul();
        } else {
            routesLinehaul.clear();
            // crea un numero di routesSequenziale pari al numero di veicoli
            for (int i = 0; i < depot.numberOfVehicles(); i++) {
                routesLinehaul.add(new Route());
            }

            // eseguire il ciclo fino a quando non sono stati visitati tutti i customers
            while (usedCustomers.size() < deliveries.size()) {
                // scorre lista di route
                for (Route route : routesLinehaul) {
                    k = 0; // si riparte dall'inizio della lista dei saving
                    currentSavingFlag = false;

                    int indexRoute = routesLinehaul.indexOf(route);

                    // se il numero di customer restanti è minore o uguale al numero di route, allora assegna un customer ad ogni route
                    if (((deliveries.size()) - (usedCustomers.size())) <= (depot.numberOfVehicles() - (indexRoute))) {
                        for (int i = 0; i < customers.length; i++) {
                            if ((!usedCustomers.contains(i)) && (customers[i].getDemand() != 0 && (depot.getMaxCapacity() >= (route.getDelivery() + customers[i].getDemand())))) {
                                routesLinehaul.get(indexRoute).getRoute().add(i);
                                routesLinehaul.get(indexRoute).addDelivery(customers[i].getDemand());
                                usedCustomers.add(i);

                                indexRoute++;
                            }
                        }
                        // altrimenti esegue l'algoritmo normale
                    } else {
                        // per ogni route scorre la lista di saving e aggiunge il primo utilizzabile
                        while (k < sortedSavingsLinehaul.size() && !currentSavingFlag) {
                            // se i savings correnti i o j sono già stati inseriti in una route
                            boolean iUsed = usedCustomers.contains(sortedSavingsLinehaul.get(k).i);
                            boolean jUsed = usedCustomers.contains(sortedSavingsLinehaul.get(k).j);

                            // se la richiesta è minore della capacità massima
                            boolean iCapacity = depot.getMaxCapacity() >= (route.getDelivery() + customers[sortedSavingsLinehaul.get(k).i].getDemand());
                            boolean jCapacity = depot.getMaxCapacity() >= (route.getDelivery() + customers[sortedSavingsLinehaul.get(k).j].getDemand());
                            boolean ijCapacity = depot.getMaxCapacity() >= (route.getDelivery() + customers[sortedSavingsLinehaul.get(k).i].getDemand() + customers[sortedSavingsLinehaul.get(k).j].getDemand());

                            // se la route non è vuota
                            if (!route.getRoute().isEmpty() && !currentSavingFlag) {
                                iFirst = route.firstCustomer() == sortedSavingsLinehaul.get(k).i;
                                jFirst = route.firstCustomer() == sortedSavingsLinehaul.get(k).j;
                                iLast = route.lastCustomer() == sortedSavingsLinehaul.get(k).i;
                                jLast = route.lastCustomer() == sortedSavingsLinehaul.get(k).j;

                                // se primo customer di route corrisponde al saving corrente i e j non è stato visitato
                                if (iFirst && !jUsed && !currentSavingFlag && jCapacity) {
                                    // aggiunge il customer j in testa
                                    route.getRoute().add(0, sortedSavingsLinehaul.get(k).j);
                                    route.addDelivery(customers[sortedSavingsLinehaul.get(k).j].getDemand());

                                    usedCustomers.add(sortedSavingsLinehaul.get(k).j);

                                    currentSavingFlag = true;
                                } // se primo customer di route corrisponde al saving corrente j e i non è stato visitato
                                else if (jFirst && !iUsed && !currentSavingFlag && iCapacity) {
                                    // aggiunge il customer i in testa
                                    route.getRoute().add(0, sortedSavingsLinehaul.get(k).i);
                                    route.addDelivery(customers[sortedSavingsLinehaul.get(k).i].getDemand());

                                    usedCustomers.add(sortedSavingsLinehaul.get(k).i);

                                    currentSavingFlag = true;
                                } // se ultimo customer di route corrisponde al saving corrente i e j non è stato visitato
                                else if (iLast && !jUsed && !currentSavingFlag && jCapacity) {
                                    // aggiunge il customer j in coda
                                    route.getRoute().add(route.getRoute().size(), sortedSavingsLinehaul.get(k).j);
                                    route.addDelivery(customers[sortedSavingsLinehaul.get(k).j].getDemand());

                                    usedCustomers.add(sortedSavingsLinehaul.get(k).j);

                                    currentSavingFlag = true;
                                } // se ultimo customer di route corrisponde al saving corrente j e i non è stato visitato
                                else if (jLast && !iUsed && !currentSavingFlag && iCapacity) {
                                    // aggiunge il customer i in coda
                                    route.getRoute().add(route.getRoute().size(), sortedSavingsLinehaul.get(k).i);
                                    route.addDelivery(customers[sortedSavingsLinehaul.get(k).i].getDemand());

                                    usedCustomers.add(sortedSavingsLinehaul.get(k).i);

                                    currentSavingFlag = true;
                                }
                            } // se route è vuota, i e j non sono ancora stati usati
                            else if (route.getRoute().isEmpty() && !iUsed && !jUsed && !currentSavingFlag && ijCapacity) {
                                route.getRoute().add(sortedSavingsLinehaul.get(k).i);
                                route.getRoute().add(sortedSavingsLinehaul.get(k).j);
                                route.addDelivery(customers[sortedSavingsLinehaul.get(k).i].getDemand());
                                route.addDelivery(customers[sortedSavingsLinehaul.get(k).j].getDemand());

                                usedCustomers.add(sortedSavingsLinehaul.get(k).i);
                                usedCustomers.add(sortedSavingsLinehaul.get(k).j);

                                currentSavingFlag = true;
                            }

                            k++;
                        }
                    }
                }
            }
        }

        // BACKHAUL PARALLELO ERROR
        k = 0;  // indice per scorrimento sortedSavingsLinehaul
        currentSavingFlag = false;   // routesUnited per uscire da ciclo dato che le routesSequenziale vanno popolate in parallelo
        usedCustomers = new ArrayList<>();    // lista dei customer già utilizzati

        // se i savings correnti i o j sono primo o ultimo nella route corrente
        iFirst = false;
        jFirst = false;
        iLast = false;
        jLast = false;

        // un veicolo per ogni customer
        if (pickups.size() <= depot.numberOfVehicles()) {
            initializeRoutesBackhaul();
        } else {
            routesBackhaul.clear();
            // crea un numero di routesSequenziale pari al numero di veicoli
            for (int i = 0; i < depot.numberOfVehicles(); i++) {
                routesBackhaul.add(new Route());
            }

            // eseguire il ciclo fino a quando non sono stati visitati tutti i customers
            while (usedCustomers.size() < pickups.size()) {
                // scorre lista di route
                for (Route route : routesBackhaul) {
                    k = 0; // si riparte dall'inizio della lista dei saving
                    currentSavingFlag = false;

                    int indexRoute = routesBackhaul.indexOf(route);

                    // se il numero di customer restanti è minore o uguale al numero di route, allora assegna un customer ad ogni route
                    if (((pickups.size()) - (usedCustomers.size())) <= (depot.numberOfVehicles() - (indexRoute))) {
                        for (int i = 0; i < customers.length; i++) {
                            if ((!usedCustomers.contains(i)) && (customers[i].getSupply() != 0 && (depot.getMaxCapacity() >= (route.getPickup() + customers[i].getSupply())))) {
                                routesBackhaul.get(indexRoute).getRoute().add(i);
                                routesBackhaul.get(indexRoute).addPickup(customers[i].getSupply());
                                usedCustomers.add(i);

                                indexRoute++;
                            }
                        }
                        // altrimenti esegue l'algoritmo normale
                    } else {
                        // per ogni route scorre la lista di saving e aggiunge il primo utilizzabile
                        while (k < sortedSavingsBackhaul.size() && !currentSavingFlag) {
                            // se i savings correnti i o j sono già stati inseriti in una route
                            boolean iUsed = usedCustomers.contains(sortedSavingsBackhaul.get(k).i);
                            boolean jUsed = usedCustomers.contains(sortedSavingsBackhaul.get(k).j);

                            // se la richiesta è minore della capacità massima
                            boolean iCapacity = depot.getMaxCapacity() >= (route.getDelivery() + customers[sortedSavingsBackhaul.get(k).i].getSupply());
                            boolean jCapacity = depot.getMaxCapacity() >= (route.getDelivery() + customers[sortedSavingsBackhaul.get(k).j].getSupply());
                            boolean ijCapacity = depot.getMaxCapacity() >= (route.getDelivery() + customers[sortedSavingsBackhaul.get(k).i].getSupply() + customers[sortedSavingsBackhaul.get(k).j].getSupply());

                            // se la route non è vuota
                            if (!route.getRoute().isEmpty() && !currentSavingFlag) {
                                iFirst = route.firstCustomer() == sortedSavingsBackhaul.get(k).i;
                                jFirst = route.firstCustomer() == sortedSavingsBackhaul.get(k).j;
                                iLast = route.lastCustomer() == sortedSavingsBackhaul.get(k).i;
                                jLast = route.lastCustomer() == sortedSavingsBackhaul.get(k).j;

                                // se primo customer di route corrisponde al saving corrente i e j non è stato visitato
                                if (iFirst && !jUsed && !currentSavingFlag && jCapacity) {
                                    // aggiunge il customer j in testa
                                    route.getRoute().add(0, sortedSavingsBackhaul.get(k).j);
                                    route.addPickup(customers[sortedSavingsBackhaul.get(k).j].getSupply());

                                    usedCustomers.add(sortedSavingsBackhaul.get(k).j);

                                    currentSavingFlag = true;
                                } // se primo customer di route corrisponde al saving corrente j e i non è stato visitato
                                else if (jFirst && !iUsed && !currentSavingFlag && iCapacity) {
                                    // aggiunge il customer i in testa
                                    route.getRoute().add(0, sortedSavingsBackhaul.get(k).i);
                                    route.addPickup(customers[sortedSavingsBackhaul.get(k).i].getSupply());

                                    usedCustomers.add(sortedSavingsBackhaul.get(k).i);

                                    currentSavingFlag = true;
                                } // se ultimo customer di route corrisponde al saving corrente i e j non è stato visitato
                                else if (iLast && !jUsed && !currentSavingFlag && jCapacity) {
                                    // aggiunge il customer j in coda
                                    route.getRoute().add(route.getRoute().size(), sortedSavingsBackhaul.get(k).j);
                                    route.addPickup(customers[sortedSavingsBackhaul.get(k).j].getSupply());

                                    usedCustomers.add(sortedSavingsBackhaul.get(k).j);

                                    currentSavingFlag = true;
                                } // se ultimo customer di route corrisponde al saving corrente j e i non è stato visitato
                                else if (jLast && !iUsed && !currentSavingFlag && iCapacity) {
                                    // aggiunge il customer i in coda
                                    route.getRoute().add(route.getRoute().size(), sortedSavingsBackhaul.get(k).i);
                                    route.addPickup(customers[sortedSavingsBackhaul.get(k).i].getSupply());

                                    usedCustomers.add(sortedSavingsBackhaul.get(k).i);

                                    currentSavingFlag = true;
                                }
                            } // se route è vuota, i e j non sono ancora stati usati
                            else if (route.getRoute().isEmpty() && !iUsed && !jUsed && !currentSavingFlag && ijCapacity) {
                                route.getRoute().add(sortedSavingsBackhaul.get(k).i);
                                route.getRoute().add(sortedSavingsBackhaul.get(k).j);
                                route.addPickup(customers[sortedSavingsBackhaul.get(k).i].getSupply());
                                route.addPickup(customers[sortedSavingsBackhaul.get(k).j].getSupply());

                                usedCustomers.add(sortedSavingsBackhaul.get(k).i);
                                usedCustomers.add(sortedSavingsBackhaul.get(k).j);

                                currentSavingFlag = true;
                            }

                            k++;
                        }
                    }
                }
            }
        }
//        // stampa di controllo BACKHAUL
//        x = 0;
//        System.out.print("\nBackhaul:");
//        for (Route route : routesBackhaul) {
//            System.out.print("\nroute " + x + ": ");
//            for (Integer rotta : route.getRoute()) {
//                System.out.print(rotta + 1 + " - ");
//            }
//            x++;
//        }
        // stampa di controllo BACKHAUL

        // MERGE TRA LINEHAUL E BACKHAUL
        // non completo perché ERROR (vedi file A2.txt)
    }
     */
}
