package ro_gruppoe_project;

/**
 * Occorrenza dei savings
 */
public class SavingOccurrence implements Comparable<SavingOccurrence> {

    int i;  // riga
    int j;  // colonna
    double s;   // saving
    boolean c;    // checked: true se il saving è stato utilizzato

    /**
     * Costruttore
     *
     * @param i Riga
     * @param j Colonna
     * @param s Saving
     */
    public SavingOccurrence(int i, int j, double s) {
        this.i = i;
        this.j = j;
        this.s = s;
        this.c = false;
    }

    @Override
    /**
     * Comparazione tra savings per ordinamento decrescente
     */
    public int compareTo(SavingOccurrence so) {
        // descending order
        double ris = so.s - this.s;

        if (ris > 0) {
            return 1;
        } else if (ris < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
