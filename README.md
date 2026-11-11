# AutoLoc API

Projet Spring Boot / Maven créé dans le cadre de l'atelier 1.

## Stack
- Java 17
- Spring Boot 3.3.5
- SpringDoc OpenAPI 2.6.0 / Swagger UI
- Spring Data JPA
- MySQL
- Lombok
- Maven

## Configuration
Le fichier `src/main/resources/application.properties` contient la configuration JDBC MySQL et les options Hibernate. Le préfixe `/api` est défini par `server.servlet.context-path`. Un profil `demo` utilise une base H2 en mémoire pour lancer l'API et essayer Swagger/Postman sans configurer MySQL.

## Lancer avec WAMP / MySQL

1. Démarrer les services **Apache** et **MySQL** depuis Wampserver.
2. Vérifier la connexion à phpMyAdmin sur `http://localhost:8080/phpmyadmin/`. La configuration du projet utilise `root` sans mot de passe; adapter `spring.datasource.username` et `spring.datasource.password` dans `src/main/resources/application.properties` si le compte MySQL local est différent.
3. Démarrer l'application avec le profil MySQL normal. L'URL JDBC crée `autoloc_db` si elle n'existe pas et Hibernate (`ddl-auto=update`) crée/met à jour les dix tables depuis les entités; aucune suppression de tables existantes n'est nécessaire.

Dans cette configuration WAMP, le port `8080` est utilisé par Apache. Lancer Spring Boot sur `18082` pour éviter le conflit :

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=18082"
```

Ouvrir ensuite Swagger sur `http://localhost:18082/api/swagger-ui.html`. La base contient initialement les tables mais aucune donnée métier; créer les premiers enregistrements via l'API, en commençant par une agence et un équipement avant un véhicule.

La structure générée dans phpMyAdmin est visible dans [la capture de la base AutoLoc](docs/captures/wamp-autoloc-database.png).

## Entités créées
- Vehicule
- Agence
- Client
- Employe
- Equipement
- Reservation
- Contrat
- Paiement
- Maintenance

## Associations JPA
- Une agence possède plusieurs employés et véhicules.
- Un véhicule appartient à une agence et peut avoir plusieurs maintenances et réservations.
- Les véhicules et les équipements sont liés plusieurs-à-plusieurs.
- Un client peut effectuer plusieurs réservations; chaque réservation concerne un véhicule et un client.
- Une réservation peut être rattachée à un contrat; chaque contrat est rattaché à une réservation et à un paiement.

Le schéma comprend **9 tables d'entités** et **1 table de jointure** (`vehicule_equipement`), soit **10 tables** au total. Les énumérations sont stockées dans les tables de leurs entités respectives.

## Repositories et services
Le package `repository` contient une interface `I…Repository` qui étend `JpaRepository<Entité, Long>` pour chacune des neuf entités. Les packages `service` et `service.impl` contiennent respectivement les neuf interfaces métier et leurs implémentations. Chaque service propose les opérations de création, lecture de la liste, lecture par identifiant, mise à jour et suppression; une ressource absente déclenche une réponse HTTP 404 lorsqu'elle est appelée depuis l'API.

## API CRUD
Les contrôleurs REST documentés avec SpringDoc exposent :

| Entité | Endpoint |
|---|---|
| Agence | `/api/agences` |
| Client | `/api/clients` |
| Contrat | `/api/contrats` |
| Employé | `/api/employes` |
| Équipement | `/api/equipements` |
| Maintenance | `/api/maintenances` |
| Paiement | `/api/paiements` |
| Réservation | `/api/reservations` |
| Véhicule | `/api/vehicules` |

Pour Agence, Client, Contrat, Employé, Équipement, Maintenance, Réservation et Véhicule : `POST` crée (201), `GET` liste ou lit par identifiant, `PUT /{id}` met à jour et `DELETE /{id}` supprime (204). Les paiements sont en lecture seule (`GET` collection et détail); ils sont créés et supprimés avec leur contrat par cascade. Les corps JSON sont validés; les associations sont fournies par identifiant dans les requêtes et les réponses utilisent des DTO afin d'éviter les cycles JSON des associations JPA.

La documentation interactive est disponible sur `http://localhost:8080/api/swagger-ui.html`; le document OpenAPI est disponible sur `http://localhost:8080/api/v3/api-docs`.

## Essai de l'atelier 5

Sans MySQL, démarrer l'application avec le profil de démonstration :

```powershell
mvn spring-boot:run "-Dspring-boot.run.profiles=demo"
```

Importer `postman/AutoLoc.postman_collection.json` et `postman/AutoLoc-local.postman_environment.json` dans Postman, sélectionner **AutoLoc - local**, puis exécuter la collection dans l'ordre. L'application normale conserve sa configuration MySQL. La collection et les détails des tests sont décrits dans [docs/api-notes.md](docs/api-notes.md).

Captures Swagger de vérification : [page Swagger](docs/captures/swagger-ui.png), [GET agences — 200](docs/captures/swagger-agences-get-200.png), [POST agence — 201](docs/captures/swagger-agences-post-201.png), [validation — 400](docs/captures/swagger-agences-post-400.png). Elles ont été prises sur le profil `demo` (port 18081, le port 8080 étant déjà occupé lors de la capture).

## Atelier 6 — DTO, mappers et validation

Toutes les réponses REST utilisent les DTO du package `web.dto`; les neuf mappers du package `web.mapper` convertissent les entités et les requêtes. Les contrôleurs restent minces et délèguent les opérations métier aux services.

La dépendance `spring-boot-starter-validation` active les contraintes Jakarta présentes sur les DTO d'entrée (`@NotBlank`, `@Email`, `@Positive`, limites de taille et validation des périodes). Les identifiants d'équipement sont validés lorsqu'ils sont fournis, et une liste vide reste autorisée. Les erreurs sont uniformisées en JSON par `ApiExceptionHandler` : validation et corps invalides (400), ressource absente (404), méthode HTTP non autorisée (405) et conflit d'intégrité (409).

La note de l'atelier et les captures d'exécution sont disponibles dans [docs/atelier-6-notes.md](docs/atelier-6-notes.md) et [docs/captures](docs/captures).

## Génération du schéma
La configuration finale utilise `spring.jpa.hibernate.ddl-auto=update`. Pour recréer le schéma, vérifier d'abord que MySQL est démarré et que la base `autoloc_db` ne contient aucune donnée à conserver, puis lancer temporairement l'application avec `create` :

```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--spring.jpa.hibernate.ddl-auto=create
```

Hibernate supprime alors les tables existantes et crée le schéma. Vérifier les 10 tables dans MySQL, puis relancer l'application sans cet argument : la configuration persistante reste `update`.

## Démarrage
```bash
mvn spring-boot:run
```

## Tests
```bash
mvn test
```
