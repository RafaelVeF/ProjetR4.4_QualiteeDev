//Import des dependances necessaires

import gestionincidents.model.Ticket;
import gestionincidents.service.IncidentService;
import gestionincidents.UtilisateurService;
//Changer les dependances si necessaire

//Installation de swing pour l'interface
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;



//classe d'acceuil
public class InterfaceAcceuil extends JFrame{



    private IncidentService incidentService;
    private UtilisateurService utilisateurService;
    private JTable ticketTable;
    private DefaultTableModel tableModel;



    //Classe qui servira à lancer l'interface d'acceuil
    public InterfaceAcceuil(IncidentService incidentService,UtilisateurService utilisateurService){
        this.incidentService = incidentService;
        this.utilisateurService = utilisateurService;

        configurerFenetre();
        initialiserComposants();
        rafraichirTableau();
    }



    //sert à configurer la fenetre d'acceuil de manière générale
    private void configurerFenetre(){
        setTitle("Gestionnaire de ticket d'incidents");
        setSize(800,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);//position centré
        setLayout(new BorderLayout());//disposition
    }



    //alimente la fenetre avec composants
    private void initialiserComposants(){
        JPanel topPanel = new JPanel(new BorderLayout());
        //Le panel du haut

        topPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        JLabel titreLabel = new JLabel("Liste des Incidents");
        titreLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titreLabel, BorderLayout.WEST);

        //recherche de ticket
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Aligné à droite
        JLabel lblRecherche = new JLabel("Rechercher : ");
        JTextField txtRecherche = new JTextField(15);
        searchPanel.add(lblRecherche);
        searchPanel.add(txtRecherche);

        //creation de ticket
        JButton btnNouveau = new JButton("+ Créer un ticket");
        btnNouveau.addActionListener(e -> {
            InterfaceCreation popupCreation = new InterfaceCreation(this, incidentService, utilisateurService);
            popupCreation.setVisible(true);
        });
        searchPanel.add(btnNouveau);

        topPanel.add(searchPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);



        //Tableau des tickets -----------

        //Definition du tableau des tickets
        String[] colonnes = {"ID","Titre","Status","Demandeur","Date","Lieu"};
        tableModel = new DefaultTableModel(colonnes, 0){
            @Override
            public boolean isCellEditable(int ligne, int colonne){
                return false;
            }
        };

        ticketTable= new JTable(tableModel);
        ticketTable.setRowHeight(25);//taille des lignes
        JScrollPane scrollPane = new JScrollPane(ticketTable);//ajout du tableau pour l'interface de scolling
        add(scrollPane,BorderLayout.CENTER);
        //ajoute une barre de scroll


        //filtre pour la recherche
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        ticketTable.setRowSorter(sorter);

        txtRecherche.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filtrer() {
                String texte = txtRecherche.getText();
                if (texte.trim().isEmpty()){
                    sorter.setRowFilter(null); //affiche tout si vide
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texte));//casse avec ?i
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrer(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrer(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrer(); }
        });




        JPanel bottomPanel = new JPanel();
        JButton btnModifier = new JButton("Gerer le ticket");
        JButton btnSupprimer = new JButton("Supprimer le ticket");
        bottomPanel.add(btnModifier);
        bottomPanel.add(btnSupprimer);
        //desactivé de base
        btnModifier.setEnabled(false);
        btnSupprimer.setEnabled(false);

        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            boolean estSelectionne = ticketTable.getSelectedRow() != -1;//si une ligne est selectionn
            btnModifier.setEnabled(estSelectionne);//reactive les boutons
            btnSupprimer.setEnabled(estSelectionne);
        });

        //comportement pour le clic sur modification
        btnModifier.addActionListener(e -> ouvrirFenetreModification());
        btnSupprimer.addActionListener(e -> supprimerTicket());
        add(bottomPanel,BorderLayout.SOUTH);

    }



    //rafraichit le tableau et le remplir avec les dernieres
    //donnés
    protected void rafraichirTableau(){
        tableModel.setRowCount(0);//vide les lignes

        List<Ticket> tickets = incidentService.obtenirTousLesTickets();

        for(Ticket t : tickets){
            Object[] ligne = {
                    "-" + t.getId(),
                    t.getTitre(),
                    t.getStatut().name(),
                    t.getCreateur().getNom(),
                    t.getDate(),
                    t.getLocation(),
            };
            tableModel.addRow(ligne);
        }

    }


    //ouvrir le tableau de modif
    private void ouvrirFenetreModification() {
        int ligneSelectionnee = ticketTable.getSelectedRow();//ligne cible
        if (ligneSelectionnee != -1) {
            String idTexte = (String) tableModel.getValueAt(ligneSelectionnee, 0);
            Long ticketId = Long.parseLong(idTexte.substring(1));//enleve le premier caractère -

            Ticket ticket = incidentService.obtenirTicket(ticketId);

            if (ticket != null) {
                //ouvre l'interface de mofif si ticket existe bien
                InterfaceModification popup = new InterfaceModification(this, incidentService, ticket);
                popup.setVisible(true);
            }
        }
    }

    private void supprimerTicket(){
        int ligneSelectionnee = ticketTable.getSelectedRow();

        if (ligneSelectionnee != -1) {
            String idTexte = (String) tableModel.getValueAt(ligneSelectionnee, 0);
            Long ticketId = Long.parseLong(idTexte.substring(1)); // On enlève le "-" ou "#"

            int choix = JOptionPane.showConfirmDialog(
                    this,
                    "Êtes-vous sûr de vouloir supprimer définitivement le ticket #" + ticketId + " ?",
                    "Confirmation de suppression",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choix == JOptionPane.YES_OPTION) {
                try {
                    incidentService.supprimerTicket(ticketId);
                    rafraichirTableau();
                    JOptionPane.showMessageDialog(this, "Ticket supprimé avec succès.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
