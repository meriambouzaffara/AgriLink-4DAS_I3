# API Gateway

## Présentation
Composant d'infrastructure commun qui sert de point d'entrée aux requêtes du frontend et les achemine vers les microservices AgriLink.

> Ce composant d'infrastructure est une proposition d'architecture. Le document fonctionnel décrit les sept domaines métier, mais ne spécifie pas la technologie ni la configuration de l'API Gateway.

## Responsabilités prévues
- Fournir un point d'entrée unique au frontend.
- Router les requêtes HTTP vers le microservice correspondant.
- Centraliser, si retenu par l'équipe, certains traitements transverses comme les contrôles d'accès et la configuration CORS.
- Éviter au frontend de devoir connaître directement toutes les adresses des microservices.

## Routage fonctionnel indicatif
| Préfixe | Microservice cible |
|---|---|
| `/api/users/**` | User Management |
| `/api/products/**`, `/api/recoltes/**` | Agricultural Management |
| `/api/orders/**` | Order Management |
| `/api/deliveries/**` | Delivery Management |
| `/api/cooperatives/**`, `/api/memberships/**` | Cooperative & Partnership Management |
| `/api/inspections/**`, `/api/certifications/**` | Quality & Certification Management |
| `/api/reviews/**`, `/api/complaints/**` | Customer Relations Management |

Les routes exactes devront être alignées sur les contrôleurs réellement développés.

## Technologies proposées
- Java et Spring Boot.
- Spring Cloud Gateway.
- Eureka Discovery Client si le Discovery Server est utilisé.

## Configuration et lancement
À compléter après la création du projet : version Java/Spring Boot, port, routes et configuration Eureka.

## Responsable
Infrastructure commune — travail coordonné par l'équipe.
