package gestionincidents.dao;

import gestionincidents.model.Ticket;
import java.util.List;

public interface TicketDao {
    Ticket sauvegarder(Ticket ticket);
    Ticket trouverParId(Long id);
    Ticket trouverParTitreEtCreateur(String titre, Long createurId);
    List<Ticket> trouverTous();
    void supprimer(Long id);
}
