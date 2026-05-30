import gestionincidents.model.Utilisateur;
import gestionincidents.service.IncidentService;
import gestionincidents.UtilisateurService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

// JDialog pour fenetre en avant plan
public class InterfaceCreation extends JDialog {

    private IncidentService incidentService;
    private UtilisateurService utilisateurService;
    private InterfaceAcceuil fenetreParente; // Pour pouvoir rafraîchir

    private JComboBox<Utilisateur> comboUtilisateurs;
    private JTextField txtTitre;
    private JTextArea txtDescription;
    private JTextField txtDate;
    private JTextField txtLieu;
    private JLabel lblErreur; // Pour afficher les messages rouges du G1

    public InterfaceCreation(InterfaceAcceuil parent, IncidentService incidentService, UtilisateurService utilisateurService) {
        super(parent, "Déclarer un nouvel incident", true);
        //bloque la fenetre d'acceuil
        this.fenetreParente = parent;
        this.incidentService = incidentService;
        this.utilisateurService = utilisateurService;

        configurerFenetre();
        initialiserComposants();
    }

    private void configurerFenetre() {
        setSize(500, 450);
        setLocationRelativeTo(fenetreParente);
        setLayout(new BorderLayout());
    }

    private void initialiserComposants() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 15)); // 6 lignes, 2 colonnes, marges
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Demandeur :"));
        comboUtilisateurs = new JComboBox<>();
        List<Utilisateur> users = utilisateurService.obtenirTousLesUtilisateurs();
        for (Utilisateur u : users) {
            comboUtilisateurs.addItem(u);//liste les utilisateurs present en base
        }

        comboUtilisateurs.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Utilisateur) {
                    setText(((Utilisateur) value).getNom());
                }
                return this;
            }
        });
        formPanel.add(comboUtilisateurs);

        //titre de l'incident
        formPanel.add(new JLabel("Titre de la panne :"));
        txtTitre = new JTextField();
        formPanel.add(txtTitre);

        //Sa date
        formPanel.add(new JLabel("Date (JJ/MM/AAAA) :"));
        txtDate = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))); // Date du jour par défaut
        formPanel.add(txtDate);

        //lieu
        formPanel.add(new JLabel("Localisation :"));
        txtLieu = new JTextField();
        formPanel.add(txtLieu);

        //description
        formPanel.add(new JLabel("Description :"));
        txtDescription = new JTextArea();
        txtDescription.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        formPanel.add(new JScrollPane(txtDescription));//permettre de scorller si beaucoup de texte

        add(formPanel, BorderLayout.CENTER);


        lblErreur = new JLabel(" "); //Label d'erreur vide par default
        lblErreur.setForeground(Color.RED);
        lblErreur.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblErreur, BorderLayout.NORTH);

        //bouttons du bas pour annuler ou envoyer le ticket
        JPanel bottomPanel = new JPanel();
        JButton btnAnnuler = new JButton("Annuler");
        btnAnnuler.addActionListener(e -> dispose()); // Ferme la fenêtre

        JButton btnValider = new JButton("Soumettre le ticket");
        btnValider.addActionListener(e -> validerFormulaire());

        //met les boutons
        bottomPanel.add(btnAnnuler);
        bottomPanel.add(btnValider);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void validerFormulaire() {
        try {
            //récupération données
            Utilisateur createur = (Utilisateur) comboUtilisateurs.getSelectedItem();
            String titre = txtTitre.getText();
            String description = txtDescription.getText();
            String lieu = txtLieu.getText();
            LocalDate date;
            try {
                date = LocalDate.parse(txtDate.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                if (date.isAfter(LocalDate.now())) {
                    lblErreur.setText("Erreur : La date doit etre antérieure ou égale à aujourd'hui");
                    return;
                }
                if (date.isBefore(LocalDate.now().minusMonths(2))) {
                    lblErreur.setText("Erreur : la date est trop ancienne pour être prise en compte");
                    return;
                }
            } catch (DateTimeParseException ex) {
                lblErreur.setText("Erreur : Le format de la date doit être JJ/MM/AAAA");
                return;
            }

            if (titre.trim().isEmpty()) {
                lblErreur.setText("Erreur : Le titre est obligatoire.");
                return;
            }
            if (lieu.trim().isEmpty()) {
                lblErreur.setText("Erreur : La localisation est obligatoire.");
                return;
            }
            if (description.trim().isEmpty()) {
                lblErreur.setText("Erreur : Veuillez décrire le problème.");
                return;
            }

            //creer le ticket avec les infos
            incidentService.creerTicket(titre, description, createur, date, lieu);
            fenetreParente.rafraichirTableau();//Maj du tdb
            dispose();//fermeture
            JOptionPane.showMessageDialog(fenetreParente, "Ticket créé avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            lblErreur.setText("Erreur : " + ex.getMessage());
        } catch (Exception ex) {
            lblErreur.setText("Une erreur inattendue est survenue.");
        }
    }
}