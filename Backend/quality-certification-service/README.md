# MS6 — Quality & Certification Management

## Présentation
Microservice responsable des contrôles qualité, des résultats de conformité et des certifications.

## Fonctionnalités

### Gestion de la qualité
- Enregistrer un contrôle qualité et sa date.
- Identifier l'inspecteur ayant effectué le contrôle.
- Enregistrer les résultats du contrôle.
- Définir si la récolte ou le lot est conforme ou non conforme.

### Gestion des certifications
- Enregistrer une certification et identifier l'organisme certificateur.
- Ajouter une date de délivrance et une date d'expiration.
- Consulter les certificats et vérifier leur validité.

## Entités principales
| Entité | Description |
|---|---|
| `QualityInspection` | Contrôle effectué sur une récolte ou un lot, avec inspecteur, date, résultats et conclusion de conformité. |
| `Certification` | Certificat associé à un produit, lot ou acteur, avec organisme émetteur et période de validité. |

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/inspections` | Enregistrer un contrôle qualité. |
| GET | `/api/inspections/recolte/{lotId}` | Consulter les contrôles d'une récolte ou d'un lot. |
| POST | `/api/certifications` | Ajouter une certification. |

## Références fonctionnelles
- Le lot de récolte constitue une référence importante pour rattacher les contrôles et l'historique de traçabilité.
- L'organisme certificateur doit disposer d'un profil et d'autorisations adaptés pour enregistrer ou valider une certification.

## Données
Base proposée : **H2**.

## Technologies proposées
- Spring Boot.


## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Chaima

