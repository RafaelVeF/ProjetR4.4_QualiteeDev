//Import des dependances
import gestionincidents.UtilisateurService;
import gestionincidents.dao.TicketDaoMemory;
import gestionincidents.dao.UtilisateurDaoMemory;
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

        //jeu de donneés pour apercu
        Utilisateur user1 = utilisateurService.creerUtilisateur("Noa Audegond", "noa@iut.fr");
        Utilisateur user2 = utilisateurService.creerUtilisateur("Alice Martin", "alice@iut.fr");
        incidentService.creerTicket("Panne Serveur", "Le serveur de l'amphi A ne répond plus.", user1,LocalDate.of(2026, 5, 28),"Salle 102");
        Ticket t2 = incidentService.creerTicket("Imprimante bloquée", "Bourrage papier",user2, LocalDate.now(),"Secretariat");

        incidentService.modifierTicket(t2.getId(), t2.getTitre(), t2.getDescription(), Statut.EN_COURS, t2.getLocation());

        SwingUtilities.invokeLater(() -> {
            InterfaceAcceuil acceuil = new InterfaceAcceuil(incidentService, utilisateurService);
            acceuil.setVisible(true); // On rend la fenêtre visible
        });
    }
}