import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Simulation simulation = new Simulation();

        // Mode de lancement automatique sans saisie utilisateur (exigé pour Jenkins/Docker)
        if (args.length > 0 && args[0].equalsIgnoreCase("auto")) {
            lancerModeAutomatique(simulation);
            return;
        }

        // Mode interactif en console
        Scanner scanner = new Scanner(System.in);
        boolean quitter = false;

        while (!quitter) {
            afficherMenu();
            System.out.print("Votre choix : ");
            String saisie = scanner.nextLine().trim();

            try {
                int choix = Integer.parseInt(saisie);
                switch (choix) {
                    case 1:
                        ajouterVoitureUI(scanner, simulation);
                        break;
                    case 2:
                        afficherVoituresUI(simulation);
                        break;
                    case 3:
                        rechercherVoitureUI(scanner, simulation);
                        break;
                    case 4:
                        modifierVoitureUI(scanner, simulation);
                        break;
                    case 5:
                        supprimerVoitureUI(scanner, simulation);
                        break;
                    case 6:
                        configurerCourseUI(scanner, simulation);
                        break;
                    case 7:
                        simulation.lancerSimulation();
                        break;
                    case 8:
                        consulterResultatsUI(simulation);
                        break;
                    case 0:
                        quitter = true;
                        System.out.println("Fermeture de l'application.");
                        break;
                    default:
                        System.out.println("Erreur : Choix invalide. Veuillez saisir un chiffre entre 0 et 8.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Erreur : Saisie non numérique. Veuillez entrer un nombre valide.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erreur : " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void afficherMenu() {
        System.out.println("\n==================================");
        System.out.println("  GESTION DE COURSES AUTOMOBILES  ");
        System.out.println("==================================");
        System.out.println("1. Ajouter une voiture");
        System.out.println("2. Afficher les voitures");
        System.out.println("3. Rechercher une voiture");
        System.out.println("4. Modifier une voiture");
        System.out.println("5. Supprimer une voiture");
        System.out.println("6. Créer et configurer une course");
        System.out.println("7. Démarrer une course");
        System.out.println("8. Consulter les résultats");
        System.out.println("0. Quitter");
        System.out.println("==================================");
    }

    private static void ajouterVoitureUI(Scanner scanner, Simulation simulation) {
        System.out.print("Identifiant de la voiture : ");
        String id = scanner.nextLine().trim();
        System.out.print("Nom de la voiture : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Marque : ");
        String marque = scanner.nextLine().trim();
        System.out.print("Vitesse (en km/tour) : ");
        double vitesse = Double.parseDouble(scanner.nextLine().trim());

        Voiture v = new Voiture(id, nom, marque, vitesse);
        simulation.ajouterVoiture(v);
        System.out.println("Voiture ajoutée avec succès !");
    }

    private static void afficherVoituresUI(Simulation simulation) {
        List<Voiture> liste = simulation.getVoituresDisponibles();
        if (liste.isEmpty()) {
            System.out.println("Aucune voiture enregistrée.");
            return;
        }
        System.out.println("\n--- Liste des voitures enregistrées ---");
        for (Voiture v : liste) {
            System.out.println(v);
        }
    }

    private static void rechercherVoitureUI(Scanner scanner, Simulation simulation) {
        System.out.print("Entrez l'identifiant de la voiture recherchée : ");
        String id = scanner.nextLine().trim();
        Voiture v = simulation.rechercherVoiture(id);
        if (v != null) {
            System.out.println("Voiture trouvée : " + v);
        } else {
            System.out.println("Erreur : Aucune voiture trouvée avec l'identifiant '" + id + "'.");
        }
    }

    private static void modifierVoitureUI(Scanner scanner, Simulation simulation) {
        System.out.print("Identifiant de la voiture à modifier : ");
        String id = scanner.nextLine().trim();
        Voiture v = simulation.rechercherVoiture(id);
        if (v == null) {
            System.out.println("Erreur : Voiture introuvable.");
            return;
        }
        // Vérification si la voiture est dans une course non terminée
        Course c = simulation.getCourseActuelle();
        if (c != null && !c.isTerminee() && c.getVoituresParticipants().contains(v)) {
            throw new IllegalStateException("Impossible de modifier une voiture inscrite à une course en cours.");
        }

        System.out.print("Nouveau nom (actuel: " + v.getNom() + ") : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Nouvelle marque (actuelle: " + v.getMarque() + ") : ");
        String marque = scanner.nextLine().trim();
        System.out.print("Nouvelle vitesse (actuelle: " + v.getVitesse() + ") : ");
        double vitesse = Double.parseDouble(scanner.nextLine().trim());

        v.setVitesse(vitesse); // Valide que vitesse > 0 avant de modifier le reste
        v.setNom(nom);
        v.setMarque(marque);
        System.out.println("Voiture modifiée avec succès !");
    }

    private static void supprimerVoitureUI(Scanner scanner, Simulation simulation) {
        System.out.print("Identifiant de la voiture à supprimer : ");
        String id = scanner.nextLine().trim();
        if (simulation.rechercherVoiture(id) == null) {
            System.out.println("Erreur : Voiture introuvable.");
            return;
        }
        simulation.supprimerVoiture(id);
        System.out.println("Voiture supprimée avec succès !");
    }

    private static void configurerCourseUI(Scanner scanner, Simulation simulation) {
        if (simulation.getVoituresDisponibles().size() < 2) {
            throw new IllegalStateException("Il faut au moins 2 voitures enregistrées dans le système pour créer une course.");
        }
        System.out.print("Identifiant de la course : ");
        String id = scanner.nextLine().trim();
        System.out.print("Nom de la course : ");
        String nom = scanner.nextLine().trim();
        System.out.print("Distance totale (en km) : ");
        double distance = Double.parseDouble(scanner.nextLine().trim());

        afficherVoituresUI(simulation);
        System.out.println("Saisissez les identifiants des voitures participantes séparés par des virgules (ex: V1,V2,V3) :");
        String[] ids = scanner.nextLine().trim().split(",");
        List<String> listeIds = new ArrayList<>();
        for (String vid : ids) {
            listeIds.add(vid.trim());
        }

        simulation.configurerCourse(id, nom, distance, listeIds);
        System.out.println("Course configurée avec succès ! (" + simulation.getCourseActuelle().getVoituresParticipants().size() + " participants)");
    }

    private static void consulterResultatsUI(Simulation simulation) {
        List<Simulation.ResultatVoiture> resultats = simulation.getDerniersResultats();
        if (resultats.isEmpty()) {
            System.out.println("Aucun résultat disponible. Veuillez d'abord lancer une course.");
            return;
        }
        System.out.println("\n=== RÉSULTATS DE LA DERNIÈRE COURSE ===");
        int rang = 1;
        for (Simulation.ResultatVoiture res : resultats) {
            System.out.println(rang + ". " + res.voiture.getNom() + " (ID: " + res.voiture.getId() + 
                               ") - Distance: " + res.distanceParcourue + " km - Tours: " + res.toursNecessaires);
            rang++;
        }
    }

    private static void lancerModeAutomatique(Simulation simulation) {
        System.out.println("=== LANCEMENT EN MODE AUTOMATIQUE ===");
        simulation.ajouterVoiture(new Voiture("V1", "SF-24", "Ferrari", 25.0));
        simulation.ajouterVoiture(new Voiture("V2", "M4 GT3", "BMW", 20.0));
        simulation.ajouterVoiture(new Voiture("V3", "RS01", "Renault", 15.0));

        List<String> participants = List.of("V1", "V2", "V3");
        simulation.configurerCourse("C1", "Grand Prix Automatique", 100.0, participants);
        simulation.lancerSimulation();
    }
}