package view;

import controller.AdherentController;
import model.Adherent;
import model.Adherent.Statut;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Vue : Gestion des adhérents (espace bibliothécaire).
 * CRUD complet avec les 5 boutons du sujet.
 */
public class GestionAdherentsView extends JFrame {

    private DefaultTableModel tableModel;
    private JTable            table;

    private JTextField txtNom, txtPrenom, txtEmail, txtTel, txtAdresse;
    private JTextField txtLogin, txtMdp, txtCarte, txtDateInscr, txtDateExp;
    private JComboBox<Statut> cmbStatut;

    private JButton btnAjouter, btnModifier, btnSupprimer, btnEnregistrer, btnFermer;

    private AdherentController controller;
    private List<Adherent>     adherents;

    public GestionAdherentsView() {
        try {
            controller = new AdherentController();
            adherents  = controller.getTousAdherents();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erreur BD : " + e.getMessage());
        }
        initUI();
        chargerTable();
    }

    private void initUI() {
        setTitle("Gestion des Adhérents");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(8, 8));
        main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Informations de l'Adhérent"));
        form.setPreferredSize(new Dimension(310, 0));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        txtNom       = new JTextField(16);
        txtPrenom    = new JTextField(16);
        txtEmail     = new JTextField(16);
        txtTel       = new JTextField(14);
        txtAdresse   = new JTextField(16);
        txtLogin     = new JTextField(14);
        txtMdp       = new JTextField(14);
        txtCarte     = new JTextField(14);
        txtDateInscr = new JTextField(10);
        txtDateExp   = new JTextField(10);
        cmbStatut    = new JComboBox<>(Statut.values());

        String[] labels = {"Nom *","Prénom","Email","Téléphone","Adresse",
                           "Login *","Mot de passe","N° Carte *",
                           "Date inscription\n(YYYY-MM-DD)","Date expiration\n(YYYY-MM-DD)","Statut"};
        Component[] champs = {txtNom, txtPrenom, txtEmail, txtTel, txtAdresse,
                              txtLogin, txtMdp, txtCarte, txtDateInscr, txtDateExp, cmbStatut};
        for (int i = 0; i < labels.length; i++) {
            g.gridx=0; g.gridy=i; g.weightx=0.35;
            form.add(new JLabel(labels[i].replace("\n"," ") + " :"), g);
            g.gridx=1; g.weightx=0.65;
            form.add(champs[i], g);
        }

        // ---- Table + boutons
        JPanel right = new JPanel(new BorderLayout(6, 6));
        String[] cols = {"ID","Nom","Prénom","Email","N° Carte","Statut"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(45);
        table.getSelectionModel().addListSelectionListener(e -> remplirFormulaire());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        btnPanel.setBorder(BorderFactory.createEtchedBorder());
        btnAjouter    = creerBtn("Ajouter",     new Color(30, 130, 60));
        btnModifier   = creerBtn("Modifier",    new Color(30, 100, 200));
        btnSupprimer  = creerBtn("Supprimer",   new Color(200, 50, 50));
        btnEnregistrer= creerBtn("Enregistrer", new Color(150, 100, 0));
        btnFermer     = creerBtn("Fermer",      Color.DARK_GRAY);
        btnPanel.add(btnAjouter); btnPanel.add(btnModifier); btnPanel.add(btnSupprimer);
        btnPanel.add(btnEnregistrer); btnPanel.add(btnFermer);

        right.add(new JScrollPane(table), BorderLayout.CENTER);
        right.add(btnPanel,               BorderLayout.SOUTH);

        main.add(form,  BorderLayout.WEST);
        main.add(right, BorderLayout.CENTER);
        add(main);

        btnAjouter.addActionListener(e    -> ajouterAdherent());
        btnModifier.addActionListener(e   -> modifierAdherent());
        btnSupprimer.addActionListener(e  -> supprimerAdherent());
        btnEnregistrer.addActionListener(e-> enregistrerEnBD());
        btnFermer.addActionListener(e     -> dispose());
    }

    private void chargerTable() {
        tableModel.setRowCount(0);
        for (Adherent a : adherents)
            tableModel.addRow(new Object[]{
                a.getIdAdherent(), a.getNom(), a.getPrenom(),
                a.getEmail(), a.getNumeroCarte(), a.getStatut()});
    }

    private void remplirFormulaire() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        int id = (int) tableModel.getValueAt(row, 0);
        Adherent a = adherents.stream().filter(x -> x.getIdAdherent() == id).findFirst().orElse(null);
        if (a == null) return;
        txtNom.setText(a.getNom());
        txtPrenom.setText(a.getPrenom());
        txtEmail.setText(a.getEmail());
        txtTel.setText(a.getTelephone());
        txtAdresse.setText(a.getAdresse());
        txtLogin.setText(a.getLogin());
        txtMdp.setText("");
        txtCarte.setText(a.getNumeroCarte());
        txtDateInscr.setText(a.getDateInscription().toString());
        txtDateExp.setText(a.getDateExpiration().toString());
        cmbStatut.setSelectedItem(a.getStatut());
    }

    private Adherent lireFormulaire() {
        if (txtNom.getText().trim().isEmpty() || txtLogin.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nom et Login sont obligatoires.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        Adherent a = new Adherent();
        a.setNom(txtNom.getText().trim());
        a.setPrenom(txtPrenom.getText().trim());
        a.setEmail(txtEmail.getText().trim());
        a.setTelephone(txtTel.getText().trim());
        a.setAdresse(txtAdresse.getText().trim());
        a.setLogin(txtLogin.getText().trim());
        a.setMotDePasse(txtMdp.getText().trim());
        a.setNumeroCarte(txtCarte.getText().trim());
        try { a.setDateInscription(LocalDate.parse(txtDateInscr.getText().trim())); }
        catch (Exception ex) { a.setDateInscription(LocalDate.now()); }
        try { a.setDateExpiration(LocalDate.parse(txtDateExp.getText().trim())); }
        catch (Exception ex) { a.setDateExpiration(LocalDate.now().plusYears(1)); }
        a.setStatut((Statut) cmbStatut.getSelectedItem());
        return a;
    }

    private void ajouterAdherent() {
        Adherent a = lireFormulaire();
        if (a == null) return;
        a.setIdAdherent(-(adherents.size() + 1));
        adherents.add(a);
        chargerTable();
        viderFormulaire();
        JOptionPane.showMessageDialog(this, "Adhérent ajouté. Cliquez sur Enregistrer pour sauvegarder.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void modifierAdherent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un adhérent."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Adherent a = lireFormulaire();
        if (a == null) return;
        a.setIdAdherent(id);
        for (int i = 0; i < adherents.size(); i++)
            if (adherents.get(i).getIdAdherent() == id) { adherents.set(i, a); break; }
        chargerTable();
        viderFormulaire();
    }

    private void supprimerAdherent() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Sélectionnez un adhérent."); return; }
        if (JOptionPane.showConfirmDialog(this, "Confirmer la suppression ?", "Suppression",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        int id = (int) tableModel.getValueAt(row, 0);
        adherents.removeIf(a -> a.getIdAdherent() == id);
        chargerTable();
        viderFormulaire();
    }

    private void enregistrerEnBD() {
        int erreurs = 0;
        for (Adherent a : adherents) {
            try {
                if (a.getIdAdherent() <= 0) controller.ajouterAdherent(a);
                else controller.modifierAdherent(a);
            } catch (Exception e) { erreurs++; }
        }
        try { adherents = controller.getTousAdherents(); chargerTable(); }
        catch (SQLException e) { JOptionPane.showMessageDialog(this, "Erreur rechargement."); }
        if (erreurs == 0)
            JOptionPane.showMessageDialog(this, "Enregistré avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
        else
            JOptionPane.showMessageDialog(this, erreurs + " erreur(s).", "Avertissement", JOptionPane.WARNING_MESSAGE);
    }

    private void viderFormulaire() {
        txtNom.setText(""); txtPrenom.setText(""); txtEmail.setText("");
        txtTel.setText(""); txtAdresse.setText(""); txtLogin.setText("");
        txtMdp.setText(""); txtCarte.setText("");
        txtDateInscr.setText(LocalDate.now().toString());
        txtDateExp.setText(LocalDate.now().plusYears(1).toString());
        cmbStatut.setSelectedIndex(0);
        table.clearSelection();
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
