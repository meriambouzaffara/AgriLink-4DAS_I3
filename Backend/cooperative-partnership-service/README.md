# MS5 — Cooperative & Partnership Management

## Présentation
Microservice responsable de la gestion des coopératives agricoles, des adhésions et des partenariats commerciaux.

## Fonctionnalités

### Gestion des coopératives
- Créer une coopérative agricole et modifier ses informations.
- Consulter les coopératives disponibles.
- Gérer les membres et consulter la liste des agriculteurs membres.
- Recevoir, accepter ou refuser une demande d'adhésion.

### Gestion des partenariats
- Créer une proposition de partenariat et envoyer une demande de collaboration.
- Accepter ou refuser une proposition.
- Consulter les partenariats existants.

## Entités principales
| Entité | Description |
|---|---|
| `Coopérative` | Structure qui regroupe des agriculteurs et centralise certaines informations ou offres. |
| `Membership` | Relation active entre un agriculteur et une coopérative. |
| `MembershipRequest` | Demande soumise par un agriculteur et en attente d'acceptation ou de refus. |

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/cooperatives` | Créer une coopérative. |
| GET | `/api/cooperatives` | Consulter les coopératives. |
| POST | `/api/cooperatives/{id}/members` | Soumettre une demande d'adhésion. |
| GET | `/api/cooperatives/{id}/members` | Consulter les membres. |
| PATCH | `/api/memberships/{id}/accept` | Accepter une adhésion. |

## Interactions
- MS1 permet d'identifier les agriculteurs qui demandent à rejoindre une coopérative.
- MS2 peut fournir les références des produits des agriculteurs membres pour centraliser leurs offres.

## Données
Base proposée : **MySQL**. 

## Technologies proposées
- Spring Boot.

## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Meriam

