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
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.service.IEquipementService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

import java.util.List;

@RestController
@RequestMapping("/equipements")
@RequiredArgsConstructor
@Tag(name = "Équipements", description = "Gestion des équipements")
public class EquipementController {

    private final IEquipementService equipementService;

    @PostMapping
    @Operation(summary = "Créer un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Équipement créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public EquipementResponse create(@Valid @RequestBody EquipementRequest request) {
        return toResponse(equipementService.create(toEntity(request)));
    }

    @GetMapping
    @Operation(summary = "Lister les équipements")
    @ApiResponse(responseCode = "200", description = "Liste des équipements")
    public List<EquipementResponse> findAll() {
        return equipementService.findAll().stream()
                .map(EquipementController::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement trouvé"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public EquipementResponse findById(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id) {
        return toResponse(equipementService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Équipement modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public EquipementResponse update(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id,
                                     @Valid @RequestBody EquipementRequest request) {
        return toResponse(equipementService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un équipement")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Équipement supprimé"),
            @ApiResponse(responseCode = "404", description = "Équipement introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'équipement") @PathVariable Long id) {
        equipementService.deleteById(id);
    }

    private static Equipement toEntity(EquipementRequest request) {
        Equipement equipement = new Equipement();
        equipement.setLibelle(request.libelle());
        return equipement;
    }

    private static EquipementResponse toResponse(Equipement equipement) {
        return new EquipementResponse(equipement.getIdEquipement(), equipement.getLibelle());
    }
}
