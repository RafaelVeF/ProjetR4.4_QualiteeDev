package gestionincidents.dao;

import gestionincidents.model.Ticket;
import java.util.List;
import java.util.ArrayList;

public class TicketDaoMemory implements TicketDao {
    private final List<Ticket> baseDeDonneesIdem = new ArrayList<>();
    private long compteurId = 1L;

    @Override
    public Ticket sauvegarder(Ticket ticket) {
        if (ticket.getId() == null) {
            ticket.setId(compteurId++);
            baseDeDonneesIdem.add(ticket);
        } else {
            Ticket existant = trouverParId(ticket.getId());
            if (existant != null) {
                existant.setTitre(ticket.getTitre());
                existant.setDescription(ticket.getDescription());
                existant.setStatut(ticket.getStatut());
            }
        }
        return ticket;
    }

    @Override
    public Ticket trouverParId(Long id) {
        return baseDeDonneesIdem.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Ticket trouverParTitreEtCreateur(String titre, Long createurId) {
        return baseDeDonneesIdem.stream()
                .filter(t -> t.getTitre().equalsIgnoreCase(titre) && t.getCreateur().getId().equals(createurId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Ticket> trouverTous() {
        return new ArrayList<>(baseDeDonneesIdem);
    }

    @Override
    public void supprimer(Long id) {
        baseDeDonneesIdem.removeIf(t -> t.getId().equals(id));
    }
}
