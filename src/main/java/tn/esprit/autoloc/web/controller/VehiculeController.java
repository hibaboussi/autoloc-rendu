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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IEquipementService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.VehiculeRequest;
import tn.esprit.autoloc.web.dto.VehiculeResponse;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

import java.util.List;

@RestController
@RequestMapping("/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion des véhicules de location")
public class VehiculeController {

    private final IVehiculeService vehiculeService;
    private final IAgenceService agenceService;
    private final IEquipementService equipementService;
    private final VehiculeMapper vehiculeMapper;

    @PostMapping
    @Operation(summary = "Créer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Véhicule créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Agence ou équipement introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public VehiculeResponse create(@Valid @RequestBody VehiculeRequest request) {
        return vehiculeMapper.toResponse(vehiculeService.create(toEntity(request)));
    }

    @GetMapping
    @Operation(summary = "Lister les véhicules")
    @ApiResponse(responseCode = "200", description = "Liste des véhicules")
    public List<VehiculeResponse> findAll() {
        return vehiculeService.findAll().stream().map(vehiculeMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule trouvé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public VehiculeResponse findById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        return vehiculeMapper.toResponse(vehiculeService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Véhicule modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public VehiculeResponse update(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
                                   @Valid @RequestBody VehiculeRequest request) {
        return vehiculeMapper.toResponse(vehiculeService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Véhicule supprimé"),
            @ApiResponse(responseCode = "404", description = "Véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant du véhicule") @PathVariable Long id) {
        vehiculeService.deleteById(id);
    }

    @PutMapping("/{id}/equipements/{equipementId}")
    @Operation(summary = "Affecter un équipement à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement affecté au véhicule"),
            @ApiResponse(responseCode = "404", description = "Véhicule ou équipement introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public VehiculeDTO affecterVehiculeAEquipement(
            @Parameter(description = "Identifiant du véhicule") @PathVariable Long id,
            @Parameter(description = "Identifiant de l'équipement") @PathVariable Long equipementId) {
        return vehiculeService.affecterVehiculeAEquipement(id, equipementId);
    }

    @PostMapping("/affecter-maintenance")
    @Operation(summary = "Affecter une maintenance existante à un véhicule")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance affectée au véhicule"),
            @ApiResponse(responseCode = "400", description = "Description de maintenance ambiguë",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Véhicule ou maintenance introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public VehiculeDTO affecterMaintenanceAvehicule(
            @RequestParam String description,
            @RequestParam String immatriculation) {
        return vehiculeService.affecterMaintenanceAvehicule(description, immatriculation);
    }

    private Vehicule toEntity(VehiculeRequest request) {
        return vehiculeMapper.toEntity(request,
                agenceService.findById(request.agenceId()),
                request.equipementIds().stream()
                .distinct()
                .map(equipementService::findById)
                .toList());
    }
}
