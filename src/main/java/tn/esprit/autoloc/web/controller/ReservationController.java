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
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.service.IClientService;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.service.IVehiculeService;
import tn.esprit.autoloc.web.dto.ApiError;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;

import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Gestion des réservations de véhicules")
public class ReservationController {

    private final IReservationService reservationService;
    private final IVehiculeService vehiculeService;
    private final IClientService clientService;

    @PostMapping
    @Operation(summary = "Créer une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Réservation créée"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou dates incohérentes",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Client ou véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@Valid @RequestBody ReservationRequest request) {
        return toResponse(reservationService.create(toEntity(request)));
    }

    @GetMapping
    @Operation(summary = "Lister les réservations")
    @ApiResponse(responseCode = "200", description = "Liste des réservations")
    public List<ReservationResponse> findAll() {
        return reservationService.findAll().stream().map(ReservationController::toResponse).toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ReservationResponse findById(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        return toResponse(reservationService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Réservation modifiée"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou dates incohérentes",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = "Réservation, client ou véhicule introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    public ReservationResponse update(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id,
                                      @Valid @RequestBody ReservationRequest request) {
        return toResponse(reservationService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une réservation")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Réservation supprimée"),
            @ApiResponse(responseCode = "404", description = "Réservation introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Parameter(description = "Identifiant de la réservation") @PathVariable Long id) {
        reservationService.deleteById(id);
    }

    private Reservation toEntity(ReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        reservation.setStatut(request.statut());
        reservation.setVehicule(vehiculeService.findById(request.vehiculeId()));
        reservation.setClient(clientService.findById(request.clientId()));
        return reservation;
    }

    private static ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getIdReservation(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatut(),
                reservation.getVehicule().getIdVehicule(),
                reservation.getClient().getIdClient(),
                reservation.getContrat() == null ? null : reservation.getContrat().getIdContrat()
        );
    }
}
