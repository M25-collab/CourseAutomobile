public class Voiture {
    private String id;
    private String nom;
    private String marque;
    private double vitesse; 

    public Voiture(String id, String nom, String marque, double vitesse) {
        if (vitesse <= 0) {
            throw new IllegalArgumentException("La vitesse doit être strictement supérieure à 0.");
        }
        this.id = id;
        this.nom = nom;
        this.marque = marque;
        this.vitesse = vitesse;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getMarque() { return marque; }
    public void setMarque(String marque) { this.marque = marque; }

    public double getVitesse() { return vitesse; }
    public void setVitesse(double vitesse) {
        if (vitesse <= 0) {
            throw new IllegalArgumentException("La vitesse doit être strictement supérieure à 0.");
        }
        this.vitesse = vitesse;
    }

    
    @Override
    public String toString() {
        return "Voiture [ID=" + id + ", Nom=" + nom + ", Marque=" + marque + ", Vitesse=" + vitesse + " km/tour]";
    }
}