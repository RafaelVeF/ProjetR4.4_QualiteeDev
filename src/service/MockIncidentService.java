package service;

import modele.Ticket;
import modele.Utilisateur;
import modele.Statut;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MockIncidentService {
    public boolean creerTicketAppele = false;
    public boolean modifierTicketAppele = false;
    public boolean supprimerTicketAppele = false;
    private List<Ticket> fauxTickets = new ArrayList<>();

    public Ticket creerTicket(String titre, String description, Utilisateur createur, LocalDate date, String location) {
        this.creerTicketAppele = true;
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du ticket ne peut pas être vide.");
        }
        Ticket nouveauTicket = new Ticket((long) (fauxTickets.size() + 1), titre, description, createur, date, location);
        fauxTickets.add(nouveauTicket);
        return nouveauTicket;
    }

    public Ticket modifierTicket(Long id, String titre, String description, Statut statut, String location) {
        this.modifierTicketAppele = true;
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre ne peut pas être vide.");
        }
        for (Ticket t : fauxTickets) {
            if (t.getId().equals(id)) {
                t.setStatut(statut);
                t.setTitre(titre);
                t.setDescription(description);
                t.setLocation(location);
                return t;
            }
        }
        return null;
    }

    public void supprimerTicket(Long id) {
        this.supprimerTicketAppele = true;
        fauxTickets.removeIf(t -> t.getId().equals(id));
    }

    public List<Ticket> obtenirTousLesTickets() {
        return fauxTickets;
    }
    
    public Ticket obtenirTicket(Long id) {
        return fauxTickets.stream().filter(t -> t.getId().equals(id)).findFirst().orElse(null);
    }
}
