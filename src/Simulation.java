import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Simulation {
    private List<Voiture> voituresDisponibles;
    private Course courseActuelle;
    private List<ResultatVoiture> derniersResultats;

    public Simulation() {
        this.voituresDisponibles = new ArrayList<>();
        this.derniersResultats = new ArrayList<>();
    }

    public void ajouterVoiture(Voiture v) {
        // Vérifier l'unicité de l'identifiant
        if (voituresDisponibles.stream().anyMatch(voiture -> voiture.getId().equals(v.getId()))) {
            throw new IllegalArgumentException("Une voiture avec cet identifiant existe déjà.");
        }
        voituresDisponibles.add(v);
    }

    public Voiture rechercherVoiture(String id) {
        return voituresDisponibles.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void supprimerVoiture(String id) {
        Voiture v = rechercherVoiture(id);
        if (v != null) {
            // Vérifier si la voiture participe à une course non terminée
            if (courseActuelle != null && !courseActuelle.isTerminee() && 
                courseActuelle.getVoituresParticipants().contains(v)) {
                throw new IllegalStateException("Impossible de supprimer une voiture inscrite à une course en cours.");
            }
            voituresDisponibles.remove(v);
        }
    }

    public void configurerCourse(String id, String nom, double distance, List<String> idsParticipants) {
        Course nouvelleCourse = new Course(id, nom, distance);
        for (String vid : idsParticipants) {
            Voiture v = rechercherVoiture(vid);
            if (v != null) {
                nouvelleCourse.inscrireVoiture(v);
            }
        }
        if (!nouvelleCourse.estValide()) {
            throw new IllegalArgumentException("La course doit comporter au moins 2 voitures et une distance valide.");
        }
        this.courseActuelle = nouvelleCourse;
    }

    // Classe interne pour stocker l'état d'une voiture pendant la course
    public static class ResultatVoiture implements Comparable<ResultatVoiture> {
        public Voiture voiture;
        public double distanceParcourue;
        public int toursNecessaires;
        public double tempsTheorique;
        public boolean arrivee;

        public ResultatVoiture(Voiture voiture) {
            this.voiture = voiture;
            this.distanceParcourue = 0;
            this.toursNecessaires = 0;
            this.arrivee = false;
        }

        @Override
        public int compareTo(ResultatVoiture o) {
            // 1. Trier par nombre de tours croissant (le plus rapide en premier)
            if (this.toursNecessaires != o.toursNecessaires) {
                return Integer.compare(this.toursNecessaires, o.toursNecessaires);
            }
            // 2. En cas d'égalité de tours, départager par temps théorique (distance / vitesse)
            if (Double.compare(this.venantTempsTheorique(), o.venantTempsTheorique()) != 0) {
                return Double.compare(this.venantTempsTheorique(), o.venantTempsTheorique());
            }
            // 3. En dernier recours, départager par l'identifiant
            return this.voiture.getId().compareTo(o.voiture.getId());
        }

        public double venantTempsTheorique() {
            return tempsTheorique;
        }
    }

    public void lancerSimulation() {
        if (courseActuelle == null || !courseActuelle.estValide()) {
            throw new IllegalStateException("Aucune course valide configurée.");
        }

        System.out.println("\n=== DÉMARRAGE DE LA COURSE : " + courseActuelle.getNom() + " ===");
        System.out.println("Distance totale : " + courseActuelle.getDistanceTotale() + " km\n");

        List<ResultatVoiture> participants = new ArrayList<>();
        for (Voiture v : courseActuelle.getVoituresParticipants()) {
            participants.add(new ResultatVoiture(v));
        }

        int tour = 0;
        boolean courseFinie = false;

        while (!courseFinie) {
            tour++;
            System.out.println("--- Tour " + tour + " ---");
            courseFinie = true;

            for (ResultatVoiture res : participants) {
                if (!res.arrivee) {
                    res.distanceParcourue += res.voiture.getVitesse();
                    if (res.distanceParcourue >= courseActuelle.getDistanceTotale()) {
                        res.distanceParcourue = courseActuelle.getDistanceTotale(); // Plafonnement
                        res.arrivee = true;
                        res.toursNecessaires = tour;
                        res.tempsTheorique = courseActuelle.getDistanceTotale() / res.voiture.getVitesse();
                        System.out.println(" > " + res.voiture.getNom() + " a franchi la ligne d'arrivée !");
                    } else {
                        System.out.println(" - " + res.voiture.getNom() + " : " + res.distanceParcourue + " / " + courseActuelle.getDistanceTotale() + " km");
                    }
                }
                if (!res.arrivee) {
                    courseFinie = false; // S'il reste au moins une voiture en course
                }
            }
        }

        // Classement final
        Collections.sort(participants);
        this.derniersResultats = participants;
        courseActuelle.setTerminee(true);

        System.out.println("\n======= CLASSEMENT FINAL (Tour total : " + tour + ") =======");
        int rang = 1;
        for (ResultatVoiture res : participants) {
            System.out.println(rang + ". " + res.voiture.getNom() + " (Marque: " + res.voiture.getMarque() + ") - Tours: " + res.toursNecessaires);
            rang++;
        }
    }

    public List<Voiture> getVoituresDisponibles() { return voituresDisponibles; }
    public Course getCourseActuelle() { return courseActuelle; }
    public List<ResultatVoiture> getDerniersResultats() { return derniersResultats; }
}