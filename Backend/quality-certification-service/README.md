# MS6 — Quality & Certification Management

## Présentation
Le microservice MS6 assure le contrôle de la qualité des récoltes agricoles ainsi que la délivrance et le suivi des certifications agricoles. Il permet au responsable qualité et certifications de vérifier la conformité des récoltes, de gérer les certifications et de générer des rapports.
## Fonctionnalités

### Gestion des contrôles qualité
•	Ajouter un contrôle qualité à une récolte.
•	Modifier les informations d'un contrôle qualité.
•	Annuler un contrôle qualité.
•	Consulter les contrôles qualité et leur historique.

### Génération des rapports de contrôle qualité 
•	Générer automatiquement un rapport à partir des résultats des contrôles qualité.
•	Consulter les rapports générés.
•	Exporter les rapports au format PDF.
### Gestion des certifications agricoles 
•	Créer une certification et l'associer à une récolte conforme.
•	Modifier les informations d'une certification.
•	Archiver une certification.
•	Consulter et rechercher les certifications.
### Génération des rapports de certification 
•	Générer un rapport à partir des informations des certifications.
•	Consulter les rapports générés.
•	Exporter les rapports au format PDF.


## Entités principales
| Entité | Description |
|---|---|
| `	ControleQualite ` | Creprésente un contrôle effectué sur une récolte agricole afin de vérifier sa qualité et sa conformité |
| `Certification` | : représente une certification délivrée à une récolte ayant satisfait aux contrôles qualité. |
|  `Rapport `| : représente un document généré à partir des données des contrôles qualité ou des certifications. |

## API REST prévues dans le cadrage
| Méthode | Endpoint | Description |
|---|---|---|
| POST | `/api/qualite-agricole/controles` |ajouter un contrôle à une recolte. |
| GET | `/api/qualite-agricole/controles/{id}` | Consulter les contrôles d'une récolte ou d'un lot. |
| POST | /api/qualite-agricole/certifications` | Ajouter une certification. |


## Données
Base proposée : **H2**.
## Justification :
1.	Simplicité : H2 est une base de données légère, facile à installer et à utiliser.
2.	Intégration : elle s'intègre facilement avec Spring Boot et ne nécessite pas de serveur externe.
3.	Adaptation : elle prend en charge SQL et permet de gérer les données de MS6, notamment les contrôles qualité, les certifications et les rapports.
4.	Transactions : elle permet de garantir la cohérence des opérations sur les données


## Technologies proposées
- Spring Boot.


## Configuration et lancement
À compléter selon la configuration Spring Boot, le port et les variables d'environnement du service.

## Responsable
Chaima

