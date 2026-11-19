import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassementPartage {
    private final List<String> ordreArrivee = new ArrayList<>();
    private final Set<String> voituresDejaEnregistrees = new HashSet<>();

    public synchronized boolean enregistrerArrivee(Voiture voiture) {
        if (voituresDejaEnregistrees.contains(voiture.getId())) {
            return false;
        }
        voituresDejaEnregistrees.add(voiture.getId());
        ordreArrivee.add(voiture.getNom() + " (" + voiture.getMarque() + ")");
        return true;
    }

    public synchronized List<String> getClassement() {
        return Collections.unmodifiableList(new ArrayList<>(ordreArrivee));
    }

    public synchronized void afficherClassement() {
        System.out.println("\n======= CLASSEMENT CONCURRENT =======");
        for (int i = 0; i < ordreArrivee.size(); i++) {
            System.out.println((i + 1) + ". " + ordreArrivee.get(i));
        }
    }
}