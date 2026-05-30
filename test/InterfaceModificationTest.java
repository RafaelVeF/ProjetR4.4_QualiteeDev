import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gestionincidents.service.IncidentService;
import gestionincidents.UtilisateurService;
import gestionincidents.dao.TicketDaoMemory;
import gestionincidents.dao.UtilisateurDaoMemory;
import gestionincidents.model.Statut;
import gestionincidents.model.Ticket;
import gestionincidents.model.Utilisateur;
import gestionincidents.model.Role;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;

public class InterfaceModificationTest {

    private IncidentService incidentService;
    private UtilisateurService utilisateurService;
    private InterfaceAcceuil parent;
    private Ticket ticket;

    @BeforeEach
    public void setUp() {
        TicketDaoMemory ticketDao = new TicketDaoMemory();
        UtilisateurDaoMemory utilisateurDao = new UtilisateurDaoMemory();
        incidentService = new IncidentService(ticketDao);
        utilisateurService = new UtilisateurService(utilisateurDao);
        
        Utilisateur u = utilisateurService.creerUtilisateur("Admin", "admin@mail.com", Role.TECHNICIEN);
        ticket = incidentService.creerTicket("Ancien Titre", "Ancienne Description", u, LocalDate.now(), "Ancien Lieu");
        
        parent = new InterfaceAcceuil(incidentService, utilisateurService, u);
    }

    @Test
    public void testInitialisationValeursCorrectes() throws Exception {
        incidentService.modifierTicket(ticket.getId(), ticket.getTitre(), ticket.getDescription(), Statut.RESOLU, ticket.getLocation());
        
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);
        
        JComboBox<Statut> comboStatut = getField(im, "comboStatut");
        assertEquals(Statut.RESOLU, comboStatut.getSelectedItem());
        
        JTextField txtTitre = getField(im, "txtTitre");
        assertEquals("Ancien Titre", txtTitre.getText());
    }

    @Test
    public void testValidationModificationSucces() throws Exception {
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);

        setField(im, "txtTitre", "Titre Modifié");
        JComboBox<Statut> comboStatut = getField(im, "comboStatut");
        comboStatut.setSelectedItem(Statut.EN_COURS);
        
        fermerPopupAutomatiquement();
        invokeValiderModification(im);

        Ticket ticketModifie = incidentService.obtenirTicket(ticket.getId());
        assertEquals("Titre Modifié", ticketModifie.getTitre());
        assertEquals(Statut.EN_COURS, ticketModifie.getStatut());
    }

    @Test
    public void testValidationModificationErreur() throws Exception {
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);

        setField(im, "txtTitre", ""); 
        
        invokeValiderModification(im);
        
        JLabel lblErreur = getField(im, "lblErreur");
        assertNotNull(lblErreur);
    }

    @Test
    public void testTexteExtremementLong() throws Exception {
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);

        String texteTresLong = "A".repeat(5000);
        
        setField(im, "txtTitre", texteTresLong); 
        setField(im, "txtDescription", texteTresLong); 
        
        fermerPopupAutomatiquement();
        invokeValiderModification(im);
        
        Ticket ticketModifie = incidentService.obtenirTicket(ticket.getId());
        assertNotNull(ticketModifie);
    }

    private void setField(Object obj, String fieldName, String value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object component = field.get(obj);
        if (component instanceof JTextField) {
            ((JTextField) component).setText(value);
        } else if (component instanceof JTextArea) {
            ((JTextArea) component).setText(value);
        }
    }

    private <T> T getField(Object obj, String fieldName) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(obj);
    }

    private void invokeValiderModification(Object obj) throws Exception {
        Method method = obj.getClass().getDeclaredMethod("validerModification");
        method.setAccessible(true);
        method.invoke(obj);
    }

    private void fermerPopupAutomatiquement() {
        new Thread(() -> {
            try {
                Thread.sleep(500); // Attendre que la popup s'ouvre
                for (java.awt.Window window : java.awt.Window.getWindows()) {
                    if (window instanceof JDialog) {
                        JDialog dialog = (JDialog) window;
                        if ("Succès".equals(dialog.getTitle())) {
                            dialog.dispose(); // Ferme la popup bloquante
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
