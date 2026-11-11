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
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.service.IMaintenanceService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.MaintenanceRequest;
import tn.esprit.autoloc.web.dto.MaintenanceResponse;

import java.util.List;

@RestController
@RequestMapping("/maintenances")
@RequiredArgsConstructor
@Tag(name = "Maintenances", description = "Gestion des maintenances de véhicules")
public class MaintenanceController {

    private final IMaintenanceService maintenanceService;
    private final IVehiculeService vehiculeService;

    @PostMapping
    @Operation(summary = "Planifier une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Maintenance créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public MaintenanceResponse create(@Valid @RequestBody MaintenanceRequest request) {
        return toResponse(maintenanceService.create(toEntity(request)));
    }

    @GetMapping
    @Operation(summary = "Lister les maintenances")
    @ApiResponse(responseCode = "200", description = "Liste des maintenances")
    public List<MaintenanceResponse> findAll() {
        return maintenanceService.findAll().stream().map(MaintenanceController::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance trouvée"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public MaintenanceResponse findById(@Parameter(description = "Identifiant de la maintenance") @PathVariable Long id) {
        return toResponse(maintenanceService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Maintenance modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Maintenance ou véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public MaintenanceResponse update(@Parameter(description = "Identifiant de la maintenance") @PathVariable Long id,
                                      @Valid @RequestBody MaintenanceRequest request) {
        return toResponse(maintenanceService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une maintenance")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Maintenance supprimée"),
            @ApiResponse(responseCode = "404", description = "Maintenance introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de la maintenance") @PathVariable Long id) {
        maintenanceService.deleteById(id);
    }

    private Maintenance toEntity(MaintenanceRequest request) {
        Maintenance maintenance = new Maintenance();
        maintenance.setDateDebut(request.dateDebut());
        maintenance.setDateFin(request.dateFin());
        maintenance.setDescription(request.description());
        maintenance.setVehicule(vehiculeService.findById(request.vehiculeId()));
        return maintenance;
    }

    private static MaintenanceResponse toResponse(Maintenance maintenance) {
        return new MaintenanceResponse(
                maintenance.getIdMaintenance(),
                maintenance.getDateDebut(),
                maintenance.getDateFin(),
                maintenance.getDescription(),
                maintenance.getVehicule().getIdVehicule()
        );
    }
}
