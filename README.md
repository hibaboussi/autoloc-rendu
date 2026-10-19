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
