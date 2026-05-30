//Import des dependances
import gestionincidents.UtilisateurService;
import gestionincidents.dao.TicketDaoMemory;
import gestionincidents.dao.UtilisateurDaoMemory;
import gestionincidents.model.Role;
import gestionincidents.model.Statut;
import gestionincidents.model.Ticket;
import gestionincidents.model.Utilisateur;
import gestionincidents.service.IncidentService;
//swing
import javax.swing.*;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        TicketDaoMemory ticketDao = new TicketDaoMemory();
        UtilisateurDaoMemory utilisateurDao = new UtilisateurDaoMemory();

        IncidentService incidentService = new IncidentService(ticketDao);
        UtilisateurService utilisateurService = new UtilisateurService(utilisateurDao);

        Utilisateur u0 = utilisateurService.creerUtilisateur("Jean Lemerc", "jeanlem@iut.fr",Role.TECHNICIEN);
        Utilisateur u1 = utilisateurService.creerUtilisateur("Noa Audegond", "noaaud@iut.fr", Role.CLASSIQUE);
        Utilisateur u2 = utilisateurService.creerUtilisateur("Alice Martin", "alicemartin@iut.fr",Role.TECHNICIEN);
        Utilisateur u3 = utilisateurService.creerUtilisateur("Marc Dupont", "marcdupont@iut.fr",Role.CLASSIQUE);
        Utilisateur u4 = utilisateurService.creerUtilisateur("Sophie Leroux", "sophieleroux@iut.fr",Role.TECHNICIEN);

        Utilisateur utilisateurConnecte = u0;


        incidentService.creerTicket("Panne Serveur", "Le serveur de l'amphi A ne répond plus.", u1, LocalDate.of(2026, 5, 28), "Amphi A");
        incidentService.creerTicket("Clavier défectueux", "Touche espace cassée.", u3, LocalDate.now(), "Salle 204");
        incidentService.creerTicket("Logiciel introuvable", "Eclipse n'est pas installé sur le poste 12.", u4, LocalDate.now(), "Salle 102");

        Ticket t4 = incidentService.creerTicket("Imprimante bloquée", "Bourrage papier", u2, LocalDate.of(2026, 5, 27), "Secrétariat");
        incidentService.modifierTicket(t4.getId(), t4.getTitre(), t4.getDescription(), Statut.EN_COURS, t4.getLocation());
        Ticket t5 = incidentService.creerTicket("Plus de Wi-Fi", "Réseau eduroam inaccessible.", u1, LocalDate.of(2026, 5, 26), "Cafétéria");
        incidentService.modifierTicket(t5.getId(), t5.getTitre(), t5.getDescription(), Statut.EN_COURS, t5.getLocation());
        Ticket t6 = incidentService.creerTicket("Câble réseau manquant", "Il manque un câble RJ45.", u3, LocalDate.now(), "Bureau des profs");
        incidentService.modifierTicket(t6.getId(), t6.getTitre(), t6.getDescription(), Statut.EN_COURS, t6.getLocation());
        Ticket t7 = incidentService.creerTicket("Mot de passe oublié", "Impossible de me connecter à mon compte.", u4, LocalDate.of(2026, 5, 20), "Bureau 3");
        incidentService.modifierTicket(t7.getId(), t7.getTitre(), "Réinitialisation effectuée par l'admin.", Statut.RESOLU, t7.getLocation());
        Ticket t8 = incidentService.creerTicket("Écran bleu", "Le PC redémarre en boucle.", u2, LocalDate.of(2026, 5, 22), "Salle 201");
        incidentService.modifierTicket(t8.getId(), t8.getTitre(), "Mise à jour des pilotes graphiques.", Statut.RESOLU, t8.getLocation());
        Ticket t9 = incidentService.creerTicket("Souris HS", "Le clic droit ne marche plus.", u1, LocalDate.of(2026, 5, 25), "Salle 102");
        incidentService.modifierTicket(t9.getId(), t9.getTitre(), "Souris remplacée.", Statut.RESOLU, t9.getLocation());
        Ticket t10 = incidentService.creerTicket("Fuite d'eau", "Il y a de l'eau près des PC.", u3, LocalDate.of(2026, 5, 28), "Couloir B");
        incidentService.modifierTicket(t10.getId(), t10.getTitre(), "Erreur, c'était juste une bouteille renversée.", Statut.ANNULE, t10.getLocation());

        SwingUtilities.invokeLater(() -> {
            InterfaceAcceuil acceuil = new InterfaceAcceuil(incidentService, utilisateurService, utilisateurConnecte);
            acceuil.setVisible(true);
        });
    }
}