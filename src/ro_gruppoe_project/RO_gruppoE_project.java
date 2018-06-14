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
        manager.algoritmoClarkeWrightSequenziale();
        //calcolo dei costi per l'algoritmo sequenziale
        manager.calculateCost();
        // salvataggio risultati su file
        manager.writeFile("Sequential");
        System.out.println("File 'Solution Sequential A1.txt' written.");

        //copia delle routes prima di chiamare l'algoritmo parallelo
        manager.copyRoutes();

        // PARALLELO
        // inizializzazione routes
        manager.initializeRoutesLinehaul();
        manager.initializeRoutesBackhaul();
        // chiamata alla funzione parallelo
        manager.algoritmoClarkeWrightParallelo();
        //calcolo dei costi per l'algoritmo parallelo
        manager.calculateCost();
        // salvataggio risultati su file
        manager.writeFile("Parallel");
        System.out.println("File 'Solution Parallel A1.txt' written.");
        
        //copia delle routes prima di chiamare l'algoritmo parallelo
        //manager.copyRoutes();

        // PARALLEL ERROR
        manager.algoritmoClarkeWrightParalleloERROR();
        //calcolo dei costi per l'algoritmo parallelo
        manager.calculateCost();
        //salvataggio risultati su file
        manager.writeFile("Parallel Error");
        System.out.println("File 'Solution Parallel Error A1.txt' written.");
    }
}
