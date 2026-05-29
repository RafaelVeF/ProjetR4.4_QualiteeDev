package gestionincidents.service;

import gestionincidents.dao.TicketDao;
import gestionincidents.model.Ticket;
import gestionincidents.model.Utilisateur;
import gestionincidents.model.Statut;

import java.time.LocalDate;
import java.util.List;

public class IncidentService {

    private final TicketDao ticketDao;

    public IncidentService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }

    //Creer un nouveau ticket
    public Ticket creerTicket(String titre, String description, Utilisateur createur,LocalDate date,String location) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du ticket ne peut pas être vide.");
        }
        if (createur == null) {
            throw new IllegalArgumentException("Un ticket doit obligatoirement avoir un créateur.");
        }
        if (date==null){
            throw new IllegalArgumentException("La date est requise");
        }

        // verif doublon
        Ticket ticketExistant = ticketDao.trouverParTitreEtCreateur(titre, createur.getId());
        if (ticketExistant != null && ticketExistant.getStatut() != Statut.RESOLU) {
            throw new IllegalStateException("Un ticket avec ce titre a déjà été déclaré par cet utilisateur.");
        }

        Ticket nouveauTicket = new Ticket(null, titre, description, createur, date, location);
        return ticketDao.sauvegarder(nouveauTicket);
    }

    public Ticket obtenirTicket(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'identifiant du ticket ne peut pas être nul.");
        }
        return ticketDao.trouverParId(id);
    }

    public List<Ticket> obtenirTousLesTickets() {
        return ticketDao.trouverTous();
    }

    public Ticket modifierTicket(Long id, String nouveauTitre, String nouvelleDescription, Statut nouveauStatut, String nouveauLieu) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du ticket à modifier ne peut pas être nul.");
        }
        if (nouveauTitre == null || nouveauTitre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du ticket ne peut pas être vidé lors d'une modification.");
        }

        Ticket ticket = ticketDao.trouverParId(id);
        if (ticket == null) {
            throw new IllegalArgumentException("Impossible de modifier : aucun ticket trouvé avec l'ID " + id);
        }

        ticket.setTitre(nouveauTitre);
        ticket.setDescription(nouvelleDescription);
        ticket.setStatut(nouveauStatut);
        ticket.setLocation(nouveauLieu);

        return ticketDao.sauvegarder(ticket);
    }

    public void supprimerTicket(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du ticket à supprimer ne peut pas être nul.");
        }

        Ticket ticket = ticketDao.trouverParId(id);
        if (ticket == null) {
            throw new IllegalArgumentException("Impossible de supprimer : aucun ticket avec l'ID " + id);
        }

        ticketDao.supprimer(id);
    }
}