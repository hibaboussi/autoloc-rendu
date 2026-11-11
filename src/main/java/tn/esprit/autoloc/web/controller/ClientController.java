package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.ClientRequest;
import tn.esprit.autoloc.web.dto.ClientResponse;

import java.util.List;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Gestion des clients")
public class ClientController {

    private final IClientService clientService;

    @PostMapping
    @Operation(summary = "Créer un client")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Client créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ClientResponse create(@Valid @RequestBody ClientRequest request) {
        return toResponse(clientService.create(toEntity(request)));
    }

    @GetMapping
    @Operation(summary = "Lister les clients")
    @ApiResponse(responseCode = "200", description = "Liste des clients")
    public List<ClientResponse> findAll() {
        return clientService.findAll().stream()
                .map(ClientController::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client trouvé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ClientResponse findById(@Parameter(description = "Identifiant du client") @PathVariable Long id) {
        return toResponse(clientService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un client")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Client modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Client introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ClientResponse update(@Parameter(description = "Identifiant du client") @PathVariable Long id,
                                 @Valid @RequestBody ClientRequest request) {
        return toResponse(clientService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Client supprimé"),
            @ApiResponse(responseCode = "404", description = "Client introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du client") @PathVariable Long id) {
        clientService.deleteById(id);
    }

    private static Client toEntity(ClientRequest request) {
        Client client = new Client();
        client.setNom(request.nom());
        client.setPrenom(request.prenom());
        client.setEmail(request.email());
        client.setTelephone(request.telephone());
        client.setNumPermis(request.numPermis());
        client.setDateInscription(request.dateInscription());
        return client;
    }

    private static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getIdClient(),
                client.getNom(),
                client.getPrenom(),
                client.getEmail(),
                client.getTelephone(),
                client.getNumPermis(),
                client.getDateInscription()
        );
    }
}
