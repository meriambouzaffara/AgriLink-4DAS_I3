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
2. MS3 contacte MS2 pour vérifier la disponibilité et réserver la quantité demandée.
3. Si le stock est disponible, MS3 enregistre et confirme la commande.
4. MS3 publie l'événement métier `OrderConfirmed`.
5. MS4 reçoit cet événement et prépare la livraison.

MS1 fournit l'identité du client. Les références partagées (`userId`, `productId`) sont échangées sans recopier toutes les données des autres services.

## Données
Base proposée : **MySQL**. 

## Technologies proposées
- Spring Boot.


## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Khaled
