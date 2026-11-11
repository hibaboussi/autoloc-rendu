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
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.AgenceRequest;
import tn.esprit.autoloc.web.dto.AgenceResponse;

import java.util.List;

@RestController
@RequestMapping("/agences")
@RequiredArgsConstructor
@Tag(name = "Agences", description = "Gestion des agences de location")
public class AgenceController {

    private final IAgenceService agenceService;

    @PostMapping
    @Operation(summary = "Créer une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Agence créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public AgenceResponse create(@Valid @RequestBody AgenceRequest request) {
        return toResponse(agenceService.create(toEntity(request)));
    }

    @GetMapping
    @Operation(summary = "Lister les agences")
    @ApiResponse(responseCode = "200", description = "Liste des agences")
    public List<AgenceResponse> findAll() {
        return agenceService.findAll().stream().map(AgenceController::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence trouvée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public AgenceResponse findById(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id) {
        return toResponse(agenceService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Agence modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Agence introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public AgenceResponse update(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id,
                                 @Valid @RequestBody AgenceRequest request) {
        return toResponse(agenceService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une agence")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Agence supprimée"),
            @ApiResponse(responseCode = "404", description = "Agence introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'agence") @PathVariable Long id) {
        agenceService.deleteById(id);
    }

    private static Agence toEntity(AgenceRequest request) {
        Agence agence = new Agence();
        agence.setNom(request.nom());
        agence.setVille(request.ville());
        agence.setAdresse(request.adresse());
        agence.setTelephone(request.telephone());
        return agence;
    }

    private static AgenceResponse toResponse(Agence agence) {
        return new AgenceResponse(
                agence.getIdAgence(),
                agence.getNom(),
                agence.getVille(),
                agence.getAdresse(),
                agence.getTelephone()
        );
    }
}
