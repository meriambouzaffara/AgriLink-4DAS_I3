# Discovery Server

## Présentation
Composant d'infrastructure chargé de permettre l'enregistrement et la découverte des instances de microservices AgriLink.

> Eureka est une proposition technique pour réaliser cette fonction. Le document fonctionnel ne prescrit pas de technologie de découverte de services.

## Responsabilités prévues
- Démarrer le registre de services.
- Permettre aux microservices de s'y enregistrer.
- Permettre aux composants clients, notamment l'API Gateway, de découvrir les services enregistrés.
- Faciliter la communication sans coder en dur toutes les adresses des instances.

## Services concernés
- User Management
- Agricultural Management
- Order Management
- Delivery Management
- Cooperative & Partnership Management
- Quality & Certification Management
- Customer Relations Management
- API Gateway

## Technologies proposées
- Java et Spring Boot.
- Spring Cloud Netflix Eureka Server.
- Eureka Client dans les microservices et l'API Gateway.

## Configuration et lancement
À compléter après la création du projet : version Java/Spring Boot, port du serveur et configuration des clients Eureka.

## Responsable
Infrastructure commune — travail coordonné par l'équipe.
