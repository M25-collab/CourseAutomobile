import java.util.ArrayList;
import java.util.List;

public class Course {
    private String id;
    private String nom;
    private double distanceTotale;
    private List<Voiture> voituresParticipants;
    private boolean terminee;

    public Course(String id, String nom, double distanceTotale) {
        if (distanceTotale <= 0) {
            throw new IllegalArgumentException("La distance de la course doit être strictement supérieure à 0.");
        }
        this.id = id;
        this.nom = nom;
        this.distanceTotale = distanceTotale;
        this.voituresParticipants = new ArrayList<>();
        this.terminee = false;
    }

    public void inscrireVoiture(Voiture voiture) {
        if (terminee) {
            throw new IllegalStateException("Impossible d'inscrire des voitures à une course déjà terminée.");
        }
        if (voituresParticipants.stream().anyMatch(v -> v.getId().equals(voiture.getId()))) {
            throw new IllegalArgumentException("Cette voiture est déjà inscrite à la course.");
        }
        voituresParticipants.add(voiture);
    }

    public boolean estValide() {
        return voituresParticipants.size() >= 2 && distanceTotale > 0;
    }

    
    public String getId() { return id; }
    public String getNom() { return nom; }
    public double getDistanceTotale() { return distanceTotale; }
    public List<Voiture> getVoituresParticipants() { return voituresParticipants; }
    public boolean isTerminee() { return terminee; }
    public void setTerminee(boolean terminee) { this.terminee = terminee; }
}