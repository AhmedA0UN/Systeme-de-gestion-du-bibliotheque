package view;

import controller.DocumentController;
import model.Document;
import model.Document.TypeDocument;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface de gestion des documents.
 * Contient : liste des documents + 5 boutons (Ajouter, Modifier, Supprimer, Enregistrer, Fermer).
 * Architecture MVC : cette classe est la VUE.
 */
public class GestionDocumentsView extends JFrame {

    // Modèle de table (liste en mémoire)
    private DefaultTableModel tableModel;
    private JTable            table;

    // Champs de saisie
    private JTextField  txtTitre, txtAuteur, txtEditeur, txtAnnee, txtIsbn, txtCategorie;
    private JTextField  txtExemplaires, txtDisponibles;
    private JComboBox<TypeDocument> cmbType;
    private JTextArea   txtDescription;

    // Boutons requis par le sujet
    private JButton btnAjouter, btnModifier, btnSupprimer, btnEnregistrer, btnFermer;

    // Champ de recherche
    private JTextField txtRecherche;
    private JButton    btnRechercher;

    private DocumentController controller;
    private final int idBibliothecaire;

    // Données en mémoire (liste locale avant "Enregistrer")
    private List<Document> documents;

    public GestionDocumentsView(int idBibliothecaire) {
        this.idBibliothecaire = idBibliothecaire;
        try {
            controller = new DocumentController();
            documents  = controller.getTousDocuments();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage());
        }
        initUI();
        chargerTable(documents);
    }

    private void initUI() {
        setTitle("Gestion des Documents");
        setSize(1050, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ================================================================
        // PANNEAU GAUCHE : Formulaire de saisie
        // ================================================================
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations du Document"));
        formPanel.setPreferredSize(new Dimension(320, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        txtTitre       = new JTextField(18);
        txtAuteur      = new JTextField(18);
        txtEditeur     = new JTextField(18);
        txtAnnee       = new JTextField(6);
        txtIsbn        = new JTextField(18);
        txtCategorie   = new JTextField(18);
        txtExemplaires = new JTextField(5);
        txtDisponibles = new JTextField(5);
        cmbType        = new JComboBox<>(TypeDocument.values());
        txtDescription = new JTextArea(3, 18);
        txtDescription.setLineWrap(true);

        String[] labels = {"Titre *", "Auteur", "Éditeur", "Année", "ISBN",
                           "Catégorie", "Type", "Exemplaires *", "Disponibles *", "Description"};
        Component[] champs = {txtTitre, txtAuteur, txtEditeur, txtAnnee, txtIsbn,
                              txtCategorie, cmbType, txtExemplaires, txtDisponibles,
                              new JScrollPane(txtDescription)};
        for (int i = 0; i < labels.length; i++) {
            g.gridx = 0; g.gridy = i; g.weightx = 0.35;
            formPanel.add(new JLabel(labels[i] + " :"), g);
            g.gridx = 1; g.weightx = 0.65;
            formPanel.add(champs[i], g);
        }

        // ================================================================
        // PANNEAU DROIT : Table + recherche + boutons
        // ================================================================
        JPanel rightPanel = new JPanel(new BorderLayout(6, 6));

        // --- Barre de recherche
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        searchBar.setBorder(BorderFactory.createTitledBorder("Recherche"));
        txtRecherche = new JTextField(20);
        btnRechercher = new JButton("Rechercher");
        JButton btnTous = new JButton("Tous");
        searchBar.add(new JLabel("Titre :"));
        searchBar.add(txtRecherche);
        searchBar.add(btnRechercher);
        searchBar.add(btnTous);

        // --- Table
        String[] colonnes = {"ID", "Titre", "Auteur", "Éditeur", "Année", "Catégorie", "Type", "Expl.", "Dispo."};
        tableModel = new DefaultTableModel(colonnes, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(7).setMaxWidth(50);
        table.getColumnModel().getColumn(8).setMaxWidth(50);
        table.getSelectionModel().addListSelectionListener(e -> remplirFormulaire());
        JScrollPane scroll = new JScrollPane(table);

        // --- Barre de boutons (les 5 requis par le sujet)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btnPanel.setBorder(BorderFactory.createEtchedBorder());
        btnAjouter    = creerBtn("Ajouter",     new Color(30, 130, 60));
        btnModifier   = creerBtn("Modifier",    new Color(30, 100, 200));
        btnSupprimer  = creerBtn("Supprimer",   new Color(200, 50, 50));
        btnEnregistrer= creerBtn("Enregistrer", new Color(150, 100, 0));
        btnFermer     = creerBtn("Fermer",      Color.DARK_GRAY);
        btnPanel.add(btnAjouter);
        btnPanel.add(btnModifier);
        btnPanel.add(btnSupprimer);
        btnPanel.add(btnEnregistrer);
        btnPanel.add(btnFermer);

        rightPanel.add(searchBar, BorderLayout.NORTH);
        rightPanel.add(scroll,    BorderLayout.CENTER);
        rightPanel.add(btnPanel,  BorderLayout.SOUTH);

        main.add(formPanel, BorderLayout.WEST);
        main.add(rightPanel, BorderLayout.CENTER);
        add(main);

        // ================================================================
        // LISTENERS
        // ================================================================
        btnAjouter.addActionListener(e    -> ajouterDocument());
        btnModifier.addActionListener(e   -> modifierDocument());
        btnSupprimer.addActionListener(e  -> supprimerDocument());
        btnEnregistrer.addActionListener(e-> enregistrerEnBD());
        btnFermer.addActionListener(e     -> dispose());
        btnRechercher.addActionListener(e -> rechercherDocuments());
        btnTous.addActionListener(e -> {
            txtRecherche.setText("");
            chargerTable(documents);
        });
    }

    // ================================================================
    // Chargement et affichage de la table
    // ================================================================
    private void chargerTable(List<Document> liste) {
        tableModel.setRowCount(0);
        for (Document d : liste) {
            tableModel.addRow(new Object[]{
                d.getId(), d.getTitre(), d.getAuteur(), d.getEditeur(),
                d.getAnneeEdition(), d.getCategorie(), d.getTypeDocument(),
                d.getNombreExemplaires(), d.getDisponibles()
            });
        }
    }

    private void remplirFormulaire() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        Document d = documents.stream().filter(x -> x.getId() == id).findFirst().orElse(null);
        if (d == null) return;
        txtTitre.setText(d.getTitre());
        txtAuteur.setText(d.getAuteur());
        txtEditeur.setText(d.getEditeur());
        txtAnnee.setText(String.valueOf(d.getAnneeEdition()));
        txtIsbn.setText(d.getIsbn());
        txtCategorie.setText(d.getCategorie());
        cmbType.setSelectedItem(d.getTypeDocument());
        txtExemplaires.setText(String.valueOf(d.getNombreExemplaires()));
        txtDisponibles.setText(String.valueOf(d.getDisponibles()));
        txtDescription.setText(d.getDescription());
    }

    // ================================================================
    // Actions des boutons
    // ================================================================

    /** Ajoute un document dans la liste locale (pas encore en BD) */
    private void ajouterDocument() {
        Document d = lireFormulaire();
        if (d == null) return;
        d.setId(-(documents.size() + 1)); // ID temporaire négatif
        documents.add(d);
        chargerTable(documents);
        viderFormulaire();
        JOptionPane.showMessageDialog(this,
            "Document ajouté à la liste. Cliquez sur Enregistrer pour sauvegarder en BD.",
            "Ajout", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Modifie le document sélectionné dans la liste locale */
    private void modifierDocument() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un document."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Document d = lireFormulaire();
        if (d == null) return;
        d.setId(id);
        // Remplacer dans la liste
        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getId() == id) { documents.set(i, d); break; }
        }
        chargerTable(documents);
        viderFormulaire();
    }

    /** Supprime le document sélectionné de la liste locale */
    private void supprimerDocument() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un document."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer la suppression ?", "Suppression", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        int id = (int) tableModel.getValueAt(row, 0);
        documents.removeIf(d -> d.getId() == id);
        chargerTable(documents);
        viderFormulaire();
    }

    /** Enregistre toutes les modifications dans la base de données */
    private void enregistrerEnBD() {
        int erreurs = 0;
        for (Document d : documents) {
            try {
                if (d.getId() <= 0) {
                    // Nouveau document (ID temporaire négatif)
                    d.setId(0);
                    controller.ajouterDocument(d);
                } else {
                    controller.modifierDocument(d);
                }
            } catch (Exception e) {
                erreurs++;
                System.err.println("Erreur enregistrement document : " + e.getMessage());
            }
        }
        try {
            documents = controller.getTousDocuments();
            chargerTable(documents);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur rechargement : " + e.getMessage());
        }
        if (erreurs == 0)
            JOptionPane.showMessageDialog(this, "Données enregistrées avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, erreurs + " erreur(s) lors de l'enregistrement.", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void rechercherDocuments() {
        String q = txtRecherche.getText().trim();
        if (q.isEmpty()) { chargerTable(documents); return; }
        try {
            chargerTable(controller.rechercherParTitre(q));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    // ================================================================
    // Utilitaires formulaire
    // ================================================================
    private Document lireFormulaire() {
        if (txtTitre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Le titre est obligatoire.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Document d = new Document();
        d.setTitre(txtTitre.getText().trim());
        d.setAuteur(txtAuteur.getText().trim());
        d.setEditeur(txtEditeur.getText().trim());
        try { d.setAnneeEdition(Integer.parseInt(txtAnnee.getText().trim())); } catch (NumberFormatException ex) { d.setAnneeEdition(0); }
        d.setIsbn(txtIsbn.getText().trim());
        d.setCategorie(txtCategorie.getText().trim());
        d.setTypeDocument((TypeDocument) cmbType.getSelectedItem());
        try { d.setNombreExemplaires(Integer.parseInt(txtExemplaires.getText().trim())); } catch (NumberFormatException ex) { d.setNombreExemplaires(1); }
        try { d.setDisponibles(Integer.parseInt(txtDisponibles.getText().trim())); } catch (NumberFormatException ex) { d.setDisponibles(1); }
        d.setDescription(txtDescription.getText().trim());
        return d;
    }

    private void viderFormulaire() {
        txtTitre.setText(""); txtAuteur.setText(""); txtEditeur.setText("");
        txtAnnee.setText(""); txtIsbn.setText(""); txtCategorie.setText("");
        txtExemplaires.setText(""); txtDisponibles.setText("");
        txtDescription.setText("");
        cmbType.setSelectedIndex(0);
        table.clearSelection();
    }

    private JButton creerBtn(String label, Color couleur) {
        JButton b = new JButton(label);
        b.setBackground(couleur);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(120, 32));
        return b;
    }
}
