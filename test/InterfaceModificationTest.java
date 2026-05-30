import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import service.MockIncidentService;
import service.MockUtilisateurService;
import modele.Statut;
import modele.Ticket;
import modele.Utilisateur;
import modele.Role;

import java.time.LocalDate;

public class InterfaceModificationTest {

    private MockIncidentService MockIncidentService;
    private MockUtilisateurService MockUtilisateurService;
    private InterfaceAcceuil parent;
    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        MockIncidentService = new MockIncidentService();
        MockUtilisateurService = new MockUtilisateurService();
        
        Utilisateur u = MockUtilisateurService.creerUtilisateur("Admin", "admin@mail.com", Role.TECHNICIEN);
        ticket = MockIncidentService.creerTicket("Ancien Titre", "Ancienne Description", u, LocalDate.now(), "Ancien Lieu");
        
        parent = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, u);
        
        // Bloquer pop-ups
        Main.modeTest = true;
    }

    @Test
    public void testInitialisationValeursCorrectes() {
        MockIncidentService.modifierTicket(ticket.getId(), ticket.getTitre(),
                ticket.getDescription(), Statut.RESOLU, ticket.getLocation());
        
        InterfaceModification im = new InterfaceModification(parent, MockIncidentService, ticket);
        
        assertEquals(Statut.RESOLU, im.comboStatut.getSelectedItem());
        assertEquals("Ancien Titre", im.txtTitre.getText());
    }

    @Test
    public void testValidationModificationSucces() {
        InterfaceModification im = new InterfaceModification(parent, MockIncidentService, ticket);

        im.txtTitre.setText("Titre Modifié");
        im.comboStatut.setSelectedItem(Statut.EN_COURS);
        
        im.validerModification();

        assertTrue(MockIncidentService.modifierTicketAppele);
    }

    @Test
    public void testValidationModificationErreur() {
        InterfaceModification im = new InterfaceModification(parent, MockIncidentService, ticket);

        im.txtTitre.setText(""); 
        
        im.validerModification();
        
        assertTrue(im.lblErreur.getText().contains("Erreur"));
    }

    @Test
    public void testTexteExtremementLong() {
        InterfaceModification im = new InterfaceModification(parent, MockIncidentService, ticket);

        String texteTresLong = "A".repeat(5000);
        
        im.txtTitre.setText(texteTresLong); 
        im.txtDescription.setText(texteTresLong); 
        
        im.validerModification();
        
        assertTrue(MockIncidentService.modifierTicketAppele);
    }
}
