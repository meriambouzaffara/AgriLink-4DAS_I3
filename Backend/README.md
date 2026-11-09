# AgriLink – Backend

## 1. Présentation du Backend

Le Backend d'**AgriLink** est basé sur une architecture **microservices**. Il permet de gérer les différentes fonctionnalités de la plateforme agricole : utilisateurs, produits agricoles, récoltes, commandes, livraisons, coopératives, certifications et relation client.

L'architecture est composée de **7 microservices métier indépendants**, qui communiquent à travers des API REST et des échanges d'événements métier lorsque cela est nécessaire.

Chaque microservice possède une responsabilité fonctionnelle précise et gère ses propres données.

## 2. Architecture du Backend

Le Backend est organisé comme suit :

```text
Backend/
│
├── user-management-service/
│   └── README.md
│
├── agricultural-management-service/
│   └── README.md
│
├── order-service/
│   └── README.md
│
├── delivery-service/
│   └── README.md
│
├── cooperative-partnership-service/
│   └── README.md
│
├── quality-certification-service/
│   └── README.md
│
├── customer-relations-service/
│   └── README.md
│
├── api-gateway/
│   └── README.md
│
└── discovery-server/
    └── README.md
```

## 3. Description des microservices

| N° | Microservice | Responsabilité |
|---|---|---|
| MS1 | User Management | Inscription, authentification, profils et rôles. |
| MS2 | Agricultural Management | Produits agricoles, récoltes, lots et stocks. |
| MS3 | Order Management | Panier, commandes, montants et statuts. |
| MS4 | Delivery Management | Livraisons, transporteurs et suivi. |
| MS5 | Cooperative & Partnership Management | Coopératives, adhésions et partenariats. |
| MS6 | Quality & Certification Management | Contrôles qualité, conformité et certifications. |
| MS7 | Customer Relations Management | Avis, notes et réclamations. |

## 4. Technologies utilisées

Les technologies prévues pour le développement du Backend sont :

- **Java** : langage de programmation.
- **Spring Boot** : développement des microservices.
- **Spring Web** : création des API REST.
- **Spring Data JPA / Hibernate** : gestion de la persistance.
- **MySQL/H2** : système de gestion des bases de données.
- **Spring Cloud Gateway** : gestion des requêtes et routage.
- **Spring Cloud Netflix Eureka** : découverte des services.
- **Node.js** : environnement d'exécution JavaScript utilisé pour développer MS7 (Customer Relations Management).
- **MongoDB** : base de données NoSQL utilisée pour MS7.


## 5. Architecture de communication

Les microservices communiquent à travers des API REST et des événements métier.

### Exemples de communication

- **MS3 → MS2** : vérification de la disponibilité et réservation du stock lors de la création d'une commande.
- **MS3 → MS4** : transmission d'une commande confirmée via l'événement `OrderConfirmed` pour préparer la livraison.
- **MS3 → MS1** : utilisation de l'identifiant du client pour associer une commande à son propriétaire.
- **MS4 → MS1** : récupération des références nécessaires à l'identification des transporteurs.
- **MS5 → MS1** : identification des agriculteurs souhaitant rejoindre une coopérative.
- **MS6** : utilisation de l'identifiant du lot agricole pour rattacher les contrôles qualité et les certifications.
- **MS7 → MS1** : identification du client à partir du jeton d'authentification.
- **MS7 → MS3** : vérification de l'appartenance et du statut livré d'une commande avant d'autoriser un avis associé.

## 6. Principes d'architecture

- **Indépendance** : chaque microservice possède son propre domaine métier.
- **Autonomie des données** : chaque service est responsable de ses propres entités et données.
- **Communication REST** : les échanges synchrones passent par des API REST.
- **Communication événementielle** : les événements métier permettent de déclencher des traitements asynchrones.
- **Sécurité** : les opérations sensibles doivent vérifier les rôles et autorisations.
- **Traçabilité** : les changements importants de statut doivent être historisés.
- **Identifiants partagés** : les services échangent des références comme `userId`, `productId`, `orderId` et `lotId` sans dupliquer toutes les données.

## 7. Infrastructure commune

Deux composants techniques facilitent le fonctionnement de l'architecture :

### API Gateway
- Point d'entrée unique pour les requêtes du frontend.
- Routage vers les microservices concernés.
- Centralisation des traitements transverses retenus par l'équipe.

### Discovery Server (Eureka)
- Enregistrement des microservices.
- Découverte des services disponibles.
- Facilitation de la communication entre les composants.

## 8. Organisation du travail

Chaque microservice métier est développé et maintenu par un étudiant responsable.

Les composants d'infrastructure (`api-gateway` et `discovery-server`) sont réalisés et intégrés en coordination avec l'ensemble de l'équipe.

Chaque dossier de microservice contient son propre README décrivant ses fonctionnalités, ses entités principales, ses endpoints REST et ses interactions avec les autres services.

## 9. Objectif

L'objectif du Backend AgriLink est de fournir une architecture modulaire, évolutive et maintenable permettant de répondre aux besoins des différents acteurs de la chaîne agricole.
