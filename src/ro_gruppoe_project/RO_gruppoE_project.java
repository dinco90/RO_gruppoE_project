/*
 * Progetto: 
 * Ricerca Operativa
 * Corso di Informatica
 * Facoltà di Scienze
 * Università degli Studi di Cagliari
 */
package ro_gruppoe_project;

/**
 *
 * @author Dennis, Claudia
 */
public class RO_gruppoE_project {

    public static void main(String[] args) {

        Manager manager = new Manager();
        long start;
        long end;
        long lastMethodTime;

        // selezione e lettura del file
        manager.selectFile();
        manager.readFile();

        // Linehaul: crea la tabella delle distanze, crea la tabella dei savings, ordina i savings
        manager.createTableDistance();
        manager.createTableSavings();
        manager.setSortedSavingsLinehaul();
        manager.setSortedSavingsBackhaul();

        // SEQUENZIALE
        // inizializzazione routes
        manager.initializeRoutesLinehaul();
        manager.initializeRoutesBackhaul();
        // start time
        start = System.currentTimeMillis();
        // chiamata alla funzione sequenziale
        manager.algoritmoClarkeWrightSequenziale();
        // end time
        end = System.currentTimeMillis();
        lastMethodTime = end - start;
        //calcolo dei costi per l'algoritmo sequenziale
        manager.calculateCost();
        // salvataggio risultati su file
        manager.writeFile("Sequential");
        System.out.println("\nFile 'Solution Sequential " + manager.getNameFile() + "' written.");
        System.out.println("Algoritmo sequenziale eseguito in " + lastMethodTime + " ms.");

        //copia delle routes prima di chiamare l'algoritmo parallelo
        manager.copyRoutes();

        // PARALLELO
        // inizializzazione routes
        manager.initializeRoutesLinehaul();
        manager.initializeRoutesBackhaul();
        // start time
        start = System.currentTimeMillis();
        // chiamata alla funzione parallelo
        manager.algoritmoClarkeWrightParallelo();
        // end time
        end = System.currentTimeMillis();
        lastMethodTime = end - start;
        //calcolo dei costi per l'algoritmo parallelo
        manager.calculateCost();
        // salvataggio risultati su file
        manager.writeFile("Parallel");
        System.out.println("\nFile 'Solution Parallel " + manager.getNameFile() + "' written.");
        System.out.println("Algoritmo sequenziale eseguito in " + lastMethodTime + " ms.");

        //copia delle routes prima di chiamare l'algoritmo parallelo
        manager.copyRoutes();

        // PARALLEL ERROR
//        manager.algoritmoClarkeWrightParalleloERROR();
//        //calcolo dei costi per l'algoritmo parallelo
//        manager.calculateCost();
//        //salvataggio risultati su file
//        manager.writeFile("Parallel Error");
//        System.out.println("\nFile 'Solution Parallel Error " + manager.getNameFile() + "' written.\n");
    }
}
