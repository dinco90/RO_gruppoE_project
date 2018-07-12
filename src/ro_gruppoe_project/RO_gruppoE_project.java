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
        
        long executionTime = 0;

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
        // chiamata alla funzione sequenziale
        executionTime = manager.algoritmoClarkeWrightSequenziale();
        //calcolo dei costi per l'algoritmo sequenziale
        manager.calculateCost();
        // salvataggio risultati su file
        manager.writeFile("Sequential");
        System.out.println("\nFile 'Solution Sequential " + manager.getNameFile() + "' written.");
        System.out.println("Algoritmo sequenziale eseguito in " + executionTime + " ms.");

        //copia delle routes nell'apposito ArrayList
        manager.copyRoutes(true);

        // PARALLELO
        // inizializzazione routes
        manager.initializeRoutesLinehaul();
        manager.initializeRoutesBackhaul();
        // chiamata alla funzione parallelo
        executionTime = manager.algoritmoClarkeWrightParallelo();
        //calcolo dei costi per l'algoritmo parallelo
        manager.calculateCost();
        // salvataggio risultati su file
        manager.writeFile("Parallel");
        System.out.println("\nFile 'Solution Parallel " + manager.getNameFile() + "' written.");
        System.out.println("Algoritmo sequenziale eseguito in " + executionTime + " ms.");

        //copia delle routes nell'apposito ArrayList
        manager.copyRoutes(false);

        // PARALLEL ERROR
//        manager.algoritmoClarkeWrightParalleloERROR();
//        //calcolo dei costi per l'algoritmo parallelo
//        manager.calculateCost();
//        //salvataggio risultati su file
//        manager.writeFile("Parallel Error");
//        System.out.println("\nFile 'Solution Parallel Error " + manager.getNameFile() + "' written.\n");
    }
}
