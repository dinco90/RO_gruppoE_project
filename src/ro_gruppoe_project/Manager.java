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
            FileWriter writer = new FileWriter("output/Solution " + algorithm + " " + nameFile);
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
        ArrayList<Integer> usedCustomers = new ArrayList<>();    //lista di customer inseriti nelle route
        int currentFirst = 0; //primo customer della route che si sta popolando
        int currentLast = 0;  //ultimo customer della route che si sta popolando
        int k = 0;

        //variabili d'appoggio per le condizioni
        int routeI;
        int routeJ;
        boolean iFirst;
        boolean iLast;
        boolean jFirst;
        boolean jLast;

        //LINEHAUL
        while (usedCustomers.size() < deliveries.size() && routesLinehaul.size() > depot.numberOfVehicles()) {
            //si identificano i primi first e last  della route da creare
            for (SavingOccurrence occurrence : sortedSavingsLinehaul) {
                if (!usedCustomers.contains(occurrence.i) && !usedCustomers.contains(occurrence.j)) {
                    routeI = findRoute(occurrence.i, true);
                    routeJ = findRoute(occurrence.j, true);

                    System.out.println(routeI + " - " + routeJ);

                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));
                    routesLinehaul.remove(routeJ);

                    usedCustomers.add(occurrence.i);
                    usedCustomers.add(occurrence.j);

                    currentFirst = occurrence.i;
                    currentLast = occurrence.j;

                    break;  //uscita forzata perché viene identificata la prima coppia che non è stata ancora inserita nelle route
                }
            }

            // scorre la tabella dei savings
            for (k = 0; k < sortedSavingsLinehaul.size(); k++) {
                SavingOccurrence occurrence = sortedSavingsLinehaul.get(k);
                boolean cond1 = occurrence.i == currentFirst;
                boolean cond2 = occurrence.i == currentLast;
                boolean cond3 = occurrence.j == currentFirst;
                boolean cond4 = occurrence.j == currentLast;
                boolean cond5 = usedCustomers.contains(occurrence.i);
                boolean cond6 = usedCustomers.contains(occurrence.j);

                if ((((cond1) != (cond2))
                        != ((cond3) != (cond4)))
                        && (cond5 != cond6)) {

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
                        //si possono unire le due route
                        if (iLast && jFirst) {
                            //i è last, j è first

                            //unisci j ad i ed elimina  poi j
                            routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));

                            currentFirst = routesLinehaul.get(routeI).firstCustomer();
                            currentLast = routesLinehaul.get(routeI).lastCustomer();

                            routesLinehaul.remove(routeJ);
                        } else {
                            if (jLast && iFirst) {
                                //j è last, i è first

                                //unisci i ad j ed elimina  poi i
                                routesLinehaul.get(routeJ).merge(routesLinehaul.get(routeI));

                                currentFirst = routesLinehaul.get(routeJ).firstCustomer();
                                currentLast = routesLinehaul.get(routeJ).lastCustomer();

                                routesLinehaul.remove(routeI);
                            } else {
                                if ((iLast && jLast) || (iFirst && jFirst)) {
                                    // si effettua il reverse di una delle due route
                                    routesLinehaul.get(routeJ).reverse();

                                    //unisci j invertito ad i ed elimina  poi j
                                    routesLinehaul.get(routeI).merge(routesLinehaul.get(routeJ));

                                    currentFirst = routesLinehaul.get(routeI).firstCustomer();
                                    currentLast = routesLinehaul.get(routeI).lastCustomer();

                                    routesLinehaul.remove(routeJ);
                                }
                            }
                        }
                        //si aggiunge il nuovo customer alla lista di quelli già presenti nelle route
                        if (usedCustomers.contains(occurrence.i)) {
                            usedCustomers.add(occurrence.j);
                        } else {
                            usedCustomers.add(occurrence.i);
                        }
                        k = 0;    //riparte dal saving maggiore
                    }
                }
            }
        }

        //BACKHAUL
        usedCustomers.clear();

        while (usedCustomers.size() < pickups.size() && routesBackhaul.size() > depot.numberOfVehicles()) {
            //si identificano i primi first e last  della route da creare
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

                    break;  //uscita forzata perché viene identificata la prima coppia che non è stata ancora inserita nelle route
                }
            }

            // scorre la tabella dei savings
            for (k = 0; k < sortedSavingsBackhaul.size(); k++) {
                SavingOccurrence occurrence = sortedSavingsBackhaul.get(k);
                boolean cond1 = occurrence.i == currentFirst;
                boolean cond2 = occurrence.i == currentLast;
                boolean cond3 = occurrence.j == currentFirst;
                boolean cond4 = occurrence.j == currentLast;
                boolean cond5 = usedCustomers.contains(occurrence.i);
                boolean cond6 = usedCustomers.contains(occurrence.j);

                if ((((cond1) != (cond2))
                        != ((cond3) != (cond4)))
                        && (cond5 != cond6)) {

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
                        //si possono unire le due route
                        if (iLast && jFirst) {
                            //i è last, j è first

                            //unisci j ad i ed elimina  poi j
                            routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));

                            currentFirst = routesBackhaul.get(routeI).firstCustomer();
                            currentLast = routesBackhaul.get(routeI).lastCustomer();

                            routesBackhaul.remove(routeJ);
                        } else {
                            if (jLast && iFirst) {
                                //j è last, i è first

                                //unisci i ad j ed elimina  poi i
                                routesBackhaul.get(routeJ).merge(routesBackhaul.get(routeI));

                                currentFirst = routesBackhaul.get(routeJ).firstCustomer();
                                currentLast = routesBackhaul.get(routeJ).lastCustomer();

                                routesBackhaul.remove(routeI);
                            } else {
                                if ((iLast && jLast) || (iFirst && jFirst)) {
                                    // si effettua il reverse di una delle due route
                                    routesBackhaul.get(routeJ).reverse();

                                    //unisci j invertito ad i ed elimina  poi j
                                    routesBackhaul.get(routeI).merge(routesBackhaul.get(routeJ));

                                    currentFirst = routesBackhaul.get(routeI).firstCustomer();
                                    currentLast = routesBackhaul.get(routeI).lastCustomer();

                                    routesBackhaul.remove(routeJ);
                                }
                            }
                        }
                        //si aggiunge il nuovo customer alla lista di quelli già presenti nelle route
                        if (usedCustomers.contains(occurrence.i)) {
                            usedCustomers.add(occurrence.j);
                        } else {
                            usedCustomers.add(occurrence.i);
                        }
                        k = 0;    //riparte dal saving maggiore
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
                    cost += tableDistances[listCustomer.get(i) + 1][listCustomer.get(i + 1) + 1];
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
        int count = 0;    //contatore per le routebackhaul unite a quelle linehaul
        int k = 0;

        //variabili d'appoggio per le condizioni
        int routeI;
        int routeJ;
        boolean iFirst;
        boolean iLast;
        boolean jFirst;
        boolean jLast;

        routesLinehaul.addAll(routesBackhaul);

        while (count < routesBackhaul.size() && routesLinehaul.size() > depot.numberOfVehicles()) {

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

                    count++;    //nuova route backhaul unita
                    break;  //uscita forzata quando si riesce ad unire due route
                }
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
     * Esegue l'algoritmo Clarke & Wright in modo parallelo
     *
     * ERROR (vedi file A2.txt)
     */
    public void algoritmoClarkeWrightParalleloERROR() {
        int k = 0;  // indice per scorrimento sortedSavingsLinehaul
        boolean currentSavingFlag = false;   // flag per uscire da ciclo dato che le routes vanno popolate in parallelo
        ArrayList<Integer> usedCustomers = new ArrayList<>();    // lista dei customer già utilizzati

        // se i savings correnti i o j sono primo o ultimo nella route corrente
        boolean iFirst = false;
        boolean jFirst = false;
        boolean iLast = false;
        boolean jLast = false;

        // LINEHAUL
        // un veicolo per ogni customer
        if (deliveries.size() <= depot.numberOfVehicles()) {
            initializeRoutesLinehaul();
        } else {
            routesLinehaul.clear();
            // crea un numero di routes pari al numero di veicoli
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
        // stampa di controllo LINEHAUL
        int x = 0;
        System.out.print("\nLinehaul:");
        for (Route route : routesLinehaul) {
            System.out.print("\nroute " + x + ": ");
            for (Integer rotta : route.getRoute()) {
                System.out.print(rotta + 1 + " - ");
            }
            x++;
        }
        // stampa di controllo LINEHAUL

        // BACKHAUL
        k = 0;  // indice per scorrimento sortedSavingsLinehaul
        currentSavingFlag = false;   // flag per uscire da ciclo dato che le routes vanno popolate in parallelo
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
            // crea un numero di routes pari al numero di veicoli
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
        // stampa di controllo BACKHAUL
        x = 0;
        System.out.print("\nBackhaul:");
        for (Route route : routesBackhaul) {
            System.out.print("\nroute " + x + ": ");
            for (Integer rotta : route.getRoute()) {
                System.out.print(rotta + 1 + " - ");
            }
            x++;
        }
        // stampa di controllo BACKHAUL

        // MERGE TRA LINEHAUL E BACKHAUL
        // non completo perché ERROR (vedi file A2.txt)
    }

    /**
     * Esegue l'algoritmo Clarke & Wright in modo parallelo
     *
     */
    public void algoritmoClarkeWrightParallelo() {
        ArrayList<Integer> usedCustomers = new ArrayList<>();    // lista dei customer già utilizzati
        ArrayList<Integer> usedCustomersParallel = new ArrayList<>();   // lista degli ultimi customer usati (per tenere conto di quali routes non modficare e rendere l'algoritmo parallelo)
        int counterSavings = 0; // contatore dei savings utilizzati

        // routes dei savings correnti
        int routeI;
        int routeJ;
        // se i savings correnti i o j sono primo o ultimo nella route corrente
        boolean iFirst = false;
        boolean jFirst = false;
        boolean iLast = false;
        boolean jLast = false;

        // finchè non sono stati tutti i customer e si ha il numero di routes richiesto
        while (usedCustomers.size() < deliveries.size() && routesLinehaul.size() > depot.numberOfVehicles()) {

            for (SavingOccurrence saving : sortedSavingsLinehaul) {
                routeI = findRoute(saving.i, true);
                routeJ = findRoute(saving.j, true);
                iFirst = routesLinehaul.get(saving.i).firstCustomer() == saving.i;
                jFirst = routesLinehaul.get(saving.j).firstCustomer() == saving.i;
                iLast = routesLinehaul.get(saving.i).lastCustomer() == saving.i;
                jLast = routesLinehaul.get(saving.j).lastCustomer() == saving.i;

                // se il numero dei customers usati corrisponde a quello del numero delle routes richiesto, allora azzera l'ArrayList
                if (counterSavings == depot.numberOfVehicles()) {
                    usedCustomersParallel.clear();
                }

                // se customer i o j sono già stati usati nel turno corrente (non si può usare tale route)
                if (usedCustomersParallel.contains(saving.i) || usedCustomersParallel.contains(saving.j)) {
                    // salta saving corrente
                } // se il customer j può essere collegato alla route contenente il customer i
                else if (usedCustomers.contains(saving.i) && !usedCustomers.contains(saving.j)) {

                    usedCustomers.add(saving.j);
                    counterSavings++;
                } // se il customer i può essere collegato alla route contenente il customer j
                else if (usedCustomers.contains(saving.j) && !usedCustomers.contains(saving.i)) {

                    usedCustomers.add(saving.i);
                    counterSavings++;
                } // se nessuno dei due customer è stato utilizzato
                else if (!usedCustomers.contains(saving.i) && !usedCustomers.contains(saving.j)) {

                    usedCustomers.add(saving.i);
                    usedCustomers.add(saving.j);
                    counterSavings++;
                }

                // se i savings correnti i o j sono primo o ultimo nella route corrente
//                iFirst = route.firstCustomer() == sortedSavingsLinehaul.get(k).i;
//                jFirst = route.firstCustomer() == sortedSavingsLinehaul.get(k).j;
//                iLast = route.lastCustomer() == sortedSavingsLinehaul.get(k).i;
//                jLast = route.lastCustomer() == sortedSavingsLinehaul.get(k).j;
                // se customer i del saving corrente non è stato usato
                // se una route è stata utilizzata nel turno corrente
//                if ((!usedCustomers.contains(saving.i)) && (!usedCustomersParallel.contains(saving.i))) {
//                    ;
//                }
            }
        }
    }
}
