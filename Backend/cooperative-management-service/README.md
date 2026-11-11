# MS5 — Cooperative Management

## Présentation

Microservice responsable de la gestion des besoins exprimés par les commerçants et de l’organisation des collectes par les responsables de coopératives.

Il permet à un commerçant de déclarer un besoin, à un responsable de le prendre en charge en publiant une annonce de collecte, puis aux agriculteurs de proposer des quantités issues de leurs récoltes.

**MS5 est autonome** (base MySQL propre, API REST) : il s’appuie sur **MS1** pour les utilisateurs et sur **MS2** pour les produits et les récoltes, et publie des événements métier (`PropositionAcceptee`, `QuantiteRecolteRecue`, `CollecteCloturee`) via un broker.

---

## Fonctionnalités

### 1. Gestion des besoins clients — Client (Commerçant)
- Créer un besoin (produit, quantité demandée, date souhaitée)
- Consulter ses besoins
- Suivre le statut du besoin : `EN_ATTENTE`, `PRIS_EN_CHARGE`, `EN_COLLECTE`, `SATISFAIT` ou `ANNULE`

### 2. Gestion des coopératives — Responsable de coopérative
- Enregistrer les informations de la coopérative et l’associer à son compte MS1
- Activer ou désactiver le responsable

### 3. Gestion des annonces de collecte — Responsable de coopérative
- Prendre en charge un besoin et publier une annonce (quantité objectif, date limite)
- Consulter les annonces ouvertes et suivre la quantité acceptée
- Clôturer ou annuler une annonce

### 4. Gestion des propositions de récolte — Agriculteur / Responsable
- Proposer une quantité issue d’une récolte existante (vérifiée auprès de MS2)
- Accepter, accepter partiellement ou refuser une proposition
- Enregistrer la quantité réellement reçue et suivre les livraisons

> **Règle clé** : le besoin n’est satisfait que lorsque la quantité **réellement reçue** couvre la quantité demandée, et non la simple somme des quantités acceptées.

---

## Entités principales

| Entité                    | Description                                                                 |
|---------------------------|-----------------------------------------------------------------------------|
| **BesoinClient**          | Besoin d’un commerçant en un produit agricole (quantité demandée, date souhaitée) |
| **ResponsableCooperative**| Responsable d’une coopérative, lié à un compte MS1, et son dépôt de réception |
| **AnnonceCollecte**       | Annonce publiée pour collecter la quantité d’un besoin auprès des agriculteurs |
| **PropositionRecolte**    | Quantité proposée par un agriculteur à partir d’une récolte, avec quantités acceptée et reçue |
| **StatutBesoin**          | `EN_ATTENTE`, `PRIS_EN_CHARGE`, `EN_COLLECTE`, `SATISFAIT`, `ANNULE` |
| **StatutAnnonce**         | `OUVERTE`, `OBJECTIF_ATTEINT`, `CLOTUREE`, `ANNULEE` |
| **StatutProposition**     | `EN_ATTENTE`, `ACCEPTEE`, `ACCEPTEE_PARTIELLEMENT`, `REFUSEE`, `RECUE`, `RECUE_PARTIELLEMENT` |

**Multiplicités** :
- Un besoin est associé à 0 ou 1 annonce
- Un responsable publie plusieurs annonces (chacune a un seul responsable)
- Une annonce reçoit plusieurs propositions (chacune concerne une seule annonce)

Les identifiants `clientId`, `productId`, `userId`, `agriculteurId`, `recolteId` sont des références externes (MS1/MS2), **sans clé étrangère SQL**.

---

## API REST prévues

### 1. Gestion des besoins clients — Client (Commerçant)

| Méthode | Endpoint                          | Description                          | Codes HTTP              |
|---------|-----------------------------------|--------------------------------------|-------------------------|
| POST    | `/api/besoins`                    | Créer un besoin client               | 201 / 400               |
| GET     | `/api/besoins/mine`               | Consulter les besoins du client connecté | 200                 |
| GET     | `/api/besoins/{id}`               | Consulter un besoin                  | 200 / 404               |
| PATCH   | `/api/besoins/{id}/annulation`    | Annuler un besoin                    | 200 / 409               |

### 2. Gestion des annonces de collecte — Responsable de coopérative

| Méthode | Endpoint                                      | Description                              | Codes HTTP     |
|---------|-----------------------------------------------|------------------------------------------|----------------|
| POST    | `/api/annonces-collecte`                      | Prendre en charge un besoin et créer l’annonce | 201 / 409 |
| GET     | `/api/annonces-collecte`                      | Rechercher les annonces ouvertes         | 200            |
| GET     | `/api/annonces-collecte/{id}`                 | Consulter le détail d’une annonce        | 200 / 404      |
| GET     | `/api/annonces-collecte/mes-annonces`         | Consulter les annonces du responsable connecté | 200     |
| PATCH   | `/api/annonces-collecte/{id}/cloture`         | Clôturer une annonce                     | 200 / 409      |
| PATCH   | `/api/annonces-collecte/{id}/annulation`      | Annuler une annonce                      | 200 / 409      |

### 3. Gestion des propositions — Agriculteur / Responsable

| Méthode | Endpoint                                          | Description                              | Codes HTTP     |
|---------|---------------------------------------------------|------------------------------------------|----------------|
| POST    | `/api/annonces-collecte/{id}/propositions`        | Soumettre une proposition (agriculteur)  | 201 / 503      |
| GET     | `/api/annonces-collecte/{id}/propositions`        | Consulter les propositions d’une annonce | 200 / 403      |
| GET     | `/api/propositions/{id}`                          | Consulter une proposition                | 200 / 404      |
| PATCH   | `/api/propositions/{id}/decision`                 | Accepter / accepter partiellement / refuser | 200 / 409   |
| PATCH   | `/api/propositions/{id}/reception`                | Enregistrer la quantité réellement reçue | 200 / 400      |

> Les identifiants du client, du responsable et de l’agriculteur sont **déduits du jeton d’authentification**, jamais du corps de la requête.

---
