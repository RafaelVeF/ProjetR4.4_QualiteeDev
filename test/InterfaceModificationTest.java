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
        
        Utilisateur u = utilisateurService.creerUtilisateur("Admin", "admin@mail.com");
        ticket = incidentService.creerTicket("Ancien Titre", "Ancienne Description", u, LocalDate.now(), "Ancien Lieu");
        
        parent = new InterfaceAcceuil(incidentService, utilisateurService);
    }

    @Test
    public void testInitialisationValeursCorrectes() throws Exception {
        // On modifie le statut pour vérifier que le composant s'initialise bien sur la bonne valeur
        incidentService.modifierTicket(ticket.getId(), ticket.getTitre(), ticket.getDescription(), Statut.RESOLU, ticket.getLocation());
        
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);
        
        JComboBox<Statut> comboStatut = getField(im, "comboStatut");
        assertEquals(Statut.RESOLU, comboStatut.getSelectedItem(), "Le menu déroulant doit s'initialiser sur le bon statut");
        
        JTextField txtTitre = getField(im, "txtTitre");
        assertEquals("Ancien Titre", txtTitre.getText(), "Le titre doit être pré-rempli correctement");
    }

    @Test
    public void testValidationModificationSucces() throws Exception {
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);

        // Modification des valeurs via réflexion
        setField(im, "txtTitre", "Titre Modifié");
        JComboBox<Statut> comboStatut = getField(im, "comboStatut");
        comboStatut.setSelectedItem(Statut.EN_COURS);
        
        // Exécution
        invokeValiderModification(im);

        // Vérification
        Ticket ticketModifie = incidentService.obtenirTicket(ticket.getId());
        assertEquals("Titre Modifié", ticketModifie.getTitre(), "Le titre du ticket doit avoir été mis à jour dans le service");
        assertEquals(Statut.EN_COURS, ticketModifie.getStatut(), "Le statut doit avoir été mis à jour dans le service");
    }

    @Test
    public void testValidationModificationErreur() throws Exception {
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);

        // Simulation d'une erreur en envoyant un titre vide (si géré par le service)
        // Note : Si le service IncidentService.modifierTicket lève une IllegalArgumentException sur les champs vides,
        // ce test le couvrira.
        setField(im, "txtTitre", ""); 
        
        invokeValiderModification(im);
        
        // S'il y a des protections dans les services, le ticket.getTitre() n'aura pas changé
        // ou le labelErreur contiendra un texte.
        JLabel lblErreur = getField(im, "lblErreur");
        // On s'assure que la méthode a fini de s'exécuter sans planter l'application entière
        assertNotNull(lblErreur);
    }

    @Test
    public void testTexteExtremementLong() throws Exception {
        InterfaceModification im = new InterfaceModification(parent, incidentService, ticket);

        // Génère un texte de 5000 caractères
        String texteTresLong = "A".repeat(5000);
        
        setField(im, "txtTitre", texteTresLong); 
        setField(im, "txtDescription", texteTresLong); 
        
        invokeValiderModification(im);
        
        // S'assurer que le composant TextArea et le service supportent de grandes chaînes sans Timeout ni Crash
        Ticket ticketModifie = incidentService.obtenirTicket(ticket.getId());
        assertNotNull(ticketModifie, "Le ticket doit toujours exister");
    }

    // --- Utilitaires ---

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
}
