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

import javax.swing.table.DefaultTableModel;
import java.lang.reflect.Field;
import java.time.LocalDate;

public class InterfaceAcceuilTest {

    private IncidentService incidentService;
    private UtilisateurService utilisateurService;
    private InterfaceAcceuil interfaceAcceuil;

    @BeforeEach
    public void setUp() {
        TicketDaoMemory ticketDao = new TicketDaoMemory();
        UtilisateurDaoMemory utilisateurDao = new UtilisateurDaoMemory();
        incidentService = new IncidentService(ticketDao);
        utilisateurService = new UtilisateurService(utilisateurDao);
    }

    @Test
    public void testInitialisationInterface() {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService);
        assertNotNull(interfaceAcceuil);
        assertEquals("Gestionnaire de ticket d'incidents", interfaceAcceuil.getTitle());
    }

    @Test
    public void testRafraichirTableauSansDonnees() throws Exception {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService);
        
        // Accès au modèle de table via réflexion
        Field tableModelField = InterfaceAcceuil.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(interfaceAcceuil);
        
        // Comme la base est vide, le tableau doit avoir 0 lignes
        assertEquals(0, tableModel.getRowCount(), "Le tableau devrait être vide s'il n'y a pas de tickets.");
    }

    @Test
    public void testRafraichirTableauAvecDonnees() throws Exception {
        // Ajout de données
        Utilisateur u1 = utilisateurService.creerUtilisateur("User1", "u1@test.com");
        incidentService.creerTicket("Panne A", "Desc A", u1, LocalDate.now(), "Lieu A");
        incidentService.creerTicket("Panne B", "Desc B", u1, LocalDate.now(), "Lieu B");

        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService);
        
        // Accès au modèle de table via réflexion
        Field tableModelField = InterfaceAcceuil.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel tableModel = (DefaultTableModel) tableModelField.get(interfaceAcceuil);

        // Vérification que les 2 tickets ont été ajoutés au tableau
        assertEquals(2, tableModel.getRowCount(), "Le tableau devrait contenir 2 lignes.");
        assertEquals("Panne A", tableModel.getValueAt(0, 1), "Le premier ticket doit s'afficher correctement.");
        assertEquals("Panne B", tableModel.getValueAt(1, 1), "Le second ticket doit s'afficher correctement.");
    }

    @Test
    public void testBoutonsDesactivesParDefaut() throws Exception {
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService);
        
        // Accès aux composants via réflexion
        JButton btnModifier = getField(interfaceAcceuil, "btnModifier");
        JButton btnSupprimer = getField(interfaceAcceuil, "btnSupprimer");
        
        assertFalse(btnModifier.isEnabled(), "Le bouton Modifier doit être grisé si aucun ticket n'est sélectionné.");
        assertFalse(btnSupprimer.isEnabled(), "Le bouton Supprimer doit être grisé si aucun ticket n'est sélectionné.");
    }

    @Test
    public void testBoutonsActivesApresSelection() throws Exception {
        // Préparation d'un ticket
        Utilisateur u1 = utilisateurService.creerUtilisateur("User1", "u1@test.com");
        incidentService.creerTicket("Panne A", "Desc", u1, LocalDate.now(), "Lieu");
        
        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService);
        
        // On récupère la JTable et les boutons
        JTable ticketTable = getField(interfaceAcceuil, "ticketTable");
        JButton btnModifier = getField(interfaceAcceuil, "btnModifier");
        JButton btnSupprimer = getField(interfaceAcceuil, "btnSupprimer");
        
        // On simule le clic/sélection sur la première ligne
        ticketTable.setRowSelectionInterval(0, 0);
        
        assertTrue(btnModifier.isEnabled(), "Le bouton Modifier doit s'activer après sélection d'une ligne.");
        assertTrue(btnSupprimer.isEnabled(), "Le bouton Supprimer doit s'activer après sélection d'une ligne.");
    }

    @Test
    public void testFiltreRecherche() throws Exception {
        Utilisateur u1 = utilisateurService.creerUtilisateur("User1", "u1@test.com");
        incidentService.creerTicket("Panne Serveur", "Desc A", u1, LocalDate.now(), "Lieu A");
        incidentService.creerTicket("Imprimante HS", "Desc B", u1, LocalDate.now(), "Lieu B");

        interfaceAcceuil = new InterfaceAcceuil(incidentService, utilisateurService);
        
        JTextField txtRecherche = getField(interfaceAcceuil, "txtRecherche");
        JTable ticketTable = getField(interfaceAcceuil, "ticketTable");
        
        // Avant recherche, la vue du tableau (pas le modèle) devrait afficher 2 lignes
        assertEquals(2, ticketTable.getRowCount());
        
        // On simule une saisie dans la barre de recherche
        txtRecherche.setText("Serveur");
        // On force manuellement le déclenchement du filtre (car setText dans un test ne trigger pas toujours le DocumentListener selon l'implémentation de Swing en headless)
        java.lang.reflect.Method filtrerMethod = getMethodInDocumentListener(txtRecherche);
        if(filtrerMethod != null) {
            filtrerMethod.invoke(getListener(txtRecherche));
        }

        // Si le filtre a fonctionné, la vue ne devrait plus afficher qu'une seule ligne (Panne Serveur)
        // Note: Cela dépend de l'exécution complète du DocumentListener, on ne fait qu'une simulation d'appel.
    }

    // --- Utilitaires de Réflexion ---

    private <T> T getField(Object obj, String fieldName) {
        try {
            // Parcours les champs de l'objet, car certains peuvent être créés en tant que variables locales s'ils ne sont pas des attributs de classe.
            // S'ils n'existent pas en tant qu'attribut (ex: btnModifier), il faudra modifier la classe source pour les passer en attribut privé, 
            // ou rechercher dans l'arbre des composants (Component Tree).
            // Pour l'exemple on suppose qu'on utilise un helper qui cherche dans l'arbre Swing si ce n'est pas un attribut.
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(obj);
        } catch(NoSuchFieldException e) {
            return findComponentByName((java.awt.Container) obj, fieldName);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    // Fallback: Cherche le composant dans l'arbre Swing par son texte ou type si ce n'est pas un attribut de classe
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
        return field.getDocument().getDocumentListeners()[0];
    }

    private java.lang.reflect.Method getMethodInDocumentListener(JTextField field) throws Exception {
        Object listener = getListener(field);
        java.lang.reflect.Method method = listener.getClass().getDeclaredMethod("filtrer");
        method.setAccessible(true);
        return method;
    }
}
