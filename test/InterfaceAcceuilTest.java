import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import service.MockIncidentService;
import service.MockUtilisateurService;
import modele.Utilisateur;
import modele.Role;

import java.time.LocalDate;

public class InterfaceAcceuilTest {

    private MockIncidentService MockIncidentService;
    private MockUtilisateurService MockUtilisateurService;
    private InterfaceAcceuil interfaceAcceuil;
    private Utilisateur techUser;
    private Utilisateur classiqueUser;

    @BeforeEach
    public void setUp() {
        MockIncidentService = new MockIncidentService();
        MockUtilisateurService = new MockUtilisateurService();
        
        techUser = MockUtilisateurService.creerUtilisateur("Technicien", "tech@test.com", Role.TECHNICIEN);
        classiqueUser = MockUtilisateurService.creerUtilisateur("Classique", "class@test.com", Role.CLASSIQUE);
        
        // Bloquer les pop-ups
        Main.modeTest = true;
    }

    @Test
    public void testInitialisationInterface() {
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        assertNotNull(interfaceAcceuil);
        assertEquals("Gestionnaire de ticket d'incidents", interfaceAcceuil.getTitle());
    }

    @Test
    public void testRafraichirTableauSansDonnees() {
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        assertEquals(0, interfaceAcceuil.tableModel.getRowCount(),
                "Le tableau devrait être vide s'il n'y a pas de tickets.");
    }

    @Test
    public void testRafraichirTableauAvecDonnees() {
        MockIncidentService.creerTicket("Panne A", "Desc A", techUser, LocalDate.now(), "Lieu A");
        MockIncidentService.creerTicket("Panne B", "Desc B", techUser, LocalDate.now(), "Lieu B");

        // En tant que technicien, il voit tout
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        assertEquals(2, interfaceAcceuil.tableModel.getRowCount(), "Le tableau devrait contenir " +
                "2 lignes pour un technicien.");
    }

    @Test
    public void testBoutonsDesactivesParDefaut() {
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        
        // Pour un technicien, les boutons existent mais sont grisés par défaut
        assertTrue(interfaceAcceuil.btnModifier.isVisible());
        assertFalse(interfaceAcceuil.btnModifier.isEnabled());
        assertTrue(interfaceAcceuil.btnSupprimer.isVisible());
        assertFalse(interfaceAcceuil.btnSupprimer.isEnabled());
    }

    @Test
    public void testBoutonsActivesApresSelection() {
        MockIncidentService.creerTicket("Panne A", "Desc", techUser, LocalDate.now(), "Lieu");
        
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        
        interfaceAcceuil.ticketTable.setRowSelectionInterval(0, 0);
        
        assertTrue(interfaceAcceuil.btnModifier.isEnabled(), "Le technicien doit pouvoir " +
                "modifier après sélection.");
        assertTrue(interfaceAcceuil.btnSupprimer.isEnabled(), "Le technicien doit pouvoir " +
                "supprimer après sélection.");
    }

    @Test
    public void testFiltreRecherche() {
        MockIncidentService.creerTicket("Panne Serveur", "Desc A",
                techUser, LocalDate.now(), "Lieu A");
        MockIncidentService.creerTicket("Imprimante HS", "Desc B",
                techUser, LocalDate.now(), "Lieu B");

        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        
        assertEquals(2, interfaceAcceuil.ticketTable.getRowCount());
        
        interfaceAcceuil.txtRecherche.setText("Serveur");
        assertEquals(1, interfaceAcceuil.ticketTable.getRowCount());
    }

    @Test
    public void testVisibiliteBoutonsSelonRoleClassique() {
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, classiqueUser);
        
        // Pour un classique, les boutons doivent être invisibles
        assertFalse(interfaceAcceuil.btnModifier.isVisible(), "L'utilisateur classique" +
                " ne doit pas voir le bouton Modifier");
        assertFalse(interfaceAcceuil.btnSupprimer.isVisible(), "L'utilisateur classique " +
                "ne doit pas voir le bouton Supprimer");
    }

    @Test
    public void testAffichageTicketsSelonRole() {
        Utilisateur autreUser = MockUtilisateurService.creerUtilisateur("Autre", "autre@test.com", Role.CLASSIQUE);
        
        // Un ticket créé par l'utilisateur classique actuel
        MockIncidentService.creerTicket("Panne Mon PC", "Desc", classiqueUser, LocalDate.now(), "Lieu");
        // Un ticket créé par quelqu'un d'autre
        MockIncidentService.creerTicket("Panne Son PC", "Desc", autreUser, LocalDate.now(), "Lieu");
        
        // Connexion en classique
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, classiqueUser);
        assertEquals(1, interfaceAcceuil.tableModel.getRowCount(), "Un utilisateur CLASSIQUE " +
                "ne doit voir que ses propres tickets");
        assertEquals("Panne Mon PC", interfaceAcceuil.tableModel.getValueAt(0, 1));
        
        // Connexion en technicien
        InterfaceAcceuil interfaceTech = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);
        assertEquals(2, interfaceTech.tableModel.getRowCount(), "Un technicien " +
                "doit voir tous les tickets");
    }

    @Test
    public void testSuppressionTicket() {
        MockIncidentService.creerTicket("Ticket à supprimer", "Desc", techUser, LocalDate.now(), "Lieu");
        interfaceAcceuil = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, techUser);

        assertEquals(1, interfaceAcceuil.ticketTable.getRowCount(), "Le ticket doit être présent au début.");

        interfaceAcceuil.ticketTable.setRowSelectionInterval(0, 0);
        interfaceAcceuil.btnSupprimer.doClick();

        assertTrue(MockIncidentService.supprimerTicketAppele, "Le service " +
                "de suppression du backend doit avoir été appelé.");
        assertEquals(0, interfaceAcceuil.ticketTable.getRowCount(), "Le ticket " +
                "doit avoir disparu du tableau instantanément.");
    }
}
