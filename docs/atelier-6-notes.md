# Atelier 6 — Clean code, DTO et validation

## Architecture

- Les contrôleurs n'exposent aucune entité JPA : leurs réponses sont des DTO dédiés (`*Response`), y compris les listes, créations, lectures et mises à jour.
- Les neuf composants `*Mapper` centralisent les conversions entre DTO et entités. Les identifiants des associations sont résolus via les services avant d'être transmis au mapper.
- Les services conservent leurs responsabilités métier et travaillent avec les entités; les contrôleurs ne contiennent plus de conversion manuelle de réponse.
- Les paiements restent en lecture seule et sont créés avec leur contrat conformément aux règles actuelles du projet.

## Validation et erreurs

`spring-boot-starter-validation` est déclaré dans `pom.xml`. Les DTO de requête appliquent les contraintes adaptées aux champs : valeurs obligatoires, chaînes non blanches et tailles maximales, format email, montants et identifiants positifs, enums/date obligatoires, et cohérence des dates de réservation et de maintenance. Les listes d'équipements peuvent être vides, mais chaque identifiant fourni doit être positif.

`ApiExceptionHandler` uniformise les réponses `ApiError` :

| Cas | HTTP |
|---|---:|
| DTO invalide, paramètre invalide ou corps JSON mal formé | 400 |
| Ressource ou association référencée absente | 404 |
| Méthode HTTP non prise en charge | 405 |
| Suppression ou écriture bloquée par une contrainte d'intégrité | 409 |

## Vérification et captures

Lancer les tests avec `mvn test`. La suite d'intégration vérifie les DTO de réponse sur les opérations CRUD, l'absence de relations JPA imbriquées, les erreurs de validation/JSON/identifiant, ainsi que le conflit d'intégrité.

Captures réalisées avec le profil de démonstration :

- [Swagger UI et ressources documentées](captures/swagger-ui.png)
- [Réponse de lecture GET](captures/atelier-6-get-dto.png)
- [Réponse de création POST](captures/atelier-6-post-dto.png)
- [Réponse d'erreur de validation](captures/atelier-6-validation-400.png)
