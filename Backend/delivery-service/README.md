# MS4 — Delivery Management

## Présentation
Microservice responsable de l'organisation, de l'affectation et du suivi des livraisons.

## Fonctionnalités
- Créer une livraison pour une commande confirmée.
- Affecter un transporteur à une livraison.
- Enregistrer l'adresse de livraison et la date prévue.
- Modifier le statut de la livraison.
- Consulter l'historique des changements de statut.
- Confirmer la réception de la commande.

## Entités principales
| Entité | Description |
|---|---|
| `Livraison` | Dossier de livraison associé à une commande : adresse, transporteur, date prévue et statut. |
| `TrackingEvent` | Événement de suivi conservant l'évolution du statut, la date et les informations associées. |

## Cycle de vie d'une livraison
- Statuts principaux : `PENDING` → `ASSIGNED` → `PICKED_UP` → `IN_TRANSIT` → `DELIVERED`.
- Autres statuts possibles : `FAILED`, `CANCELLED`.

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/deliveries` | Créer une livraison. |
| GET | `/api/deliveries/{id}` | Consulter une livraison. |
| GET | `/api/deliveries/order/{orderId}` | Consulter la livraison d'une commande. |
| PATCH | `/api/deliveries/{id}/assign` | Affecter un transporteur. |
| PATCH | `/api/deliveries/{id}/status` | Modifier le statut. |
| GET | `/api/deliveries/{id}/tracking` | Consulter l'historique de suivi. |

## Interactions
- MS3 transmet les commandes confirmées, directement ou au moyen de l'événement `OrderConfirmed`.
- MS1 fournit les références et les informations de profil nécessaires pour identifier les transporteurs.

## Données
Base proposée : **MYSQL**. 

## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Maissa

