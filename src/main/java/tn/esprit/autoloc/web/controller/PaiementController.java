package tn.esprit.autoloc.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IPaiementService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.PaiementResponse;

import java.util.List;

@RestController
@RequestMapping("/paiements")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "Consultation des paiements créés avec leur contrat")
public class PaiementController {

    private final IPaiementService paiementService;

    @GetMapping
    @Operation(summary = "Lister les paiements")
    @ApiResponse(responseCode = "200", description = "Liste des paiements")
    public List<PaiementResponse> findAll() {
        return paiementService.findAll().stream().map(PaiementController::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un paiement")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Paiement trouvé"),
            @ApiResponse(responseCode = "404", description = "Paiement introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public PaiementResponse findById(@Parameter(description = "Identifiant du paiement") @PathVariable Long id) {
        return toResponse(paiementService.findById(id));
    }

    private static PaiementResponse toResponse(Paiement paiement) {
        return new PaiementResponse(
                paiement.getIdPaiement(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getModePaiement(),
                paiement.getContrat().getIdContrat()
        );
    }
}
