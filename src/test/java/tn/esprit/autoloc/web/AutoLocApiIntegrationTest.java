package tn.esprit.autoloc.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("demo")
class AutoLocApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void documentsAndExposesCrudEndpointsAndCascadesContractPayment() throws Exception {
        MvcResult swaggerResult = mockMvc.perform(apiRequest(HttpMethod.GET, "/api/swagger-ui.html"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        assertTrue(swaggerResult.getResponse().getHeader("Location").contains("swagger-ui"));

        JsonNode openApi = objectMapper.readTree(get("/api/v3/api-docs", 200).getResponse().getContentAsString());
        assertTrue(openApi.path("info").path("title").asText().contains("AutoLoc API"));
        assertTrue(openApi.path("paths").has("/agences"));
        assertTrue(openApi.path("paths").has("/clients"));
        assertTrue(openApi.path("paths").has("/vehicules"));

        long agenceId = create("/api/agences",
                "{\"nom\":\"Agence test\",\"ville\":\"Tunis\",\"adresse\":\"10 rue test\",\"telephone\":\"71000001\"}",
                "idAgence");
        get("/api/agences", 200);
        get("/api/agences/" + agenceId, 200);
        get("/api/agences/999999", 404);
        MvcResult invalidId = get("/api/agences/invalide", 400);
        assertEquals(400, objectMapper.readTree(invalidId.getResponse().getContentAsString()).path("status").asInt());
        post("/api/agences",
                "{\"nom\":\"\",\"ville\":\"Tunis\",\"adresse\":\"Adresse\",\"telephone\":\"71000002\"}", 400);
        put("/api/agences/" + agenceId,
                "{\"nom\":\"Agence modifiée\",\"ville\":\"Tunis\",\"adresse\":\"10 rue test\",\"telephone\":\"71000003\"}",
                200);

        String unique = Long.toString(System.nanoTime());
        long clientId = create("/api/clients",
                "{\"nom\":\"Client\",\"prenom\":\"Test\",\"email\":\"client-" + unique
                        + "@example.com\",\"telephone\":\"22000001\",\"numPermis\":\"P-" + unique
                        + "\",\"dateInscription\":\"2026-11-11\"}", "idClient");
        get("/api/clients", 200);
        get("/api/clients/" + clientId, 200);
        get("/api/clients/999999", 404);
        post("/api/clients",
                "{\"nom\":\"\",\"prenom\":\"Test\",\"email\":\"invalide\",\"telephone\":\"22000002\","
                        + "\"numPermis\":\"INVALIDE\",\"dateInscription\":\"2026-11-11\"}", 400);
        put("/api/clients/" + clientId,
                "{\"nom\":\"Client modifié\",\"prenom\":\"Test\",\"email\":\"client-" + unique
                        + "@example.com\",\"telephone\":\"22000003\",\"numPermis\":\"P-" + unique
                        + "\",\"dateInscription\":\"2026-11-11\"}", 200);

        long equipementId = create("/api/equipements", "{\"libelle\":\"Climatisation\"}", "idEquipement");
        get("/api/equipements", 200);
        get("/api/equipements/" + equipementId, 200);
        get("/api/equipements/999999", 404);
        post("/api/equipements", "{\"libelle\":\"\"}", 400);
        put("/api/equipements/" + equipementId, "{\"libelle\":\"Climatisation modifiée\"}", 200);

        String vehicule = "{\"immatriculation\":\"AUT-" + unique
                + "\",\"marque\":\"Toyota\",\"modele\":\"Yaris\",\"categorie\":\"CITADINE\","
                + "\"tarifJournalier\":120,\"statut\":\"DISPONIBLE\",\"agenceId\":" + agenceId
                + ",\"equipementIds\":[" + equipementId + "]}";
        long vehiculeId = create("/api/vehicules", vehicule, "idVehicule");
        get("/api/vehicules", 200);
        get("/api/vehicules/" + vehiculeId, 200);
        get("/api/vehicules/999999", 404);
        post("/api/vehicules", vehicule.replace("\"tarifJournalier\":120", "\"tarifJournalier\":-1"), 400);
        put("/api/vehicules/" + vehiculeId,
                vehicule.replace("\"modele\":\"Yaris\"", "\"modele\":\"Yaris modifiée\""), 200);

        String employe = "{\"nom\":\"Employé\",\"prenom\":\"Test\",\"role\":\"AGENT\",\"agenceId\":"
                + agenceId + "}";
        long employeId = create("/api/employes", employe, "idEmploye");
        get("/api/employes", 200);
        get("/api/employes/" + employeId, 200);
        get("/api/employes/999999", 404);
        post("/api/employes", "{\"nom\":\"\",\"prenom\":\"Test\",\"role\":\"AGENT\",\"agenceId\":"
                + agenceId + "}", 400);
        MvcResult invalidEnum = post("/api/employes",
                "{\"nom\":\"Employé\",\"prenom\":\"Test\",\"role\":\"INCONNU\",\"agenceId\":"
                        + agenceId + "}", 400);
        JsonNode enumError = objectMapper.readTree(invalidEnum.getResponse().getContentAsString());
        assertEquals(400, enumError.path("status").asInt());
        assertTrue(enumError.path("message").asText().contains("JSON"));
        put("/api/employes/" + employeId,
                employe.replace("\"role\":\"AGENT\"", "\"role\":\"MANAGER\""), 200);

        String maintenance = "{\"dateDebut\":\"2026-11-11\",\"dateFin\":\"2026-11-12\","
                + "\"description\":\"Révision périodique\",\"vehiculeId\":" + vehiculeId + "}";
        long maintenanceId = create("/api/maintenances", maintenance, "idMaintenance");
        get("/api/maintenances", 200);
        get("/api/maintenances/" + maintenanceId, 200);
        get("/api/maintenances/999999", 404);
        post("/api/maintenances",
                maintenance.replace("\"dateFin\":\"2026-11-12\"", "\"dateFin\":\"2026-11-10\""), 400);
        put("/api/maintenances/" + maintenanceId,
                maintenance.replace("\"dateFin\":\"2026-11-12\"", "\"dateFin\":\"2026-11-13\""), 200);

        String reservation = "{\"dateDebut\":\"2026-12-01\",\"dateFin\":\"2026-12-03\","
                + "\"statut\":\"CONFIRMEE\",\"vehiculeId\":" + vehiculeId + ",\"clientId\":" + clientId + "}";
        long reservationId = create("/api/reservations", reservation, "idReservation");
        get("/api/reservations", 200);
        get("/api/reservations/" + reservationId, 200);
        get("/api/reservations/999999", 404);
        post("/api/reservations",
                reservation.replace("\"dateFin\":\"2026-12-03\"", "\"dateFin\":\"2026-11-30\""), 400);
        put("/api/reservations/" + reservationId,
                reservation.replace("\"dateFin\":\"2026-12-03\"", "\"dateFin\":\"2026-12-04\""), 200);

        String contrat = "{\"dateSignature\":\"2026-11-11\",\"montantTotal\":480,\"valide\":true,"
                + "\"reservationId\":" + reservationId
                + ",\"paiement\":{\"montant\":150,\"datePaiement\":\"2026-11-11\",\"modePaiement\":\"CARTE\"}}";
        MvcResult contratResult = post("/api/contrats", contrat, 201);
        JsonNode contratBody = objectMapper.readTree(contratResult.getResponse().getContentAsString());
        long contratId = contratBody.path("idContrat").asLong();
        long paiementId = contratBody.path("paiementId").asLong();
        assertTrue(contratId > 0);
        assertTrue(paiementId > 0);
        get("/api/contrats", 200);
        get("/api/contrats/" + contratId, 200);
        get("/api/contrats/999999", 404);
        get("/api/paiements", 200);
        get("/api/paiements/" + paiementId, 200);
        get("/api/paiements/999999", 404);
        post("/api/paiements", "{\"montant\":10}", 405);
        post("/api/contrats", contrat.replace("\"montantTotal\":480", "\"montantTotal\":-1"), 400);
        put("/api/contrats/" + contratId,
                "{\"dateSignature\":\"2026-11-11\",\"montantTotal\":520,\"valide\":true,"
                        + "\"reservationId\":" + reservationId + "}", 200);
        JsonNode updatedContrat = objectMapper.readTree(get("/api/contrats/" + contratId, 200)
                .getResponse().getContentAsString());
        assertEquals(520, updatedContrat.path("montantTotal").asInt());
        assertEquals(paiementId, updatedContrat.path("paiementId").asLong());

        MvcResult conflict = mockMvc.perform(apiRequest(HttpMethod.DELETE, "/api/agences/" + agenceId))
                .andExpect(status().isConflict())
                .andReturn();
        assertEquals(409, objectMapper.readTree(conflict.getResponse().getContentAsString()).path("status").asInt());

        delete("/api/contrats/" + contratId, 204);
        get("/api/paiements/" + paiementId, 404);
        delete("/api/reservations/" + reservationId, 204);
        delete("/api/maintenances/" + maintenanceId, 204);
        delete("/api/employes/" + employeId, 204);
        delete("/api/vehicules/" + vehiculeId, 204);
        delete("/api/equipements/" + equipementId, 204);
        delete("/api/clients/" + clientId, 204);
        delete("/api/agences/" + agenceId, 204);
        assertFalse(objectMapper.readTree(get("/api/clients", 200).getResponse().getContentAsString())
                .toString().contains("client-" + unique));
    }

    private long create(String path, String body, String idProperty) throws Exception {
        MvcResult result = post(path, body, 201);
        assertDtoPayload(result);
        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        long id = response.path(idProperty).asLong();
        assertTrue(id > 0, "La réponse de création doit contenir " + idProperty);
        return id;
    }

    private MvcResult post(String path, String body, int expectedStatus) throws Exception {
        return mockMvc.perform(apiRequest(HttpMethod.POST, path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(expectedStatus))
                .andReturn();
    }

    private MvcResult get(String path, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(apiRequest(HttpMethod.GET, path))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        if (expectedStatus == 200 && !path.equals("/api/v3/api-docs")) {
            assertDtoPayload(result);
        }
        return result;
    }

    private void put(String path, String body, int expectedStatus) throws Exception {
        MvcResult result = mockMvc.perform(apiRequest(HttpMethod.PUT, path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is(expectedStatus))
                .andReturn();
        if (expectedStatus == 200) {
            assertDtoPayload(result);
        }
    }

    private void delete(String path, int expectedStatus) throws Exception {
        mockMvc.perform(apiRequest(HttpMethod.DELETE, path))
                .andExpect(status().is(expectedStatus));
    }

    private static MockHttpServletRequestBuilder apiRequest(HttpMethod method, String path) {
        String servletPath = path.substring("/api".length());
        return MockMvcRequestBuilders.request(method, path)
                .contextPath("/api")
                .servletPath(servletPath);
    }

    private void assertDtoPayload(MvcResult result) throws Exception {
        JsonNode payload = objectMapper.readTree(result.getResponse().getContentAsString());
        if (payload.isArray()) {
            for (JsonNode item : payload) {
                assertDtoObject(item);
            }
        } else {
            assertDtoObject(payload);
        }
    }

    private static void assertDtoObject(JsonNode payload) {
        assertTrue(payload.isObject(), "Une réponse métier doit être un objet ou une liste de DTO");
        for (String entityRelation : new String[]{
                "agence", "client", "contrat", "employe", "equipements",
                "maintenance", "paiement", "reservation", "vehicule"
        }) {
            assertFalse(payload.has(entityRelation),
                    "La réponse ne doit pas exposer l'entité JPA imbriquée " + entityRelation);
        }
    }
}
