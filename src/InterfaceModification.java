import modele.Statut;
import modele.Ticket;
import service.MockIncidentService;

import javax.swing.*;
import java.awt.*;

//fenetre popup
public class InterfaceModification extends JDialog {

    private MockIncidentService MockIncidentService;
    private InterfaceAcceuil fenetreParente;
    private Ticket ticket; // Le ticket qu'on est en train de modifier

    //composants
    JTextField txtTitre;
    JTextArea txtDescription;
    JComboBox<Statut> comboStatut;
    JTextField txtLieu;
    JLabel lblErreur;

    public InterfaceModification(InterfaceAcceuil parent, MockIncidentService MockIncidentService, Ticket ticket) {
        super(parent, "Gérer le ticket #" + ticket.getId(), true);
        this.fenetreParente = parent;
        this.MockIncidentService = MockIncidentService;
        this.ticket = ticket;

        configurerFenetre();
        initialiserComposants();
    }

    private void configurerFenetre() {
        setSize(500, 450);
        setLocationRelativeTo(fenetreParente);
        setLayout(new BorderLayout());
    }

    private void initialiserComposants() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //infos non modifiables
        formPanel.add(new JLabel("Demandeur :"));
        JLabel lblDemandeur = new JLabel(ticket.getCreateur().getNom() + " (le " + ticket.getDate() + ")");
        lblDemandeur.setForeground(Color.GRAY);
        formPanel.add(lblDemandeur);

        //modifiables
        formPanel.add(new JLabel("Statut :"));
        comboStatut = new JComboBox<>(Statut.values());//les references sur status
        comboStatut.setSelectedItem(ticket.getStatut());//val de status actuelle
        formPanel.add(comboStatut);
        formPanel.add(new JLabel("Titre :"));
        txtTitre = new JTextField(ticket.getTitre());
        formPanel.add(txtTitre);
        formPanel.add(new JLabel("Localisation :"));
        txtLieu = new JTextField(ticket.getLocation());
        formPanel.add(txtLieu);
        formPanel.add(new JLabel("Description :"));
        txtDescription = new JTextArea(ticket.getDescription());
        txtDescription.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(new JScrollPane(txtDescription));

        add(formPanel, BorderLayout.CENTER);
        //gestion erreurs
        lblErreur = new JLabel(" ");
        lblErreur.setForeground(Color.RED);
        lblErreur.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblErreur, BorderLayout.NORTH);

        //boutons annuler
        JPanel bottomPanel = new JPanel();
        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose());
        //enrengistrer
        JButton btnValider = new JButton("Enregistrer les modifications");
        btnValider.addActionListener(e -> validerModification());

        //ajout a la fenetre des boutons
        bottomPanel.add(btnAnnuler);
        bottomPanel.add(btnValider);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    void validerModification() {
        try {
            String titre = txtTitre.getText();
            String description = txtDescription.getText();
            String lieu = txtLieu.getText();
            Statut statut = (Statut) comboStatut.getSelectedItem();

            if (titre.trim().isEmpty()) {
                lblErreur.setText("Erreur : Le titre ne peut pas être vide.");
                return;
            }
            if (lieu.trim().isEmpty()) {
                lblErreur.setText("Erreur : La localisation est obligatoire.");
                return;
            }
            if (description.trim().isEmpty()) {
                lblErreur.setText("Erreur : La description ne peut pas être vide.");
                return;
            }


            //modifie les champs, rafraichit et ferme la fenetre
            MockIncidentService.modifierTicket(ticket.getId(), titre, description, statut, lieu);
            fenetreParente.rafraichirTableau();
            dispose();
            if (!Main.modeTest) JOptionPane.showMessageDialog(fenetreParente, "Ticket mis à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            //gestion erreur
        } catch (IllegalArgumentException ex) {
            lblErreur.setText("Erreur : " + ex.getMessage());
        }catch (Exception ex) {
            lblErreur.setText("Une erreur inattendue est survenue.");
        }
    }
}
