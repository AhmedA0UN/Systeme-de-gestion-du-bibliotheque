package view;

import controller.AdherentController;
import controller.DocumentController;
import controller.PretController;
import model.Adherent;
import model.Document;
import model.Pret;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Vue : Gestion des prêts (espace bibliothécaire).
 * Permet d'enregistrer un nouveau prêt et de visualiser les prêts en cours.
 */
public class GestionPretsView extends JFrame {

    private DefaultTableModel tableModel;
    private JComboBox<String> cmbAdherents, cmbDocuments;
    private JButton btnAjouter, btnSupprimer, btnEnregistrer, btnFermer, btnActualiser;
    private final int idBibliothecaire;

    private PretController     pretController;
    private AdherentController adherentController;
    private DocumentController docController;

    private List<Adherent>  listeAdherents;
    private List<Document>  listeDocuments;

    public GestionPretsView(int idBibliothecaire) {
        this.idBibliothecaire = idBibliothecaire;
        try {
            pretController     = new PretController();
            adherentController = new AdherentController();
            docController      = new DocumentController();
            listeAdherents     = adherentController.getTousAdherents();
            listeDocuments     = docController.getDocumentsDisponibles();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage());
        }
        initUI();
        chargerTable();
    }

    private void initUI() {
        setTitle("Gestion des Prêts");
        setSize(950, 560);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Formulaire nouveau prêt
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Nouveau Prêt"));
        form.setPreferredSize(new Dimension(320, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;

        cmbAdherents = new JComboBox<>();
        cmbDocuments = new JComboBox<>();

        for (Adherent a : listeAdherents)
            cmbAdherents.addItem("[" + a.getIdAdherent() + "] " + a.getNomComplet());
        for (Document d : listeDocuments)
            cmbDocuments.addItem("[" + d.getId() + "] " + d.getTitre());

        g.gridx=0; g.gridy=0; g.weightx=0.35;
        form.add(new JLabel("Adhérent :"), g);
        g.gridx=1; g.weightx=0.65;
        form.add(cmbAdherents, g);

        g.gridx=0; g.gridy=1; g.weightx=0.35;
        form.add(new JLabel("Document :"), g);
        g.gridx=1; g.weightx=0.65;
        form.add(cmbDocuments, g);

        JLabel lblInfo = new JLabel("<html><i>Durée : 14 jours à partir d'aujourd'hui</i></html>");
        lblInfo.setForeground(new Color(80, 80, 80));
        g.gridx=0; g.gridy=2; g.gridwidth=2;
        form.add(lblInfo, g);

        // ---- Table des prêts en cours
        JPanel right = new JPanel(new BorderLayout(6, 6));
        String[] cols = {"ID","Adhérent","Document","Emprunt","Retour prévu","Statut"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(45);

        // ---- Boutons (5 du sujet)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btnPanel.setBorder(BorderFactory.createEtchedBorder());
        btnAjouter    = creerBtn("Ajouter",     new Color(30, 130, 60));
        btnSupprimer  = creerBtn("Supprimer",   new Color(200, 50, 50));
        btnEnregistrer= creerBtn("Enregistrer", new Color(150, 100, 0));
        btnActualiser = creerBtn("Actualiser",  new Color(30, 100, 200));
        btnFermer     = creerBtn("Fermer",      Color.DARK_GRAY);
        btnPanel.add(btnAjouter); btnPanel.add(btnSupprimer);
        btnPanel.add(btnEnregistrer); btnPanel.add(btnActualiser); btnPanel.add(btnFermer);

        right.add(new JScrollPane(table), BorderLayout.CENTER);
        right.add(btnPanel,               BorderLayout.SOUTH);

        main.add(form,  BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        add(main);

        btnAjouter.addActionListener(e    -> creerPret());
        btnSupprimer.addActionListener(e  -> { JOptionPane.showMessageDialog(this, "Suppression désactivée pour les prêts en cours."); });
        btnEnregistrer.addActionListener(e-> chargerTable());
        btnActualiser.addActionListener(e -> chargerTable());
        btnFermer.addActionListener(e     -> dispose());
    }

    private void chargerTable() {
        tableModel.setRowCount(0);
        try {
            List<Pret> prets = pretController.getPretsEnCours();
            for (Pret p : prets)
                tableModel.addRow(new Object[]{
                    p.getId(), p.getNomAdherent(), p.getTitreDocument(),
                    p.getDateEmprunt(), p.getDateRetourPrevue(), p.getStatut()});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void creerPret() {
        int idxAdh = cmbAdherents.getSelectedIndex();
        int idxDoc = cmbDocuments.getSelectedIndex();
        if (idxAdh < 0 || idxDoc < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un adhérent et un document.");
            return;
        }
        Adherent a = listeAdherents.get(idxAdh);
        Document d = listeDocuments.get(idxDoc);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer le prêt de\n« " + d.getTitre() + " »\nà " + a.getNomComplet() + " ?",
            "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Pret pret = pretController.emprunterDocument(a.getIdAdherent(), d.getId(), idBibliothecaire);
            JOptionPane.showMessageDialog(this,
                "Prêt enregistré !\nRetour prévu le : " + pret.getDateRetourPrevue(),
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            // Rafraîchir la liste des documents disponibles
            listeDocuments = docController.getDocumentsDisponibles();
            cmbDocuments.removeAllItems();
            for (Document doc : listeDocuments)
                cmbDocuments.addItem("[" + doc.getId() + "] " + doc.getTitre());
            chargerTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton creerBtn(String label, Color c) {
        JButton b = new JButton(label);
        b.setBackground(c); b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(115, 32));
        return b;
    }
}
