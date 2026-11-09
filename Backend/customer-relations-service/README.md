# MS7 — Customer Relations Management

## Présentation
Microservice responsable des avis clients, des notes, des réclamations et du suivi de leur traitement.

## Fonctionnalités

### Gestion des avis
- Permettre à un client de noter un produit ou une expérience.
- Ajouter un commentaire et, selon les règles de la plateforme, modifier ou supprimer son avis.
- Consulter les avis et calculer une note moyenne.

### Gestion des réclamations
- Créer une réclamation et l'associer à une commande.
- Décrire le problème et consulter le statut de la réclamation.
- Ajouter une réponse, suivre le traitement et clôturer la réclamation.

## Entités principales
| Entité | Description |
|---|---|
| `Review` | Avis associé à un client et à un produit ou à une expérience : note, commentaire et date. |
| `Complaint` | Réclamation liée à un client et, le cas échéant, à une commande : sujet, description et statut. |
| `ComplaintMessage` | Message ou réponse ajouté à l'historique d'une réclamation. |

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/reviews` | Ajouter un avis. |
| GET | `/api/reviews/product/{id}` | Consulter les avis d'un produit. |
| GET | `/api/reviews/product/{id}/rating` | Consulter la note moyenne. |
| POST | `/api/complaints` | Créer une réclamation. |
| GET | `/api/complaints/{id}` | Consulter une réclamation. |
| POST | `/api/complaints/{id}/messages` | Ajouter une réponse ou un message. |

## Interactions et règles métier
- MS1 : récupérer l'identité de l'utilisateur à partir du jeton d'authentification et consulter les informations de profil si nécessaire.
- MS3 : vérifier qu'une commande appartient bien au client et qu'elle est livrée avant d'autoriser un avis lié à cette commande.
- Un avis lié à une commande est accepté uniquement si la commande appartient au client et possède le statut `DELIVERED`.

## Données
Base proposée : **MongoDB**. 

## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Tout les membres de groupe

