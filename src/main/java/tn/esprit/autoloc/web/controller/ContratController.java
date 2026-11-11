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
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;
import tn.esprit.autoloc.web.mapper.ContratMapper;

import java.util.List;

@RestController
@RequestMapping("/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats", description = "Gestion des contrats de location")
public class ContratController {

    private final IContratService contratService;
    private final IReservationService reservationService;
    private final ContratMapper contratMapper;

    @PostMapping
    @Operation(summary = "Créer un contrat avec son paiement optionnel")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contrat créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ContratResponse create(@Valid @RequestBody ContratRequest request) {
        return contratMapper.toResponse(contratService.create(
                contratMapper.toEntity(request, reservationService.findById(request.reservationId()))));
    }

    @GetMapping
    @Operation(summary = "Lister les contrats")
    @ApiResponse(responseCode = "200", description = "Liste des contrats")
    public List<ContratResponse> findAll() {
        return contratService.findAll().stream().map(contratMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un contrat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat trouvé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ContratResponse findById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        return contratMapper.toResponse(contratService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un contrat sans remplacer son paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contrat modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ContratResponse update(@Parameter(description = "Identifiant du contrat") @PathVariable Long id,
                                  @Valid @RequestBody ContratRequest request) {
        return contratMapper.toResponse(contratService.update(
                id, contratMapper.toEntity(request, reservationService.findById(request.reservationId()))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un contrat et son paiement associé")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contrat supprimé"),
            @ApiResponse(responseCode = "404", description = "Contrat introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du contrat") @PathVariable Long id) {
        contratService.deleteById(id);
    }
}
