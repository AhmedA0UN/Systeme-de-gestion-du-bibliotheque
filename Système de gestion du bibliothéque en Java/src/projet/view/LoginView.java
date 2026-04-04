package view;

import controller.AdherentController;
import model.Utilisateur;
import model.Utilisateur.Role;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Interface graphique de connexion.
 * Premier écran présenté à l'utilisateur.
 */
public class LoginView extends JFrame {

    private JTextField     txtLogin;
    private JPasswordField txtPassword;
    private JButton        btnConnexion;
    private JButton        btnAnnuler;
    private JLabel         lblMessage;

    private AdherentController controller;

    public LoginView() {
        try {
            controller = new AdherentController();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Impossible de se connecter à la base de données :\n" + e.getMessage(),
                "Erreur BD", JOptionPane.ERROR_MESSAGE);
        }
        initUI();
    }

    private void initUI() {
        setTitle("Système de Gestion de Bibliothèque — Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // ---- Panel principal
        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        main.setBackground(new Color(245, 248, 255));

        // ---- Titre
        JLabel titre = new JLabel("Bibliothèque ISIMG", SwingConstants.CENTER);
        titre.setFont(new Font("Arial", Font.BOLD, 22));
        titre.setForeground(new Color(30, 80, 160));
        titre.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // ---- Formulaire
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(245, 248, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 6, 6, 6);
        gbc.fill    = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        form.add(new JLabel("Login :"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtLogin = new JTextField(18);
        form.add(txtLogin, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(new JLabel("Mot de passe :"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        txtPassword = new JPasswordField(18);
        form.add(txtPassword, gbc);

        // ---- Message d'erreur
        lblMessage = new JLabel(" ", SwingConstants.CENTER);
        lblMessage.setForeground(Color.RED);
        lblMessage.setFont(new Font("Arial", Font.ITALIC, 12));

        // ---- Boutons
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        boutons.setBackground(new Color(245, 248, 255));
        btnConnexion = new JButton("Se connecter");
        btnAnnuler   = new JButton("Quitter");
        btnConnexion.setBackground(new Color(30, 100, 200));
        btnConnexion.setForeground(Color.WHITE);
        btnConnexion.setFont(new Font("Arial", Font.BOLD, 13));
        boutons.add(btnConnexion);
        boutons.add(btnAnnuler);

        main.add(titre,    BorderLayout.NORTH);
        main.add(form,     BorderLayout.CENTER);
        main.add(lblMessage, BorderLayout.SOUTH);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(245, 248, 255));
        bottom.add(lblMessage, BorderLayout.NORTH);
        bottom.add(boutons,    BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);

        add(main);

        // ---- Listeners
        btnConnexion.addActionListener(e -> seConnecter());
        btnAnnuler.addActionListener(e -> System.exit(0));
        txtPassword.addActionListener(e -> seConnecter());

        getRootPane().setDefaultButton(btnConnexion);
    }

    private void seConnecter() {
        String login = txtLogin.getText().trim();
        String mdp   = new String(txtPassword.getPassword());

        if (login.isEmpty() || mdp.isEmpty()) {
            lblMessage.setText("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Utilisateur u = controller.authentifier(login, mdp);
            if (u == null) {
                lblMessage.setText("Login ou mot de passe incorrect.");
                txtPassword.setText("");
                return;
            }
            int idSpecifique = controller.getIdSpecifique(u.getId(), u.getRole());
            dispose();

            if (u.getRole() == Role.BIBLIOTHECAIRE) {
                new MenuBibliothecaireView(u, idSpecifique).setVisible(true);
            } else {
                new MenuAdherentView(u, idSpecifique).setVisible(true);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur base de données : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new LoginView().setVisible(true);
        });
    }
}
