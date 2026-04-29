-- ============================================================
-- Système de Gestion de Bibliothèque
-- Base de données MySQL
-- CPI2 - ISIMG Gabès - 2025/2026
-- ============================================================

CREATE DATABASE IF NOT EXISTS bibliotheque
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE bibliotheque;

-- ============================================================
-- TABLE : utilisateur (parent commun adhérent/bibliothécaire)
-- ============================================================
CREATE TABLE utilisateur (
    id_utilisateur  INT AUTO_INCREMENT PRIMARY KEY,
    nom             VARCHAR(50)  NOT NULL,
    prenom          VARCHAR(50)  NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    telephone       VARCHAR(20),
    adresse         VARCHAR(200),
    login           VARCHAR(50)  NOT NULL UNIQUE,
    mot_de_passe    VARCHAR(255) NOT NULL,
    role            ENUM('ADHERENT','BIBLIOTHECAIRE') NOT NULL,
    date_creation   DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE : adherent (spécialisation de utilisateur)
-- ============================================================
CREATE TABLE adherent (
    id_adherent       INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur    INT NOT NULL UNIQUE,
    numero_carte      VARCHAR(20) NOT NULL UNIQUE,
    date_inscription  DATE NOT NULL,
    date_expiration   DATE NOT NULL,
    statut            ENUM('ACTIF','SUSPENDU','EXPIRE') DEFAULT 'ACTIF',
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur)
        ON DELETE CASCADE
);

-- ============================================================
-- TABLE : bibliothecaire (spécialisation de utilisateur)
-- ============================================================
CREATE TABLE bibliothecaire (
    id_bibliothecaire INT AUTO_INCREMENT PRIMARY KEY,
    id_utilisateur    INT NOT NULL UNIQUE,
    matricule         VARCHAR(20) NOT NULL UNIQUE,
    FOREIGN KEY (id_utilisateur) REFERENCES utilisateur(id_utilisateur)
        ON DELETE CASCADE
);

-- ============================================================
-- TABLE : document
-- ============================================================
CREATE TABLE document (
    id_document    INT AUTO_INCREMENT PRIMARY KEY,
    titre          VARCHAR(200) NOT NULL,
    auteur         VARCHAR(150),
    editeur        VARCHAR(100),
    annee_edition  YEAR,
    isbn           VARCHAR(20) UNIQUE,
    categorie      VARCHAR(80),
    type_document  ENUM('LIVRE','REVUE','THESE','MEMOIRE','AUTRE') DEFAULT 'LIVRE',
    nombre_exemplaires INT NOT NULL DEFAULT 1,
    disponibles    INT NOT NULL DEFAULT 1,
    description    TEXT,
    date_ajout     DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLE : pret
-- ============================================================
CREATE TABLE pret (
    id_pret        INT AUTO_INCREMENT PRIMARY KEY,
    id_adherent    INT NOT NULL,
    id_document    INT NOT NULL,
    date_emprunt   DATE NOT NULL,
    date_retour_prevue DATE NOT NULL,
    date_retour_effective DATE,
    statut         ENUM('EN_COURS','RENDU','EN_RETARD') DEFAULT 'EN_COURS',
    id_bibliothecaire INT,
    remarque       TEXT,
    FOREIGN KEY (id_adherent)      REFERENCES adherent(id_adherent),
    FOREIGN KEY (id_document)      REFERENCES document(id_document),
    FOREIGN KEY (id_bibliothecaire) REFERENCES bibliothecaire(id_bibliothecaire)
);

-- ============================================================
-- INDEX
-- ============================================================
CREATE INDEX idx_pret_statut   ON pret(statut);
CREATE INDEX idx_doc_titre     ON document(titre);
CREATE INDEX idx_doc_auteur    ON document(auteur);
CREATE INDEX idx_pret_adherent ON pret(id_adherent);

-- ============================================================
-- TRIGGER : décrémenter disponibles lors d'un prêt
-- ============================================================
DELIMITER $$
CREATE TRIGGER trg_after_pret_insert
AFTER INSERT ON pret
FOR EACH ROW
BEGIN
    UPDATE document
    SET disponibles = disponibles - 1
    WHERE id_document = NEW.id_document;
END$$

-- TRIGGER : incrémenter disponibles lors d'un retour
CREATE TRIGGER trg_after_pret_update
AFTER UPDATE ON pret
FOR EACH ROW
BEGIN
    IF NEW.statut = 'RENDU' AND OLD.statut != 'RENDU' THEN
        UPDATE document
        SET disponibles = disponibles + 1
        WHERE id_document = NEW.id_document;
    END IF;
END$$
DELIMITER ;

