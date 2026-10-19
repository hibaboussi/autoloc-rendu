# AutoLoc API

Projet Spring Boot / Maven créé dans le cadre de l'atelier 1.

## Stack
- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- MySQL
- Lombok
- Maven

## Configuration
Le fichier `src/main/resources/application.properties` contient la configuration JDBC MySQL et les options Hibernate.

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
Le CRUD REST complet est exposé pour les clients et les équipements :

| Entité | Endpoint |
|---|---|
| Client | `/api/clients` |
| Équipement | `/api/equipements` |

Pour les deux ressources : `POST` crée (201), `GET` liste ou lit par identifiant, `PUT /{id}` met à jour et `DELETE /{id}` supprime (204). Les corps JSON sont validés; les réponses utilisent des DTO afin de ne pas sérialiser récursivement les associations JPA. Les autres entités disposent de leurs repositories et services, prêts à être exposés dans un atelier suivant.

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
