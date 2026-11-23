# AgriLink MS7 – Customer Relations Management (Gestion de la Relation Client)

Ce microservice fait partie de la plateforme **AgriLink**, une application basée sur une architecture microservices pour le secteur agricole.

MS7 est responsable de la **Gestion de la Relation Client** :
- **Gestion des avis** (Reviews) : évaluation des agriculteurs par les clients suite à des commandes livrées.
- **Gestion des réclamations** (Complaints) : déclaration et suivi du traitement des réclamations liées aux commandes.
- **Gestion des échanges** (Complaint Messages) : historique des messages et communication entre clients, agriculteurs et service client.

---

## 1. Technologies utilisées

- **Node.js** (v22 / v24)
- **NestJS** (v12)
- **TypeScript**
- **MongoDB** & **Mongoose**
- **class-validator** & **class-transformer**
- **Swagger / OpenAPI** (`/api/docs`)
- **Postman** (Collection v2.1 incluse : `agrilink-ms7.postman_collection.json`)

---

## 2. Indépendance et Données Externes (MS1 & MS3)

Durant cette phase, MS7 est **totalement autonome et indépendant** :
- Aucune communication HTTP, REST ou RabbitMQ vers d'autres microservices (MS1 User Management ou MS3 Order Management).
- Pas d'intégration directe avec Keycloak pour l'instant.
- Toutes les opérations métier fonctionnent localement avec MongoDB.

### DTOs de Référence Externes (`src/common/dto/references/`)

Les entités appartenant à d'autres services ne possèdent **aucun schéma Mongoose** et **aucune collection** dans `agrilink_customer_db`. Elles sont représentées par des DTOs de référence :

1. `ClientReferenceDto` (`client-reference.dto.ts`) : `clientId`, `nom`, `prenom`, `email`
2. `AgriculteurReferenceDto` (`agriculteur-reference.dto.ts`) : `agriculteurId`, `nom`, `prenom`
3. `CommandeReferenceDto` (`commande-reference.dto.ts`) : `commandeId`, `clientId`, `agriculteurId`, `statut`, `dateCommande`
4. `UtilisateurReferenceDto` (`utilisateur-reference.dto.ts`) : `utilisateurId`, `role`

Les identifiants externes persistés dans les schémas MS7 (`clientId`, `agriculteurId`, `commandeId`, `auteurId`) sont de type `string`.

---

## 3. Base de données MongoDB (`agrilink_customer_db`)

Exactement 3 collections :

1. **`reviews`** :
   - `_id` : ObjectId MongoDB
   - `clientId` : `string`
   - `agriculteurId` : `string`
   - `commandeId` : `string` (index unique : 1 avis par commande)
   - `note` : `number` (entier de 1 à 5)
   - `commentaire` : `string` (optionnel, max 1000 caractères)
   - `dateCreation` : `Date`
   - `dateModification` : `Date`
   - Index : `{ commandeId: 1 }` (unique), `{ agriculteurId: 1, dateCreation: -1 }`

2. **`complaints`** :
   - `_id` : ObjectId MongoDB
   - `clientId` : `string`
   - `commandeId` : `string`
   - `sujet` : `string` (3 à 150 caractères)
   - `description` : `string` (10 à 4000 caractères)
   - `statut` : `ComplaintStatus` (`OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED` ; défaut : `OPEN`)
   - `dateCreation` : `Date`
   - `dateModification` : `Date`
   - Index : `{ clientId: 1, dateCreation: -1 }`, `{ statut: 1, dateCreation: -1 }`

3. **`complaintMessages`** :
   - `_id` : ObjectId MongoDB
   - `complaintId` : `string` (identifiant de la réclamation)
   - `auteurId` : `string`
   - `contenu` : `string` (1 à 2000 caractères)
   - `dateEnvoi` : `Date`
   - Index : `{ complaintId: 1, dateEnvoi: 1 }`

---

## 4. Authentification et Rôles (Simulation Keycloak)

Pour préparer l'intégration future de Keycloak sans bloquer le développement local, l'identité et le rôle sont transmis via les en-têtes HTTP suivants :

- `x-utilisateur-id` : Identifiant de l'utilisateur (ex: `client-001`, `agri-001`, `admin-001`)
- `x-utilisateur-role` : Rôle (`CLIENT`, `ADMIN`, `AGRICULTEUR` ou `SERVICE_CLIENT`)

### Matrice des autorisations métier :

| Rôle | Avis (Reviews) | Réclamations (Complaints) | Messages |
|---|---|---|---|
| **CLIENT** | Créer, modifier, supprimer **ses propres** avis | Créer et consulter **ses propres** réclamations (`/mine`, `/:id`) | Ajouter des messages à ses réclamations ouvertes |
| **AGRICULTEUR** | Consulter les avis et la moyenne | Consulter les réclamations, modifier le statut (`IN_PROGRESS`, `RESOLVED`) | Répondre aux clients |
| **ADMIN** / **SERVICE_CLIENT** | Consulter | Consulter toutes les réclamations, modifier statut, **clôturer** (`CLOSED`) | Répondre aux réclamations |

---

## 5. Endpoints de l'API REST (Préfixe `/api`)

### Reviews (`/api/reviews`)

| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| `POST` | `/api/reviews` | `CLIENT` | Noter un agriculteur après commande (1 seul avis par commande) |
| `GET` | `/api/reviews/agriculteur/:id` | Public | Liste des avis reçus par un agriculteur |
| `GET` | `/api/reviews/agriculteur/:id/average` | Public | Moyenne des notes et nombre d'avis |
| `GET` | `/api/reviews/:id` | Public | Détail d'un avis |
| `PATCH` | `/api/reviews/:id` | `CLIENT` (auteur) | Modifier sa note ou son commentaire |
| `DELETE` | `/api/reviews/:id` | `CLIENT` (auteur) | Supprimer son propre avis |

### Complaints (`/api/complaints`)

| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| `POST` | `/api/complaints` | `CLIENT` | Déposer une réclamation pour une commande (statut `OPEN`) |
| `GET` | `/api/complaints/mine` | `CLIENT` | Lister toutes ses réclamations |
| `GET` | `/api/complaints` | `ADMIN`, `AGRICULTEUR` | Consulter l'ensemble des réclamations |
| `GET` | `/api/complaints/:id` | `CLIENT` (auteur), `ADMIN`, `AGRICULTEUR` | Détail d'une réclamation |
| `PATCH` | `/api/complaints/:id/status` | `ADMIN`, `AGRICULTEUR` (ou `ADMIN` pour `CLOSED`) | Modifier le statut ou clôturer |

### Complaint Messages (`/api/complaints/:complaintId/messages`)

| Méthode | Endpoint | Rôle requis | Description |
|---|---|---|---|
| `POST` | `/api/complaints/:complaintId/messages` | `CLIENT` (auteur), `ADMIN`, `AGRICULTEUR` | Ajouter un message à une réclamation ouverte |
| `GET` | `/api/complaints/:complaintId/messages` | `CLIENT` (auteur), `ADMIN`, `AGRICULTEUR` | Historique des messages par ordre chronologique |

---

## 6. Installation et Démarrage

### Prérequis

- Node.js >= 20.19 ou >= 22.12
- MongoDB démarré localement sur le port `27017` (ou via Docker)

### Configuration (`.env`)

```env
PORT=3007
MONGODB_URI=mongodb://localhost:27017/agrilink_customer_db
```

### Installation des dépendances

```bash
npm install
```

### Lancement de l'application

```bash
# Mode développement avec rechargement à chaud
npm run start:dev

# Mode production
npm run build
npm run start:prod
```

L'application écoute sur le port `3007`.

- **Documentation Swagger** : [http://localhost:3007/api/docs](http://localhost:3007/api/docs)
- **Préfixe global** : `/api`

### Tests Unitaires

```bash
npm test
```

Tous les tests de services (`ReviewsService`, `ComplaintsService`, `ComplaintMessagesService`) s'exécutent avec Jest et ts-jest.

---

## 7. Déploiement Docker

### Construction de l'image Docker

```bash
docker build -t agrilink-customer-relations-service:latest .
```

### Exécution du conteneur

```bash
docker run -d -p 3007:3007 \
  -e PORT=3007 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/agrilink_customer_db \
  --name agrilink-ms7 \
  agrilink-customer-relations-service:latest
```

---

## 8. Test avec Postman

1. Ouvrez Postman.
2. Cliquez sur **Import**.
3. Sélectionnez le fichier `agrilink-ms7.postman_collection.json`.
4. Tous les dossiers (**Reviews**, **Complaints**, **Complaint Messages**) sont configurés avec les variables d'environnement et les en-têtes d'authentification prêts à l'emploi.
