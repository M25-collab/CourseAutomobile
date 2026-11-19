public class VoitureRunnable implements Runnable {
    private final Voiture voiture;
    private final double distanceTotale;
    private final long delaiMs;
    private final ClassementPartage classement;
    private double distanceParcourue;

    public VoitureRunnable(Voiture voiture, double distanceTotale, long delaiMs, ClassementPartage classement) {
        this.voiture = voiture;
        this.distanceTotale = distanceTotale;
        this.delaiMs = delaiMs;
        this.classement = classement;
        this.distanceParcourue = 0.0;
    }

    @Override
    public void run() {
        while (distanceParcourue < distanceTotale) {
            distanceParcourue += voiture.getVitesse();
            if (distanceParcourue >= distanceTotale) {
                distanceParcourue = distanceTotale;
            }

            System.out.println("[" + voiture.getNom() + "] " + distanceParcourue + " / " + distanceTotale + " km");

            if (distanceParcourue >= distanceTotale) {
                System.out.println("> " + voiture.getNom() + " franchit la ligne d'arrivée !");
                classement.enregistrerArrivee(voiture);
                break;
            }

            try {
                Thread.sleep(delaiMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}