# MS3 — Order Management

## Présentation
Microservice responsable du panier, de la création des commandes, de leur validation et de leur suivi.

## Fonctionnalités
- Créer une commande et y ajouter plusieurs produits.
- Calculer le montant total de la commande.
- Vérifier la disponibilité des produits auprès de MS2.
- Confirmer ou annuler une commande.
- Consulter l'historique des commandes d'un client.
- Suivre l'état de chaque commande.

## Entités principales
| Entité | Description |
|---|---|
| `Order` | Commande : client, date, montant total, statut et informations générales. |
| `OrderItem` | Ligne de commande : produit concerné, quantité, prix unitaire et sous-total. |

## Cycle de vie d'une commande
- Statuts principaux : `PENDING` → `CONFIRMED` → `IN_PREPARATION` → `SHIPPED` → `DELIVERED`.
- Statut alternatif : `CANCELLED`.

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/orders` | Créer une commande. |
| GET | `/api/orders/{id}` | Consulter une commande. |
| GET | `/api/orders/customer/{id}` | Consulter l'historique d'un client. |
| PATCH | `/api/orders/{id}/confirm` | Confirmer une commande. |
| PATCH | `/api/orders/{id}/cancel` | Annuler une commande. |

## Collaboration avec les autres microservices
1. MS3 reçoit une demande de création de commande.
2. MS3 vérifie la disponibilité et enregistre la commande au statut `PENDING`.
3. La confirmation réserve le stock et passe la commande à `CONFIRMED`.
4. L'annulation d'une commande confirmée libère sa réservation.
5. L'intégration MS2 réelle et les événements MS4 restent à implémenter.

MS1 fournit l'identité du client. Les références partagées (`userId`, `productId`) sont échangées sans recopier toutes les données des autres services.

## Données
Base proposée : **MySQL**. 

## Technologies proposées
- Spring Boot.


## Configuration et lancement
Pour le développement autonome : `mvn spring-boot:run -Dspring-boot.run.profiles=dev-mock`.
Voir [DEVELOPMENT.md](DEVELOPMENT.md) pour les API, les limites du stock simulé et les exemples HTTP.

## Responsable
Khaled
