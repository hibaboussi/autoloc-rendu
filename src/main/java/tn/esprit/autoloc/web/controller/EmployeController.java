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
import tn.esprit.autoloc.service.IAgenceService;
import tn.esprit.autoloc.service.IEmployeService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;
import tn.esprit.autoloc.web.mapper.EmployeMapper;

import java.util.List;

@RestController
@RequestMapping("/employes")
@RequiredArgsConstructor
@Tag(name = "Employés", description = "Gestion des employés")
public class EmployeController {

    private final IEmployeService employeService;
    private final IAgenceService agenceService;
    private final EmployeMapper employeMapper;

    @PostMapping
    @Operation(summary = "Créer un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employé créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Agence introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeResponse create(@Valid @RequestBody EmployeRequest request) {
        return employeMapper.toResponse(employeService.create(
                employeMapper.toEntity(request, agenceService.findById(request.agenceId()))));
    }

    @GetMapping
    @Operation(summary = "Lister les employés")
    @ApiResponse(responseCode = "200", description = "Liste des employés")
    public List<EmployeResponse> findAll() {
        return employeService.findAll().stream().map(employeMapper::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé trouvé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public EmployeResponse findById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        return employeMapper.toResponse(employeService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employé modifié"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Employé ou agence introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public EmployeResponse update(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id,
                                  @Valid @RequestBody EmployeRequest request) {
        return employeMapper.toResponse(employeService.update(
                id, employeMapper.toEntity(request, agenceService.findById(request.agenceId()))));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un employé")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employé supprimé"),
            @ApiResponse(responseCode = "404", description = "Employé introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de l'employé") @PathVariable Long id) {
        employeService.deleteById(id);
    }
}
