package view;

import controller.DocumentController;
import controller.PretController;
import model.Document;
import model.Pret;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Vue : Emprunter un document (espace adhérent).
 * Affiche uniquement les documents disponibles.
 */
public class EmprunterDocumentView extends JFrame {

    private DefaultTableModel tableModel;
    private JTable            table;
    private final int         idAdherent;

    private DocumentController docController;
    private PretController     pretController;

    public EmprunterDocumentView(int idAdherent) {
        this.idAdherent = idAdherent;
        try {
            docController  = new DocumentController();
            pretController = new PretController();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage());
        }
        initUI();
        chargerDocumentsDisponibles();
    }

    private void initUI() {
        setTitle("Emprunter un Document");
        setSize(780, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(6, 6));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblInfo = new JLabel(
            "Sélectionnez un document disponible et cliquez sur «Emprunter».",
            SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInfo.setForeground(new Color(80, 80, 80));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        String[] cols = {"ID","Titre","Auteur","Catégorie","Type","Disponibles"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(5).setMaxWidth(85);

        JButton btnEmprunter = new JButton("  Emprunter  ");
        btnEmprunter.setBackground(new Color(180, 100, 20));
        btnEmprunter.setForeground(Color.WHITE);
        btnEmprunter.setFont(new Font("Arial", Font.BOLD, 13));
        JButton btnFermer = new JButton("Fermer");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        south.add(btnEmprunter);
        south.add(btnFermer);

        main.add(lblInfo,                   BorderLayout.NORTH);
        main.add(new JScrollPane(table),    BorderLayout.CENTER);
        main.add(south,                     BorderLayout.SOUTH);
        add(main);

        btnEmprunter.addActionListener(e -> emprunter());
        btnFermer.addActionListener(e -> dispose());
    }

    private void chargerDocumentsDisponibles() {
        tableModel.setRowCount(0);
        try {
            List<Document> liste = docController.getDocumentsDisponibles();
            for (Document d : liste)
                tableModel.addRow(new Object[]{d.getId(), d.getTitre(), d.getAuteur(),
                    d.getCategorie(), d.getTypeDocument(), d.getDisponibles()});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void emprunter() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un document.", "Sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int    idDoc  = (int)    tableModel.getValueAt(row, 0);
        String titre  = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer l'emprunt de :\n« " + titre + " » ?\n\nDurée du prêt : 14 jours",
            "Confirmer l'emprunt", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // idBibliothecaire = 0 car l'adhérent emprunte en autonomie
            Pret pret = pretController.emprunterDocument(idAdherent, idDoc, 1);
            JOptionPane.showMessageDialog(this,
                "Emprunt enregistré avec succès !\n" +
                "Date de retour prévue : " + pret.getDateRetourPrevue(),
                "Emprunt réussi", JOptionPane.INFORMATION_MESSAGE);
            chargerDocumentsDisponibles();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Accès refusé", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur BD : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
