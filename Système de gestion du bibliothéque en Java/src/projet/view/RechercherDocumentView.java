package view;

import controller.DocumentController;
import model.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Vue : Rechercher un document (espace adhérent).
 * Permet la recherche par titre ET par auteur.
 */
public class RechercherDocumentView extends JFrame {

    private DefaultTableModel tableModel;
    private JTextField txtTitre, txtAuteur;
    private DocumentController controller;

    public RechercherDocumentView(int idAdherent) {
        try { controller = new DocumentController(); }
        catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage()); }
        initUI();
    }

    private void initUI() {
        setTitle("Rechercher un Document");
        setSize(780, 460);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(6, 6));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulaire de recherche
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Critères de recherche"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        txtTitre  = new JTextField(20);
        txtAuteur = new JTextField(20);
        JButton btnChercher = new JButton("Rechercher");
        JButton btnReset    = new JButton("Réinitialiser");
        btnChercher.setBackground(new Color(30, 100, 200));
        btnChercher.setForeground(Color.WHITE);

        g.gridx=0; g.gridy=0; form.add(new JLabel("Titre :"), g);
        g.gridx=1; form.add(txtTitre, g);
        g.gridx=2; form.add(btnChercher, g);
        g.gridx=0; g.gridy=1; form.add(new JLabel("Auteur :"), g);
        g.gridx=1; form.add(txtAuteur, g);
        g.gridx=2; form.add(btnReset, g);

        // Table résultats
        String[] cols = {"ID","Titre","Auteur","Éditeur","Année","Type","Disponibles"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setMaxWidth(45);

        JButton btnFermer = new JButton("Fermer");
        btnFermer.addActionListener(e -> dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnFermer);

        main.add(form, BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(south, BorderLayout.SOUTH);
        add(main);

        btnChercher.addActionListener(e -> rechercher());
        btnReset.addActionListener(e -> { txtTitre.setText(""); txtAuteur.setText(""); tableModel.setRowCount(0); });
    }

    private void rechercher() {
        String titre  = txtTitre.getText().trim();
        String auteur = txtAuteur.getText().trim();
        try {
            List<Document> liste;
            if (!titre.isEmpty())
                liste = controller.rechercherParTitre(titre);
            else if (!auteur.isEmpty())
                liste = controller.rechercherParAuteur(auteur);
            else
                liste = controller.getTousDocuments();

            tableModel.setRowCount(0);
            for (Document d : liste)
                tableModel.addRow(new Object[]{d.getId(), d.getTitre(), d.getAuteur(),
                    d.getEditeur(), d.getAnneeEdition(), d.getTypeDocument(), d.getDisponibles()});

            if (liste.isEmpty())
                JOptionPane.showMessageDialog(this, "Aucun document trouvé.", "Résultat", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }
}
