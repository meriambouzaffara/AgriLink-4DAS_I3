# MS5 – Cooperative Management

Microservice responsable de la gestion des besoins clients et de l’organisation des collectes par les coopératives agricoles.

##  Description

MS5 assure la gestion des besoins exprimés par les clients et l’organisation des collectes par les responsables de coopératives. Il permet :

- À un **client** de déclarer un besoin
- À un **responsable de coopérative** de prendre en charge ce besoin en publiant une annonce de collecte
- Aux **agriculteurs** de proposer des quantités issues de leurs récoltes

**MS5 est autonome** (base MySQL propre + API REST). Il s’appuie sur :
- **MS1** pour la gestion des utilisateurs
- **MS2** pour les produits et les récoltes

Il publie également des événements métier via un broker de messages.

##  Fonctionnalités

### 1. Gestion des besoins clients (Commerçant)
- Créer un besoin (produit, quantité demandée, date souhaitée)
- Consulter ses besoins
- Suivre le statut du besoin (`EN_ATTENTE`, `PRIS_EN_CHARGE`, `EN_COLLECTE`, `SATISFAIT`, `ANNULE`)

### 2. Gestion des coopératives (Responsable)
- Enregistrer les informations de la coopérative et l’associer à un compte MS1
- Activer / désactiver le responsable

### 3. Gestion des annonces de collecte (Responsable)
- Prendre en charge un besoin et publier une annonce
- Consulter les annonces ouvertes et suivre la quantité acceptée
- Clôturer ou annuler une annonce

### 4. Gestion des propositions de récolte (Agriculteur / Responsable)
- Proposer une quantité issue d’une récolte existante (vérifiée auprès de MS2)
- Accepter, accepter partiellement ou refuser une proposition
- Enregistrer la quantité réellement reçue

> **Règle métier importante** : Le besoin n’est satisfait que lorsque la quantité **réellement reçue** couvre la quantité demandée (et non la simple somme des quantités acceptées).

## Architecture

### Entités principales

| Entité                    | Description |
|--------------------------|-----------|
| `BesoinClient`           | Besoin d’un client en un produit agricole |
| `ResponsableCooperative` | Responsable d’une coopérative + dépôt de réception |
| `AnnonceCollecte`        | Annonce publiée pour collecter une quantité |
| `PropositionRecolte`     | Quantité proposée par un agriculteur |

### Statuts

**StatutBesoin**  
`EN_ATTENTE` · `PRIS_EN_CHARGE` · `EN_COLLECTE` · `SATISFAIT` · `ANNULE`

**StatutAnnonce**  
`OUVERTE` · `OBJECTIF_ATTEINT` · `CLOTUREE` · `ANNULEE`

**StatutProposition**  
`EN_ATTENTE` · `ACCEPTEE` · `ACCEPTEE_PARTIELLEMENT` · `REFUSEE` · `RECUE` · `RECUE_PARTIELLEMENT`

##  Stack technique

- **Java 17+**
- **Spring Boot**
- **Spring Data JPA / Hibernate**
- **MySQL**
- **OpenFeign** (appels synchrones)
- **RabbitMQ** (événements asynchrones)
- **Spring Cloud** (Circuit Breaker, Retry, etc.)

##  API REST

### Gestion des besoins

| Méthode | Endpoint                          | Description                     | Codes HTTP      |
|---------|-----------------------------------|---------------------------------|-----------------|
| POST    | `/api/besoins`                    | Créer un besoin                 | 201 / 400       |
| GET     | `/api/besoins/mine`               | Besoins du client connecté      | 200             |
| GET     | `/api/besoins/{id}`               | Consulter un besoin             | 200 / 404       |
| PATCH   | `/api/besoins/{id}/annulation`    | Annuler un besoin               | 200 / 409       |

### Gestion des annonces de collecte

| Méthode | Endpoint                                      | Description                          | Codes HTTP      |
|---------|-----------------------------------------------|--------------------------------------|-----------------|
| POST    | `/api/annonces-collecte`                      | Prendre en charge un besoin          | 201 / 409       |
| GET     | `/api/annonces-collecte`                      | Rechercher les annonces ouvertes     | 200             |
| GET     | `/api/annonces-collecte/{id}`                 | Détail d’une annonce                 | 200 / 404       |
| GET     | `/api/annonces-collecte/mes-annonces`         | Annonces du responsable connecté     | 200             |
| PATCH   | `/api/annonces-collecte/{id}/cloture`         | Clôturer une annonce                 | 200 / 409       |
| PATCH   | `/api/annonces-collecte/{id}/annulation`      | Annuler une annonce                  | 200 / 409       |

### Gestion des propositions

| Méthode | Endpoint                                          | Description                              | Codes HTTP      |
|---------|---------------------------------------------------|------------------------------------------|-----------------|
| POST    | `/api/annonces-collecte/{id}/propositions`        | Soumettre une proposition                | 201 / 503       |
| GET     | `/api/annonces-collecte/{id}/propositions`        | Lister les propositions d’une annonce    | 200 / 403       |
| GET     | `/api/propositions/{id}`                          | Consulter une proposition                | 200 / 404       |
| PATCH   | `/api/propositions/{id}/decision`                 | Accepter / Refuser / Acceptation partielle | 200 / 409       |
| PATCH   | `/api/propositions/{id}/reception`                | Enregistrer la quantité reçue            | 200 / 400       |

> Les identifiants (`clientId`, `userId`, `agriculteurId`) sont toujours extraits du **jeton d’authentification**, jamais du corps de la requête.

##  Événements publiés

| Événement                  | Description |
|---------------------------|-----------|
| `PropositionAcceptee`     | Une proposition a été acceptée (totalement ou partiellement) |
| `QuantiteRecolteRecue`    | Une quantité a été réellement reçue au dépôt |
| `CollecteCloturee`        | Une annonce de collecte a été clôturée |

##  Interactions avec les autres microservices

### MS1 – Gestion des utilisateurs
- **Type** : REST synchrone (OpenFeign)
- **Usage** : Vérification de l’existence, du statut et du rôle de l’utilisateur
- **En cas d’indisponibilité** : Retourne `503 Service Unavailable`

### MS2 – Product & Harvest Management
- **Type** : REST synchrone (OpenFeign) + Événements asynchrones (RabbitMQ)
- **Usage** :
  - Vérification de l’existence et de la disponibilité d’une récolte
  - Réservation de quantité
  - Mise à jour de la récolte via l’événement `QuantiteRecolteRecue`
- **Résilience** :
  - Pattern **Outbox** en cas d’indisponibilité du broker
  - Retry + Idempotence + Dead Letter Queue
  - Circuit Breaker + Timeout sur les appels REST

##  Base de données

**MySQL** a été choisi pour les raisons suivantes :

- Gestion native des relations entre entités
- Support des transactions (proposition + mise à jour des quantités + statut)
- Intégration optimale avec Spring Data JPA / Hibernate
- Chaque microservice possède sa propre base (pas de clé étrangère vers MS1/MS2)


