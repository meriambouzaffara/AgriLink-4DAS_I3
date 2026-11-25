-- =====================================================================
-- Script d'initialisation MySQL pour MS5 : Cooperative Management Service
-- Base de donnees : cooperative_db
-- =====================================================================

CREATE DATABASE IF NOT EXISTS cooperative_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE cooperative_db;

-- 1. Table des besoins clients
CREATE TABLE IF NOT EXISTS besoins_clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantite_demandee DECIMAL(12, 2) NOT NULL,
    date_souhaitee DATE NOT NULL,
    description VARCHAR(1000),
    statut VARCHAR(30) NOT NULL DEFAULT 'EN_ATTENTE',
    date_creation DATETIME NOT NULL
) ENGINE=InnoDB;

-- 2. Table des annonces de collecte
CREATE TABLE IF NOT EXISTS annonces_collectes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    besoin_id BIGINT UNIQUE NULL,
    responsable_id BIGINT NOT NULL,
    quantite_objectif DECIMAL(12, 2) NOT NULL,
    quantite_acceptee DECIMAL(12, 2) DEFAULT 0.00,
    date_publication DATETIME NOT NULL,
    date_limite DATE NOT NULL,
    description VARCHAR(1000),
    statut VARCHAR(30) NOT NULL DEFAULT 'OUVERTE',
    CONSTRAINT fk_annonce_besoin FOREIGN KEY (besoin_id) REFERENCES besoins_clients(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- 3. Table des propositions de recolte
CREATE TABLE IF NOT EXISTS propositions_recoltes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    annonce_id BIGINT NOT NULL,
    agriculteur_id BIGINT NOT NULL,
    recolte_id BIGINT NOT NULL,
    quantite_proposee DECIMAL(12, 2) NOT NULL,
    quantite_acceptee DECIMAL(12, 2) NULL,
    quantite_recue DECIMAL(12, 2) NULL,
    date_proposition DATETIME NOT NULL,
    date_reception DATETIME NULL,
    statut VARCHAR(35) NOT NULL DEFAULT 'EN_ATTENTE',
    commentaire VARCHAR(1000),
    CONSTRAINT fk_proposition_annonce FOREIGN KEY (annonce_id) REFERENCES annonces_collectes(id) ON DELETE CASCADE
) ENGINE=InnoDB;
