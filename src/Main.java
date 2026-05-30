import modele.Role;
import modele.Statut;
import modele.Ticket;
import modele.Utilisateur;
import service.MockIncidentService;
import service.MockUtilisateurService;
//swing
import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static boolean modeTest = false;
    public static void main(String[] args) {

        MockIncidentService MockIncidentService = new MockIncidentService();
        MockUtilisateurService MockUtilisateurService = new MockUtilisateurService();

        Utilisateur u0 = MockUtilisateurService.creerUtilisateur("Jean Lemerc", "jeanlem@iut.fr",Role.TECHNICIEN);
        Utilisateur u1 = MockUtilisateurService.creerUtilisateur("Noa Audegond", "noaaud@iut.fr", Role.CLASSIQUE);
        Utilisateur u2 = MockUtilisateurService.creerUtilisateur("Alice Martin", "alicemartin@iut.fr",Role.TECHNICIEN);
        Utilisateur u3 = MockUtilisateurService.creerUtilisateur("Marc Dupont", "marcdupont@iut.fr",Role.CLASSIQUE);
        Utilisateur u4 = MockUtilisateurService.creerUtilisateur("Sophie Leroux", "sophieleroux@iut.fr",Role.TECHNICIEN);

        Utilisateur utilisateurConnecte = u0;


        MockIncidentService.creerTicket("Panne Serveur", "Le serveur de l'amphi A ne répond plus.", u1, LocalDate.of(2026, 5, 28), "Amphi A");
        MockIncidentService.creerTicket("Clavier défectueux", "Touche espace cassée.", u3, LocalDate.now(), "Salle 204");
        MockIncidentService.creerTicket("Logiciel introuvable", "Eclipse n'est pas installé sur le poste 12.", u4, LocalDate.now(), "Salle 102");

        Ticket t4 = MockIncidentService.creerTicket("Imprimante bloquée", "Bourrage papier", u2, LocalDate.of(2026, 5, 27), "Secrétariat");
        MockIncidentService.modifierTicket(t4.getId(), t4.getTitre(), t4.getDescription(), Statut.EN_COURS, t4.getLocation());
        Ticket t5 = MockIncidentService.creerTicket("Plus de Wi-Fi", "Réseau eduroam inaccessible.", u1, LocalDate.of(2026, 5, 26), "Cafétéria");
        MockIncidentService.modifierTicket(t5.getId(), t5.getTitre(), t5.getDescription(), Statut.EN_COURS, t5.getLocation());
        Ticket t6 = MockIncidentService.creerTicket("Câble réseau manquant", "Il manque un câble RJ45.", u3, LocalDate.now(), "Bureau des profs");
        MockIncidentService.modifierTicket(t6.getId(), t6.getTitre(), t6.getDescription(), Statut.EN_COURS, t6.getLocation());
        Ticket t7 = MockIncidentService.creerTicket("Mot de passe oublié", "Impossible de me connecter à mon compte.", u4, LocalDate.of(2026, 5, 20), "Bureau 3");
        MockIncidentService.modifierTicket(t7.getId(), t7.getTitre(), "Réinitialisation effectuée par l'admin.", Statut.RESOLU, t7.getLocation());
        Ticket t8 = MockIncidentService.creerTicket("Écran bleu", "Le PC redémarre en boucle.", u2, LocalDate.of(2026, 5, 22), "Salle 201");
        MockIncidentService.modifierTicket(t8.getId(), t8.getTitre(), "Mise à jour des pilotes graphiques.", Statut.RESOLU, t8.getLocation());
        Ticket t9 = MockIncidentService.creerTicket("Souris HS", "Le clic droit ne marche plus.", u1, LocalDate.of(2026, 5, 25), "Salle 102");
        MockIncidentService.modifierTicket(t9.getId(), t9.getTitre(), "Souris remplacée.", Statut.RESOLU, t9.getLocation());
        Ticket t10 = MockIncidentService.creerTicket("Fuite d'eau", "Il y a de l'eau près des PC.", u3, LocalDate.of(2026, 5, 28), "Couloir B");
        MockIncidentService.modifierTicket(t10.getId(), t10.getTitre(), "Erreur, c'était juste une bouteille renversée.", Statut.ANNULE, t10.getLocation());

        SwingUtilities.invokeLater(() -> {
            InterfaceAcceuil acceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, utilisateurConnecte);
            acceuil.setVisible(true);
        });
    }
}
