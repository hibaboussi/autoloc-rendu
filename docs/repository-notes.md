# Notes sur les repositories et les services

## Choix de l'interface Spring Data

Les neuf repositories (`IAgenceRepository`, `IClientRepository`, `IContratRepository`, `IEmployeRepository`, `IEquipementRepository`, `IMaintenanceRepository`, `IPaiementRepository`, `IReservationRepository` et `IVehiculeRepository`) étendent `JpaRepository<Entité, Long>`. Ce choix fournit le CRUD, des résultats sous forme de listes, ainsi que les opérations JPA et la pagination/tris, conformément au support de l'atelier.

## Couche métier

Chaque entité possède une interface de service et une implémentation dans `service` et `service.impl`. Les opérations de création remettent l'identifiant à `null` afin d'éviter qu'un identifiant fourni par le client ne remplace un enregistrement. Les mises à jour et suppressions vérifient d'abord l'existence de la ressource; une ressource absente lève `ResourceNotFoundException` et produit une réponse HTTP 404.

## CRUD exposé

Les endpoints REST de `Client` et `Equipement` implémentent `POST`, `GET` de collection, `GET` par identifiant, `PUT` et `DELETE`. Ils utilisent des DTO validés afin de contrôler les données d'entrée et d'éviter les cycles JSON dus aux associations bidirectionnelles.

## Vérification

Les tests unitaires ciblés couvrent la création, la mise à jour avec conservation de l'identifiant de l'URL et le cas d'une ressource inexistante pour les services Client et Equipement. L'analyse SonarQube for IDE n'a pas été exécutée dans cet environnement; les résultats éventuels doivent être relevés dans l'IDE après analyse du projet.
