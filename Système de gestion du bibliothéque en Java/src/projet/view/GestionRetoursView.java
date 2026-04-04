package view;

import controller.PretController;
import model.Pret;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Vue : Gestion des retours (espace bibliothécaire).
 * Affiche les prêts en cours, permet d'enregistrer un retour.
 */
public class GestionRetoursView extends JFrame {

    private DefaultTableModel tableModel;
    private JTable            table;
    private JTextArea         txtRemarque;
    private JButton           btnRetour, btnFermer, btnActualiser;
    private final int         idBibliothecaire;
    private PretController    pretController;

    public GestionRetoursView(int idBibliothecaire) {
        this.idBibliothecaire = idBibliothecaire;
        try { pretController = new PretController(); }
        catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage()); }
        initUI();
        chargerTable();
    }

    private void initUI() {
        setTitle("Gestion des Retours");
        setSize(880, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Légende couleurs
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 3));
        legend.setBorder(BorderFactory.createTitledBorder("Légende"));
        JLabel lblNormal  = new JLabel("■ En cours (dans les délais)");
        lblNormal.setForeground(new Color(30, 130, 60));
        JLabel lblRetard  = new JLabel("■ En retard");
        lblRetard.setForeground(new Color(200, 50, 50));
        legend.add(lblNormal); legend.add(lblRetard);

        // Table prêts en cours
        String[] cols = {"ID Prêt","Adhérent","Document","Date emprunt","Retour prévu","Statut"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                Object statut = getValueAt(row, 5);
                if ("EN_RETARD".equals(statut.toString()) || "EN_RETARD".equals(String.valueOf(statut)))
                    c.setForeground(new Color(200, 50, 50));
                else
                    c.setForeground(table.getForeground());
                return c;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(65);

        // Panel remarque + bouton
        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.setBorder(BorderFactory.createTitledBorder("Enregistrer le retour"));

        txtRemarque = new JTextArea(3, 40);
        txtRemarque.setLineWrap(true);
        txtRemarque.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        JLabel lblRem = new JLabel("Remarque (optionnel) :");
        JPanel remPanel = new JPanel(new BorderLayout(4, 4));
        remPanel.add(lblRem, BorderLayout.NORTH);
        remPanel.add(new JScrollPane(txtRemarque), BorderLayout.CENTER);

        btnRetour    = new JButton("  ✔  Enregistrer le Retour  ");
        btnActualiser= new JButton("Actualiser");
        btnFermer    = new JButton("Fermer");
        btnRetour.setBackground(new Color(30, 130, 60));
        btnRetour.setForeground(Color.WHITE);
        btnRetour.setFont(new Font("Arial", Font.BOLD, 13));
        btnActualiser.setBackground(new Color(30, 100, 200));
        btnActualiser.setForeground(Color.WHITE);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnRow.add(btnActualiser); btnRow.add(btnRetour); btnRow.add(btnFermer);

        south.add(remPanel, BorderLayout.CENTER);
        south.add(btnRow,   BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(legend, BorderLayout.NORTH);

        main.add(topPanel,               BorderLayout.NORTH);
        main.add(new JScrollPane(table), BorderLayout.CENTER);
        main.add(south,                  BorderLayout.SOUTH);
        add(main);

        btnRetour.addActionListener(e    -> enregistrerRetour());
        btnActualiser.addActionListener(e-> chargerTable());
        btnFermer.addActionListener(e    -> dispose());
    }

    private void chargerTable() {
        tableModel.setRowCount(0);
        try {
            List<Pret> prets = pretController.getPretsEnCours();
            LocalDate today  = LocalDate.now();
            for (Pret p : prets) {
                String statut = p.getDateRetourPrevue().isBefore(today) ? "EN_RETARD" : "EN_COURS";
                tableModel.addRow(new Object[]{
                    p.getId(), p.getNomAdherent(), p.getTitreDocument(),
                    p.getDateEmprunt(), p.getDateRetourPrevue(), statut});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
        }
    }

    private void enregistrerRetour() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez un prêt dans la liste.", "Sélection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int    idPret   = (int) tableModel.getValueAt(row, 0);
        String document = (String) tableModel.getValueAt(row, 2);
        String adherent = (String) tableModel.getValueAt(row, 1);
        String remarque = txtRemarque.getText().trim();

        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirmer le retour de :\n« " + document + " »\npar " + adherent + " ?",
            "Confirmer le retour", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            boolean ok = pretController.enregistrerRetour(idPret, remarque.isEmpty() ? null : remarque);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Retour enregistré avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                txtRemarque.setText("");
                chargerTable();
            } else {
                JOptionPane.showMessageDialog(this, "Le retour n'a pas pu être enregistré (déjà rendu ?).", "Erreur", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erreur BD : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
