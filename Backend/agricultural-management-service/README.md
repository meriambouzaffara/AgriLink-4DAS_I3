# MS2 — Agricultural Management

## Présentation
Microservice responsable de la publication des produits agricoles, de la déclaration des récoltes et des informations de stock.

## Fonctionnalités

### Gestion des produits
- Ajouter un produit agricole (tomate, pomme de terre, olive, orange, etc.).
- Modifier les informations d'un produit.
- Définir le prix et l'unité de vente (kg, tonne, caisse, etc.).
- Publier ou retirer un produit de la marketplace.
- Consulter les produits disponibles.

### Gestion des récoltes
- Déclarer une nouvelle récolte et enregistrer sa date.
- Définir l'exploitation agricole d'origine.
- Créer un lot de récolte avec un identifiant unique.

## Entités principales
| Entité | Description |
|---|---|
| `AgriculturalProduct` | Produit agricole publié sur la marketplace : désignation, prix, unité, disponibilité et référence du propriétaire. |
| `Récolte` | Récolte déclarée par un agriculteur, associée à une date, une origine et un identifiant de lot. |

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Ajouter un produit. |
| GET | `/api/products` | Lister les produits. |
| GET | `/api/products/{id}` | Consulter un produit. |
| PUT | `/api/products/{id}` | Modifier un produit. |
| POST | `/api/recoltes` | Déclarer une récolte. |

## Interactions
- MS1 permet d'identifier l'agriculteur propriétaire du produit.
- MS3 demande la vérification puis la réservation d'une quantité de stock lors de la création d'une commande.
- La réservation du stock doit être cohérente et atomique afin qu'une même quantité ne soit pas réservée simultanément pour plusieurs commandes.

## Données
Base proposée : **H2**. 

## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Nebras


