# MS5 – Cooperative Management

Microservice responsable de la gestion des besoins clients et de l’organisation des collectes par les coopératives agricoles.

## Description

MS5 assure la gestion des besoins exprimés par les clients et l’organisation des collectes par les responsables de coopératives. Il permet :

- À un **client** de déclarer un besoin
- À un **responsable de coopérative** de prendre en charge ce besoin en publiant une annonce de collecte
- Aux **agriculteurs** de proposer des quantités issues de leurs récoltes

**MS5 est autonome** (base MySQL propre + API REST). Il s’appuie sur :
- **MS1** pour la gestion des utilisateurs
- **MS2** pour les produits et les récoltes

## Fonctionnalités

### 1. Gestion des besoins clients (Commerçant)
- Créer un besoin (produit, quantité demandée, date souhaitée)
- Consulter ses besoins
- Suivre le statut du besoin (`EN_ATTENTE`, `PRIS_EN_CHARGE`, `EN_COLLECTE`, `SATISFAIT`, `ANNULE`)

### 2. Gestion des annonces de collecte (Responsable)
- Prendre en charge un besoin et publier une annonce
- Consulter les annonces ouvertes et suivre la quantité acceptée
- Clôturer ou annuler une annonce

### 3. Gestion des propositions de récolte (Agriculteur / Responsable)
- Proposer une quantité issue d’une récolte existante
- Accepter, accepter partiellement ou refuser une proposition
- Enregistrer la quantité réellement reçue au dépôt

> **Règle métier importante** : Le besoin n’est satisfait que lorsque la quantité **réellement reçue** couvre la quantité demandée (et non la simple somme des quantités acceptées).

## Architecture

### Entités principales

| Entité                 | Description |
|-----------------------|-----------|
| `BesoinClient`        | Besoin d’un client en un produit agricole |
| `AnnonceCollecte`     | Annonce publiée pour collecter une quantité |
| `PropositionRecolte`  | Quantité proposée par un agriculteur |

### Statuts

**StatutBesoin**  
`SOUMIS` · `EN_COLLECTE` · `SATISFAIT` · `ANNULE`

**StatutAnnonce**  
`OUVERTE` · `OBJECTIF_ATTEINT` · `CLOTUREE` · `ANNULEE`

**StatutProposition**  
`EN_ATTENTE` · `ACCEPTEE` · `ACCEPTEE_PARTIELLEMENT` · `REFUSEE` · `RECUE` · `RECUE_PARTIELLEMENT`

## Stack technique

- **Java 21 / 23**
- **Spring Boot 3.3.0**
- **Spring Data JPA / Hibernate**
- **MySQL (`cooperative_db`)**
- **Lombok**
- **Jakarta Validation**

## API REST

### Gestion des besoins

| Méthode | Endpoint                 | Description                     | Codes HTTP      |
|---------|--------------------------|---------------------------------|-----------------|
| POST    | `/api/besoins`           | Créer un besoin                 | 201 / 400       |
| GET     | `/api/besoins`           | Lister les besoins              | 200             |
| GET     | `/api/besoins/{id}`      | Consulter un besoin             | 200 / 404       |
| PUT     | `/api/besoins/{id}`      | Modifier un besoin              | 200 / 400       |
| PATCH   | `/api/besoins/{id}/annuler` | Annuler un besoin            | 200 / 409       |
| DELETE  | `/api/besoins/{id}`      | Supprimer un besoin             | 204 / 404       |

### Gestion des annonces de collecte

| Méthode | Endpoint                                    | Description                          | Codes HTTP      |
|---------|---------------------------------------------|--------------------------------------|-----------------|
| POST    | `/api/annonces-collecte`                    | Prendre en charge un besoin          | 201 / 409       |
| GET     | `/api/annonces-collecte`                    | Rechercher les annonces              | 200             |
| GET     | `/api/annonces-collecte/{id}`               | Détail d’une annonce                 | 200 / 404       |
| PUT     | `/api/annonces-collecte/{id}`               | Modifier une annonce                 | 200 / 400       |
| PATCH   | `/api/annonces-collecte/{id}/cloturer`      | Clôturer une annonce                 | 200 / 409       |
| PATCH   | `/api/annonces-collecte/{id}/annuler`       | Annuler une annonce                  | 200 / 409       |
| DELETE  | `/api/annonces-collecte/{id}`               | Supprimer une annonce                | 204 / 404       |

### Gestion des propositions

| Méthode | Endpoint                                           | Description                                | Codes HTTP      |
|---------|----------------------------------------------------|--------------------------------------------|-----------------|
| POST    | `/api/annonces-collecte/{id}/propositions`         | Soumettre une proposition                  | 201 / 400       |
| POST    | `/api/propositions/annonce/{annonceId}`            | Soumettre une proposition                  | 201 / 400       |
| GET     | `/api/propositions`                                | Lister les propositions (filtres)          | 200             |
| GET     | `/api/propositions/{id}`                           | Consulter une proposition                  | 200 / 404       |
| PATCH   | `/api/propositions/{id}/decision`                  | Accepter / Refuser / Acceptation partielle | 200 / 409       |
| PATCH   | `/api/propositions/{id}/reception`                 | Enregistrer la quantité reçue au dépôt     | 200 / 400       |
| DELETE  | `/api/propositions/{id}`                           | Supprimer une proposition                  | 204 / 404       |
