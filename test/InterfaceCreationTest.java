import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import service.MockIncidentService;
import service.MockUtilisateurService;
import modele.Utilisateur;
import modele.Role;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InterfaceCreationTest {

    private MockIncidentService MockIncidentService;
    private MockUtilisateurService MockUtilisateurService;
    private InterfaceAcceuil parent;
    private Utilisateur userActif;

    @BeforeEach
    public void setUp() {
        MockIncidentService = new MockIncidentService();
        MockUtilisateurService = new MockUtilisateurService();
        
        userActif = MockUtilisateurService.creerUtilisateur("Testeur", "test@mail.com", Role.CLASSIQUE);
        parent = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, userActif);
        
        // Bloque les pop-ups
        Main.modeTest = true;
    }

    @Test
    public void testInitialisationInterfaceCreation() {
        InterfaceCreation ic = new InterfaceCreation(parent, MockIncidentService,
                MockUtilisateurService);
        assertNotNull(ic);
    }

    @Test
    public void testInitialisationSansUtilisateur() {
        MockIncidentService = new MockIncidentService();
        MockUtilisateurService = new MockUtilisateurService();
        Utilisateur invite = new Utilisateur(99L, "Invite", "inv@test.fr",
                Role.CLASSIQUE);
        parent = new InterfaceAcceuil(MockIncidentService, MockUtilisateurService, invite);
        
        assertDoesNotThrow(() -> {
            new InterfaceCreation(parent, MockIncidentService, MockUtilisateurService);
        });
    }

    @Test
    public void testValidationFormulaireSucces() {
        InterfaceCreation ic = new InterfaceCreation(parent, MockIncidentService, MockUtilisateurService);

        ic.txtTitre.setText("Nouveau Bug");
        ic.txtDescription.setText("Impossible de se connecter");
        ic.txtLieu.setText("Salle Informatique");
        ic.txtDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        ic.validerFormulaire();

        assertTrue(MockIncidentService.creerTicketAppele, "Le service n'a pas été appelé");
        assertEquals(1, MockIncidentService.obtenirTousLesTickets().size());
        assertEquals("Nouveau Bug", MockIncidentService.obtenirTousLesTickets().get(0).getTitre());
    }

    @Test
    public void testValidationFormulaireDateInvalide() {
        InterfaceCreation ic = new InterfaceCreation(parent, MockIncidentService,
                MockUtilisateurService);

        ic.txtTitre.setText("Nouveau Bug");
        ic.txtDate.setText("31-02-2026"); 
        
        ic.validerFormulaire();

        assertFalse(MockIncidentService.creerTicketAppele, "Le service ne devrait " +
                "pas être appelé si la date est invalide");
        assertEquals(0, MockIncidentService.obtenirTousLesTickets().size());
        assertTrue(ic.lblErreur.getText().contains("format de la date"));
    }

    @Test
    public void testValidationFormulaireChampsVides() {
        InterfaceCreation ic = new InterfaceCreation(parent, MockIncidentService,
                MockUtilisateurService);

        ic.txtTitre.setText("");
        ic.txtDescription.setText("");
        ic.txtLieu.setText("");
        ic.txtDate.setText("");

        ic.validerFormulaire();

        assertFalse(MockIncidentService.creerTicketAppele);
        assertEquals(0, MockIncidentService.obtenirTousLesTickets().size());
    }
    
    @Test
    public void testInjectionCaracteresSpeciaux() {
        InterfaceCreation ic = new InterfaceCreation(parent, MockIncidentService, MockUtilisateurService);

        ic.txtTitre.setText("<script>alert('XSS')</script>");
        ic.txtDescription.setText("DROP TABLE tickets;--");
        ic.txtLieu.setText("👽");
        ic.txtDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        ic.validerFormulaire();

        assertTrue(MockIncidentService.creerTicketAppele);
        assertEquals(1, MockIncidentService.obtenirTousLesTickets().size());
        assertEquals("<script>alert('XSS')</script>", MockIncidentService.obtenirTousLesTickets().get(0).getTitre());
    }

    @Test
    public void testValidationDateDansLeFutur() {
        InterfaceCreation ic = new InterfaceCreation(parent, MockIncidentService, MockUtilisateurService);

        ic.txtTitre.setText("Nouveau Bug");
        ic.txtDescription.setText("Impossible de se connecter");
        ic.txtLieu.setText("Salle Informatique");
        ic.txtDate.setText("01/01/2028");//date future

        ic.validerFormulaire();

        assertFalse(MockIncidentService.creerTicketAppele, "Le service ne devrait pas être appelé si la date est dans le futur");
        assertEquals(0, MockIncidentService.obtenirTousLesTickets().size());
        assertTrue(ic.lblErreur.getText().contains("futur") ||
                        ic.lblErreur.getText().contains("Erreur"),
                "L'interface doit indiquer que la date est dans le futur.");
    }

    @Test
    public void testExceptionBackendDoublon() {
        MockIncidentService mockDoublon = new MockIncidentService() {
            @Override
            public modele.Ticket creerTicket(String titre, String desc, Utilisateur c, LocalDate d, String l) {
                throw new IllegalStateException("Un ticket avec ce titre a déjà été déclaré.");
            }
        };

        InterfaceCreation ic = new InterfaceCreation(parent, mockDoublon, MockUtilisateurService);

        ic.txtTitre.setText("Panne Serveur");//censé etre double
        ic.txtDescription.setText("Le serveur plante");
        ic.txtLieu.setText("Salle 102");
        ic.txtDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        ic.validerFormulaire();

        assertTrue(ic.lblErreur.getText().contains("déjà été déclaré") || ic.lblErreur.getText().contains("Système"),
                "L'interface doit capturer l'IllegalStateException et l'afficher en rouge.");
    }
}
