import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import gestionincidents.service.IncidentService;
import gestionincidents.UtilisateurService;
import gestionincidents.dao.TicketDaoMemory;
import gestionincidents.dao.UtilisateurDaoMemory;
import gestionincidents.model.Utilisateur;

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
        
        userActif = utilisateurService.creerUtilisateur("Testeur", "test@mail.com");
        parent = new InterfaceAcceuil(incidentService, utilisateurService);
    }

    @Test
    public void testInitialisationInterfaceCreation() {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);
        assertNotNull(ic);
    }

    @Test
    public void testInitialisationSansUtilisateur() {
        // On recrée les services à vide pour ne pas avoir d'utilisateur
        incidentService = new IncidentService(new TicketDaoMemory());
        utilisateurService = new UtilisateurService(new UtilisateurDaoMemory());
        parent = new InterfaceAcceuil(incidentService, utilisateurService);
        
        // S'assurer que ça ne plante pas s'il n'y a aucun utilisateur dans la BDD
        assertDoesNotThrow(() -> {
            new InterfaceCreation(parent, incidentService, utilisateurService);
        });
    }

    @Test
    public void testValidationFormulaireSucces() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        // Simulation de saisie de données via réflexion
        setField(ic, "txtTitre", "Nouveau Bug");
        setField(ic, "txtDescription", "Impossible de se connecter");
        setField(ic, "txtLieu", "Salle Informatique");
        setField(ic, "txtDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        // Exécution de la validation
        invokeValiderFormulaire(ic);

        // Vérification : Le ticket a dû être créé dans le service
        assertEquals(1, incidentService.obtenirTousLesTickets().size(), "Le ticket aurait dû être enregistré en BDD");
        assertEquals("Nouveau Bug", incidentService.obtenirTousLesTickets().get(0).getTitre());
    }

    @Test
    public void testValidationFormulaireDateInvalide() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        // Simulation de saisie avec une date cassée
        setField(ic, "txtTitre", "Nouveau Bug");
        setField(ic, "txtDate", "31-02-2026"); // Mauvais format
        
        // Exécution de la validation
        invokeValiderFormulaire(ic);

        // Vérification : Le ticket ne doit PAS avoir été créé
        assertEquals(0, incidentService.obtenirTousLesTickets().size(), "Le ticket ne devrait pas être créé avec une date invalide");
        
        // Vérification que le message d'erreur s'affiche bien
        JLabel lblErreur = getField(ic, "lblErreur");
        assertTrue(lblErreur.getText().contains("format de la date"), "Le message d'erreur de date doit s'afficher");
    }

    @Test
    public void testValidationFormulaireChampsVides() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        // On vide tous les champs
        setField(ic, "txtTitre", "");
        setField(ic, "txtDescription", "");
        setField(ic, "txtLieu", "");
        setField(ic, "txtDate", "");

        // On valide
        invokeValiderFormulaire(ic);

        // Si le service rejette, il lèvera une exception et le label sera mis à jour.
        // On vérifie au moins que ça ne crash pas le thread principal et que rien n'est inséré
        assertEquals(0, incidentService.obtenirTousLesTickets().size(), "Un ticket entièrement vide ne devrait pas être créé");
    }
    
    @Test
    public void testInjectionCaracteresSpeciaux() throws Exception {
        InterfaceCreation ic = new InterfaceCreation(parent, incidentService, utilisateurService);

        // Simulation de saisie de données complexes
        setField(ic, "txtTitre", "<script>alert('XSS')</script>");
        setField(ic, "txtDescription", "DROP TABLE tickets;--");
        setField(ic, "txtLieu", "😈👽💩");
        setField(ic, "txtDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        invokeValiderFormulaire(ic);

        assertEquals(1, incidentService.obtenirTousLesTickets().size());
        assertEquals("<script>alert('XSS')</script>", incidentService.obtenirTousLesTickets().get(0).getTitre(), "L'interface doit accepter les caractères spéciaux sans crash");
        assertEquals("DROP TABLE tickets;--", incidentService.obtenirTousLesTickets().get(0).getDescription());
    }

    // --- Fonctions utilitaires de réflexion pour tester le code fermé ---

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
}
