import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gestionincidents.service.IncidentService;
import gestionincidents.UtilisateurService;
import gestionincidents.dao.TicketDaoMemory;
import gestionincidents.dao.UtilisateurDaoMemory;
import gestionincidents.model.Ticket;
import gestionincidents.model.Utilisateur;
import gestionincidents.model.Statut;
import gestionincidents.model.Role;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.time.LocalDate;

public class InterfaceAcceuilTest {

    private IncidentService incidentService;
    private UtilisateurService utilisateurService;
    private InterfaceAcceuil interfaceAcceuil;
    private Utilisateur techUser;
    private Utilisateur classiqueUser;

    @BeforeEach
    public void setUp() {
        TicketDaoMemory ticketDao = new TicketDaoMemory();
        UtilisateurDaoMemory utilisateurDao = new UtilisateurDaoMemory();
        incidentService = new IncidentService(ticketDao);
        utilisateurService = new UtilisateurService(utilisateurDao);
        
        techUser = utilisateurService.creerUtilisateur("Technicien", "tech@test.com", Role.TECHNICIEN);
        classiqueUser = utilisateurService.creerUtilisateur("Classique", "class@test.com", Role.CLASSIQUE);
    }

    @Test
    public void testInitialisationInterface() {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        assertNotNull(interfaceAcceuil);
        assertEquals("Gestionnaire de ticket d'incidents", interfaceAcceuil.getTitle());
    }

    @Test
    public void testRafraichirTableauSansDonnees() throws Exception {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        
        Field tableModelField = InterfaceAcceuil.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(interfaceAcceuil);
        
        assertEquals(0, tableModel.getRowCount(), "Le tableau devrait être vide s'il n'y a pas de tickets.");
    }

    @Test
    public void testRafraichirTableauAvecDonnees() throws Exception {
        incidentService.creerTicket("Panne A", "Desc A", techUser, LocalDate.now(), "Lieu A");
        incidentService.creerTicket("Panne B", "Desc B", techUser, LocalDate.now(), "Lieu B");

        // En tant que technicien, il voit tout
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        
        Field tableModelField = InterfaceAcceuil.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(interfaceAcceuil);

        assertEquals(2, tableModel.getRowCount(), "Le tableau devrait contenir 2 lignes pour un technicien.");
    }

    @Test
    public void testBoutonsDesactivesParDefaut() throws Exception {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        
        JButton btnModifier = getField(interfaceAcceuil, "btnModifier");
        JButton btnSupprimer = getField(interfaceAcceuil, "btnSupprimer");
        
        // Pour un technicien, les boutons existent mais sont grisés par défaut
        assertTrue(btnModifier.isVisible());
        assertFalse(btnModifier.isEnabled());
        assertTrue(btnSupprimer.isVisible());
        assertFalse(btnSupprimer.isEnabled());
    }

    @Test
    public void testBoutonsActivesApresSelection() throws Exception {
        incidentService.creerTicket("Panne A", "Desc", techUser, LocalDate.now(), "Lieu");
        
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        
        JTable ticketTable = getField(interfaceAcceuil, "ticketTable");
        JButton btnModifier = getField(interfaceAcceuil, "btnModifier");
        JButton btnSupprimer = getField(interfaceAcceuil, "btnSupprimer");
        
        ticketTable.setRowSelectionInterval(0, 0);
        
        assertTrue(btnModifier.isEnabled(), "Le technicien doit pouvoir modifier après sélection.");
        assertTrue(btnSupprimer.isEnabled(), "Le technicien doit pouvoir supprimer après sélection.");
    }

    @Test
    public void testFiltreRecherche() throws Exception {
        incidentService.creerTicket("Panne Serveur", "Desc A", techUser, LocalDate.now(), "Lieu A");
        incidentService.creerTicket("Imprimante HS", "Desc B", techUser, LocalDate.now(), "Lieu B");

        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        
        JTextField txtRecherche = getField(interfaceAcceuil, "txtRecherche");
        JTable ticketTable = getField(interfaceAcceuil, "ticketTable");
        
        assertEquals(2, ticketTable.getRowCount());
        txtRecherche.setText("Serveur");
        
        java.lang.reflect.Method filtrerMethod = getMethodInDocumentListener(txtRecherche);
        if(filtrerMethod != null) {
            filtrerMethod.invoke(getListener(txtRecherche));
        }
    }

    @Test
    public void testVisibiliteBoutonsSelonRoleClassique() throws Exception {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, classiqueUser);
        
        JButton btnModifier = getField(interfaceAcceuil, "btnModifier");
        JButton btnSupprimer = getField(interfaceAcceuil, "btnSupprimer");
        
        // Pour un classique, les boutons doivent être invisibles
        assertFalse(btnModifier.isVisible(), "L'utilisateur classique ne doit pas voir le bouton Modifier");
        assertFalse(btnSupprimer.isVisible(), "L'utilisateur classique ne doit pas voir le bouton Supprimer");
    }

    @Test
    public void testAffichageTicketsSelonRole() throws Exception {
        Utilisateur autreUser = utilisateurService.creerUtilisateur("Autre", "autre@test.com", Role.CLASSIQUE);
        
        // Un ticket créé par l'utilisateur classique actuel
        incidentService.creerTicket("Panne Mon PC", "Desc", classiqueUser, LocalDate.now(), "Lieu");
        // Un ticket créé par quelqu'un d'autre
        incidentService.creerTicket("Panne Son PC", "Desc", autreUser, LocalDate.now(), "Lieu");
        
        // Connexion en classique
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService, classiqueUser);
        Field tableModelField = InterfaceAcceuil.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(interfaceAcceuil);
        
        assertEquals(1, tableModel.getRowCount(), "Un utilisateur CLASSIQUE ne doit voir que ses propres tickets");
        assertEquals("Panne Mon PC", tableModel.getValueAt(0, 1));
        
        // Connexion en technicien
        InterfaceAcceuil interfaceTech = new InterfaceAcceuil(incidentService, utilisateurService, techUser);
        DefaultTableModel tableModelTech = (DefaultTableModel) tableModelField.get(interfaceTech);
        assertEquals(2, tableModelTech.getRowCount(), "Un technicien doit voir tous les tickets");
    }

    // --- Utilitaires de Réflexion ---

    private <T> T getField(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch(NoSuchFieldException e) {
            return findComponentByName((java.awt.Container) obj, fieldName);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private <T> T findComponentByName(java.awt.Container container, String hint) {
        for (java.awt.Component c : container.getComponents()) {
            if (c instanceof JButton && ((JButton) c).getText().toLowerCase().contains(hint.replace("btn", "").toLowerCase())) {
                return (T) c;
            }
            if (c instanceof JTextField && hint.equals("txtRecherche") && c.getParent().toString().contains("FlowLayout")) {
                return (T) c;
            }
            if (c instanceof java.awt.Container) {
                T found = findComponentByName((java.awt.Container) c, hint);
                if (found != null) return found;
            }
        }
        return null;
    }

    private Object getListener(JTextField field) throws Exception {
        return ((javax.swing.text.AbstractDocument) field.getDocument()).getDocumentListeners()[0];
    }

    private java.lang.reflect.Method getMethodInDocumentListener(JTextField field) throws Exception {
        Object listener = getListener(field);
        java.lang.reflect.Method method = listener.getClass().getDeclaredMethod("filtrer");
        method.setAccessible(true);
        return method;
    }
}
