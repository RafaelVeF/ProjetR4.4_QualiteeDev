import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gestionincidents.service.IncidentService;
import gestionincidents.UtilisateurService;
import gestionincidents.dao.TicketDaoMemory;
import gestionincidents.dao.UtilisateurDaoMemory;
import gestionincidents.model.Utilisateur;
import gestionincidents.model.Role;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InterfaceCreationTest {

    private IncidentService incidentService;
    private UtilisateurService utilisateurService;
    private InterfaceAcceuil parent;
    private Utilisateur userActif;

    @BeforeEach
    public void setUp() {
        TicketDaoMemory ticketDao = new TicketDaoMemory();
        UtilisateurDaoMemory utilisateurDao = new UtilisateurDaoMemory();
        incidentService = new IncidentService(ticketDao);
        utilisateurService = new UtilisateurService(utilisateurDao);
        
        userActif = utilisateurService.creerUtilisateur("Testeur", "test@mail.com", Role.CLASSIQUE);
        parent = new InterfaceAcceuil(incidentService, utilisateurService, userActif);
    }

    @Test
    public void testInitialisationInterfaceCreation() {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);
        assertNotNull(ic);
    }

    @Test
    public void testInitialisationSansUtilisateur() {
        incidentService = new IncidentService(new TicketDaoMemory());
        utilisateurService = new UtilisateurService(new UtilisateurDaoMemory());
        // Même si la base est vide, il faut un utilisateur "connecté" simulé pour l'accueil
        Utilisateur invite = new Utilisateur(99L, "Invite", "inv@test.fr", Role.CLASSIQUE);
        parent = new InterfaceAcceuil(incidentService, utilisateurService, invite);
        
        assertDoesNotThrow(() -> {
            new InterfaceCreation(parent, incidentService, utilisateurService);
        });
    }

    @Test
    public void testValidationFormulaireSucces() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        setField(ic, "txtTitre", "Nouveau Bug");
        setField(ic, "txtDescription", "Impossible de se connecter");
        setField(ic, "txtLieu", "Salle Informatique");
        setField(ic, "txtDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        invokeValiderFormulaire(ic);

        assertEquals(1, incidentService.obtenirTousLesTickets().size());
        assertEquals("Nouveau Bug", incidentService.obtenirTousLesTickets().get(0).getTitre());
    }

    @Test
    public void testValidationFormulaireDateInvalide() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        setField(ic, "txtTitre", "Nouveau Bug");
        setField(ic, "txtDate", "31-02-2026"); 
        
        invokeValiderFormulaire(ic);

        assertEquals(0, incidentService.obtenirTousLesTickets().size());
        
        JLabel lblErreur = getField(ic, "lblErreur");
        assertTrue(lblErreur.getText().contains("format de la date"));
    }

    @Test
    public void testValidationFormulaireChampsVides() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        setField(ic, "txtTitre", "");
        setField(ic, "txtDescription", "");
        setField(ic, "txtLieu", "");
        setField(ic, "txtDate", "");

        invokeValiderFormulaire(ic);

        assertEquals(0, incidentService.obtenirTousLesTickets().size());
    }
    
    @Test
    public void testInjectionCaracteresSpeciaux() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        setField(ic, "txtTitre", "<script>alert('XSS')</script>");
        setField(ic, "txtDescription", "DROP TABLE tickets;--");
        setField(ic, "txtLieu", "😈👽💩");
        setField(ic, "txtDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        invokeValiderFormulaire(ic);

        assertEquals(1, incidentService.obtenirTousLesTickets().size());
        assertEquals("<script>alert('XSS')</script>", incidentService.obtenirTousLesTickets().get(0).getTitre());
        assertEquals("DROP TABLE tickets;--", incidentService.obtenirTousLesTickets().get(0).getDescription());
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

    private void invokeValiderFormulaire(Object obj) throws Exception {
        Method method = obj.getClass().getDeclaredMethod("validerFormulaire");
        method.setAccessible(true);
        method.invoke(obj);
    }

    private void fermerPopupAutomatiquement() {
        new Thread(() -> {
            try {
                Thread.sleep(500); // Attendre une demi-seconde que la popup s'ouvre
                for (java.awt.Window window : java.awt.Window.getWindows()) {
                    if (window instanceof JDialog) {
                        JDialog dialog = (JDialog) window;
                        // Si le titre de la popup correspond au message de succès
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
