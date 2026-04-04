package view;

import controller.DocumentController;
import model.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Vue : Consulter la liste des documents (espace adhérent).
 */
public class ConsulterDocumentsView extends JFrame {

    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextField        txtRecherche;
    private DocumentController controller;

    public ConsulterDocumentsView(int idAdherent) {
        try { controller = new DocumentController(); }
        catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage()); }
        initUI();
        charger(null);
    }

    private void initUI() {
        setTitle("Liste des Documents");
        setSize(820, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(6, 6));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Barre recherche
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        top.setBorder(BorderFactory.createTitledBorder("Recherche par titre"));
        txtRecherche = new JTextField(22);
        JButton btnChercher = new JButton("Chercher");
        JButton btnTous     = new JButton("Tous");
        top.add(txtRecherche); top.add(btnChercher); top.add(btnTous);

        // Table
        String[] cols = {"ID","Titre","Auteur","Éditeur","Année","Catégorie","Type","Disponibles"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getColumnModel().getColumn(7).setMaxWidth(80);

        // Détail en bas
        JTextArea txtDetail = new JTextArea(4, 40);
        txtDetail.setEditable(false);
        txtDetail.setFont(new Font("Arial", Font.PLAIN, 12));
        txtDetail.setBorder(BorderFactory.createTitledBorder("Détail du document sélectionné"));
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;
            int id = (int) tableModel.getValueAt(row, 0);
            try {
                Document d = controller.getDocumentById(id);
                if (d != null)
                    txtDetail.setText("Titre : " + d.getTitre()
                        + "\nAuteur : " + d.getAuteur()
                        + "\nDescription : " + (d.getDescription() != null ? d.getDescription() : "-")
                        + "\nISBN : " + d.getIsbn()
                        + "\nExemplaires : " + d.getNombreExemplaires()
                        + "  |  Disponibles : " + d.getDisponibles());
            } catch (SQLException ex) { txtDetail.setText("Erreur : " + ex.getMessage()); }
        });

        JButton btnFermer = new JButton("Fermer");
        btnFermer.addActionListener(e -> dispose());
        JPanel south = new JPanel(new BorderLayout());
        south.add(new JScrollPane(txtDetail), BorderLayout.CENTER);
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnP.add(btnFermer);
        south.add(btnP, BorderLayout.SOUTH);

        main.add(top, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        add(main);

        btnChercher.addActionListener(e -> charger(txtRecherche.getText().trim()));
        btnTous.addActionListener(e -> { txtRecherche.setText(""); charger(null); });
    }

    private void charger(String filtre) {
        tableModel.setRowCount(0);
        try {
            List<Document> liste = (filtre == null || filtre.isEmpty())
                ? controller.getTousDocuments()
                : controller.rechercherParTitre(filtre);
            for (Document d : liste)
                tableModel.addRow(new Object[]{d.getId(), d.getTitre(), d.getAuteur(),
                    d.getEditeur(), d.getAnneeEdition(), d.getCategorie(),
                    d.getTypeDocument(), d.getDisponibles()});
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}
