# Atelier 5 — API REST, Postman et Swagger

## A. Endpoints

Les contrôleurs sont minces : ils valident et convertissent les DTO, puis délèguent aux interfaces de service. Le préfixe `/api` est défini une fois dans `server.servlet.context-path`; les routes ci-dessous sont les URL HTTP complètes.

| Contrôleur | Verbe | Route | Service appelé | Succès | Erreurs |
|---|---|---|---|---|---|
| AgenceController | GET | `/api/agences` | `IAgenceService.findAll` | 200 | — |
| AgenceController | GET | `/api/agences/{id}` | `IAgenceService.findById` | 200 | 404 |
| AgenceController | POST | `/api/agences` | `IAgenceService.create` | 201 | 400 |
| AgenceController | PUT | `/api/agences/{id}` | `IAgenceService.update` | 200 | 400, 404 |
| AgenceController | DELETE | `/api/agences/{id}` | `IAgenceService.deleteById` | 204 | 404 |
| ClientController | GET | `/api/clients` | `IClientService.findAll` | 200 | — |
| ClientController | GET | `/api/clients/{id}` | `IClientService.findById` | 200 | 404 |
| ClientController | POST | `/api/clients` | `IClientService.create` | 201 | 400 |
| ClientController | PUT | `/api/clients/{id}` | `IClientService.update` | 200 | 400, 404 |
| ClientController | DELETE | `/api/clients/{id}` | `IClientService.deleteById` | 204 | 404 |
| ContratController | GET | `/api/contrats` | `IContratService.findAll` | 200 | — |
| ContratController | GET | `/api/contrats/{id}` | `IContratService.findById` | 200 | 404 |
| ContratController | POST | `/api/contrats` | `IContratService.create` | 201 | 400, 404 |
| ContratController | PUT | `/api/contrats/{id}` | `IContratService.update` | 200 | 400, 404 |
| ContratController | DELETE | `/api/contrats/{id}` | `IContratService.deleteById` | 204 | 404 |
| EmployeController | GET | `/api/employes` | `IEmployeService.findAll` | 200 | — |
| EmployeController | GET | `/api/employes/{id}` | `IEmployeService.findById` | 200 | 404 |
| EmployeController | POST | `/api/employes` | `IEmployeService.create` | 201 | 400, 404 |
| EmployeController | PUT | `/api/employes/{id}` | `IEmployeService.update` | 200 | 400, 404 |
| EmployeController | DELETE | `/api/employes/{id}` | `IEmployeService.deleteById` | 204 | 404 |
| EquipementController | GET | `/api/equipements` | `IEquipementService.findAll` | 200 | — |
| EquipementController | GET | `/api/equipements/{id}` | `IEquipementService.findById` | 200 | 404 |
| EquipementController | POST | `/api/equipements` | `IEquipementService.create` | 201 | 400 |
| EquipementController | PUT | `/api/equipements/{id}` | `IEquipementService.update` | 200 | 400, 404 |
| EquipementController | DELETE | `/api/equipements/{id}` | `IEquipementService.deleteById` | 204 | 404 |
| MaintenanceController | GET | `/api/maintenances` | `IMaintenanceService.findAll` | 200 | — |
| MaintenanceController | GET | `/api/maintenances/{id}` | `IMaintenanceService.findById` | 200 | 404 |
| MaintenanceController | POST | `/api/maintenances` | `IMaintenanceService.create` | 201 | 400, 404 |
| MaintenanceController | PUT | `/api/maintenances/{id}` | `IMaintenanceService.update` | 200 | 400, 404 |
| MaintenanceController | DELETE | `/api/maintenances/{id}` | `IMaintenanceService.deleteById` | 204 | 404 |
| PaiementController | GET | `/api/paiements` | `IPaiementService.findAll` | 200 | — |
| PaiementController | GET | `/api/paiements/{id}` | `IPaiementService.findById` | 200 | 404 |
| ReservationController | GET | `/api/reservations` | `IReservationService.findAll` | 200 | — |
| ReservationController | GET | `/api/reservations/{id}` | `IReservationService.findById` | 200 | 404 |
| ReservationController | POST | `/api/reservations` | `IReservationService.create` | 201 | 400, 404 |
| ReservationController | PUT | `/api/reservations/{id}` | `IReservationService.update` | 200 | 400, 404 |
| ReservationController | DELETE | `/api/reservations/{id}` | `IReservationService.deleteById` | 204 | 404 |
| VehiculeController | GET | `/api/vehicules` | `IVehiculeService.findAll` | 200 | — |
| VehiculeController | GET | `/api/vehicules/{id}` | `IVehiculeService.findById` | 200 | 404 |
| VehiculeController | POST | `/api/vehicules` | `IVehiculeService.create` | 201 | 400, 404 |
| VehiculeController | PUT | `/api/vehicules/{id}` | `IVehiculeService.update` | 200 | 400, 404 |
| VehiculeController | DELETE | `/api/vehicules/{id}` | `IVehiculeService.deleteById` | 204 | 404 |

Les erreurs JSON utilisent `ApiError` (`timestamp`, `status`, `error`, `message`). Les contraintes Bean Validation et les périodes incohérentes produisent 400; une ressource ou association liée absente produit 404. Paiement est volontairement en lecture seule : un `POST /api/paiements` reçoit 405. Le paiement est créé/supprimé par cascade avec son contrat.

## B. Annotations Jackson

| Association | Annotation | Justification |
|---|---|---|
| `Contrat.paiement` / `Paiement.contrat` | `@JsonManagedReference("contrat-paiement")` / `@JsonBackReference("contrat-paiement")` | Évite le cycle bidirectionnel si un contrat est sérialisé directement; le paiement appartient au contrat. Le modèle JPA actuel est une relation un-à-un, donc un paiement par contrat. |
| Autres relations bidirectionnelles | DTO de requête/réponse; aucune entité JPA directement exposée par les contrôleurs | Les DTO ne renvoient que les identifiants des associations et empêchent les cycles JSON sans masquer les relations sur les entités. |

`@JsonBackReference` exclut du JSON le côté enfant qui remonte vers son parent, tout en conservant le lien JPA et le lien inverse lors de la désérialisation. `@JsonIgnore` exclut entièrement une propriété de la sérialisation/désérialisation Jackson; il ne convient pas à une association propriétaire obligatoire reçue dans une requête.

## C. Postman et Swagger

La collection importable se trouve dans [`postman/AutoLoc.postman_collection.json`](../postman/AutoLoc.postman_collection.json), avec son environnement local [`postman/AutoLoc-local.postman_environment.json`](../postman/AutoLoc-local.postman_environment.json). Sélectionner l'environnement **AutoLoc - local**, puis lancer la collection dans l'ordre. Elle teste les créations, lectures, modifications et suppressions, les réponses 400/404, le refus 405 de création directe d'un paiement et la création/suppression en cascade du paiement associé à un contrat.

Postman envoie et rejoue les requêtes et vérifie les statuts attendus. SpringDoc génère le contrat OpenAPI depuis les contrôleurs; Swagger UI permet de parcourir et d'essayer les routes documentées. Le schéma OpenAPI est accessible sur `/api/v3/api-docs` et Swagger UI sur `/api/swagger-ui.html`.

Captures effectuées avec Swagger UI : [documentation générale](captures/swagger-ui.png), [GET agences — 200](captures/swagger-agences-get-200.png), [POST agence — 201](captures/swagger-agences-post-201.png), [POST invalide — 400](captures/swagger-agences-post-400.png).

## D. Points de qualité relevés

| Problème constaté pendant la revue | Risque | Correction |
|---|---|---|
| Le contrôleur Paiement exposait initialement POST, PUT et DELETE alors que la règle de l'atelier le réserve à la lecture. | Un paiement pouvait être créé ou modifié en dehors de son contrat et de sa cascade. | Le contrôleur n'expose que les deux GET; le POST non pris en charge répond 405. |
| Les DTO de maintenance et réservation ne contrôlaient pas l'ordre des dates. | Des périodes de location/entretien invalides pouvaient atteindre la couche métier. | Ajout d'une contrainte de classe `@AssertTrue`; une fin antérieure à un début répond 400. |
| Renvoyer des entités JPA avec des associations bidirectionnelles peut produire des cycles JSON. | Sérialisation récursive et fuite de champs de persistance. | Les contrôleurs renvoient des DTO; la relation Contrat/Paiement possède en plus des références Jackson nommées. |

SonarQube for IDE n'est pas installé/exécuté dans cet environnement; ces points sont issus de la revue et des tests, et ne sont pas présentés comme des résultats de Sonar.

## E. Questions de compréhension

1. **Quel composant reçoit les requêtes HTTP ?** Le `DispatcherServlet` reçoit la requête et la délègue au contrôleur/méthode correspondant aux mappings Spring MVC (verbe HTTP et route).
2. **Pourquoi injecter `IContratService` ?** Le contrôleur dépend du contrat de service, pas d'une implémentation ou d'un repository; cela garde les couches découplées et facilite les tests et changements d'implémentation.
3. **Pourquoi 201, 204 et 404 ?** 201 indique qu'une ressource a été créée; 204 confirme la suppression sans corps; 404 indique que l'identifiant ne correspond à aucune ressource. L'exception métier est traduite par le gestionnaire REST au lieu d'être une erreur serveur 500.
4. **Pourquoi Paiement est-il en lecture seule ?** Le domaine fait créer le paiement par le contrat via `CascadeType.ALL`; un POST direct répond 405. La suppression du contrat supprime aussi son paiement.
5. **Quels risques présente l'exposition d'entités JPA ?** Elle couple l'API au schéma interne, risque d'exposer des champs et peut sérialiser récursivement des relations. Les DTO découplent les réponses; la paire Managed/Back Reference protège en outre la composition Contrat/Paiement. `@JsonIgnore`, lui, masque complètement une propriété plutôt que de traiter le parent dans une relation bidirectionnelle.

## Vérification

`mvn test` exécute les tests unitaires et le scénario d'intégration MockMvc avec H2. Pour voir Swagger et exécuter manuellement la collection Postman sans configurer MySQL, démarrer le profil de démonstration décrit dans le README.
