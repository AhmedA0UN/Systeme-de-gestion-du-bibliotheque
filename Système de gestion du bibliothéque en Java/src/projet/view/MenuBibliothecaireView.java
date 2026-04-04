package view;

import model.Utilisateur;

import javax.swing.*;
import java.awt.*;

/**
 * Menu principal du bibliothécaire.
 * Donne accès à toutes les fonctionnalités de gestion.
 */
public class MenuBibliothecaireView extends JFrame {

    private final Utilisateur utilisateur;
    private final int         idBibliothecaire;

    public MenuBibliothecaireView(Utilisateur utilisateur, int idBibliothecaire) {
        this.utilisateur      = utilisateur;
        this.idBibliothecaire = idBibliothecaire;
        initUI();
    }

    private void initUI() {
        setTitle("Bibliothèque — Espace Bibliothécaire");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);

        JPanel main = new JPanel(new BorderLayout(10, 10));
        main.setBackground(new Color(245, 248, 255));
        main.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // ---- En-tête
        JLabel lblBienvenue = new JLabel(
            "Bienvenue, " + utilisateur.getNomComplet() + "  |  Rôle : Bibliothécaire",
            SwingConstants.CENTER);
        lblBienvenue.setFont(new Font("Arial", Font.BOLD, 16));
        lblBienvenue.setForeground(new Color(30, 80, 160));
        lblBienvenue.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // ---- Grille de boutons
        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setBackground(new Color(245, 248, 255));

        JButton btnDocuments  = creerBouton("📚  Gestion des Documents",  new Color(30, 100, 200));
        JButton btnAdherents  = creerBouton("👤  Gestion des Adhérents",   new Color(80, 150, 50));
        JButton btnPrets      = creerBouton("🔖  Gestion des Prêts",       new Color(180, 100, 20));
        JButton btnRetours    = creerBouton("↩  Gestion des Retours",     new Color(160, 50, 50));

        grid.add(btnDocuments);
        grid.add(btnAdherents);
        grid.add(btnPrets);
        grid.add(btnRetours);

        // ---- Bouton déconnexion
        JButton btnDeconnexion = new JButton("Déconnexion");
        btnDeconnexion.setFont(new Font("Arial", Font.PLAIN, 12));
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(new Color(245, 248, 255));
        south.add(btnDeconnexion);

        main.add(lblBienvenue, BorderLayout.NORTH);
        main.add(grid,         BorderLayout.CENTER);
        main.add(south,        BorderLayout.SOUTH);
        add(main);

        // ---- Listeners
        btnDocuments.addActionListener(e ->
            new GestionDocumentsView().setVisible(true));
        btnAdherents.addActionListener(e ->
            new GestionAdherentsView().setVisible(true));
        btnPrets.addActionListener(e ->
            new GestionPretsView(idBibliothecaire).setVisible(true));
        btnRetours.addActionListener(e ->
            new GestionRetoursView().setVisible(true));
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
        btn.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        return btn;
    }
}
