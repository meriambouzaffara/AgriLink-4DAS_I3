# MS1 — User Management

## Présentation
Microservice responsable de l'inscription, de l'authentification et de la gestion des profils utilisateurs d'AgriLink.

## Fonctionnalités
- Inscription et création d'un profil.
- Connexion et authentification.
- Consultation et modification du profil.
- Gestion des rôles métier : `AGRICULTEUR`, `COOPERATIVE`, `COMMERCANT`, `CONSOMMATEUR`, `TRANSPORTEUR`, `ADMINISTRATEUR`, `ORGANISME_CERTIFICATEUR`.
- Activation et désactivation d'un profil.
- Consultation des informations d'un utilisateur.

## Entités principales
| Entité | Description |
|---|---|
| `User` | Profil utilisateur contenant les informations personnelles et l'attribut `userRole`. |
| `UserRole` | Énumération des rôles métier de la plateforme. |

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/users` | Créer un profil. |
| GET | `/api/users/{id}` | Consulter un profil. |
| GET | `/api/users` | Consulter les utilisateurs (accès autorisé). |
| PUT | `/api/users/{id}` | Modifier un profil. |
| PATCH | `/api/users/{id}/status` | Activer ou désactiver un profil. |
| GET | `/api/users/role/{role}` | Rechercher les utilisateurs par rôle. |

## Interactions
- Le microservice Order Management utilise l'identifiant du client pour associer une commande à son propriétaire.
- Les autres microservices peuvent demander les informations de profil nécessaires sans dupliquer toutes les données utilisateur.
- Après connexion, le jeton d'accès (par exemple JWT) transporte un identifiant stable tel que `userId`.
- MS1 reste la source de référence des profils et des rôles.

## Données
Base proposée pour le projet : **H2**. 

## Configuration et lancement
À compléter par l'équipe selon la version Spring Boot, le port, le nom de la base et les variables d'environnement retenus.

## Responsable
Yosr
