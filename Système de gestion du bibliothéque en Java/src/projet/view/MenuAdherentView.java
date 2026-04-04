package view;

import model.Utilisateur;

import javax.swing.*;
import java.awt.*;

/**
 * Menu principal de l'adhérent.
 * Fonctionnalités : consulter, rechercher, emprunter un document.
 */
public class MenuAdherentView extends JFrame {

    private final Utilisateur utilisateur;
    private final int         idAdherent;

    public MenuAdherentView(Utilisateur utilisateur, int idAdherent) {
        this.utilisateur = utilisateur;
        this.idAdherent  = idAdherent;
        initUI();
    }

    private void initUI() {
        setTitle("Bibliothèque — Espace Adhérent");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 380);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(240, 250, 245));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ---- En-tête
        JLabel lblBienvenue = new JLabel(
            "Bienvenue, " + utilisateur.getNomComplet() + "  |  Espace Adhérent",
            SwingConstants.CENTER);
        lblBienvenue.setFont(new Font("Arial", Font.BOLD, 16));
        lblBienvenue.setForeground(new Color(20, 120, 60));
        lblBienvenue.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // ---- Boutons
        JPanel grid = new JPanel(new GridLayout(3, 1, 0, 15));
        grid.setBackground(new Color(240, 250, 245));

        JButton btnConsulter  = creerBouton("📖   Consulter la liste des documents",  new Color(30, 130, 60));
        JButton btnRechercher = creerBouton("🔍   Rechercher un document",             new Color(30, 100, 200));
        JButton btnEmprunter  = creerBouton("📤   Emprunter un document",              new Color(180, 100, 20));

        grid.add(btnConsulter);
        grid.add(btnRechercher);
        grid.add(btnEmprunter);

        JButton btnDeconnexion = new JButton("Déconnexion");
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(new Color(240, 250, 245));
        south.add(btnDeconnexion);

        main.add(lblBienvenue, BorderLayout.NORTH);
        main.add(grid,         BorderLayout.CENTER);
        main.add(south,        BorderLayout.SOUTH);
        add(main);

        // ---- Listeners
        btnConsulter.addActionListener(e ->
            new ConsulterDocumentsView(idAdherent).setVisible(true));
        btnRechercher.addActionListener(e ->
            new RechercherDocumentView(idAdherent).setVisible(true));
        btnEmprunter.addActionListener(e ->
            new EmprunterDocumentView(idAdherent).setVisible(true));
        btnDeconnexion.addActionListener(e -> {
            dispose();
            new LoginView().setVisible(true);
        });
    }

    private JButton creerBouton(String texte, Color couleur) {
        JButton btn = new JButton(texte);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(couleur);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        return btn;
    }
}
