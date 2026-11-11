package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
import tn.esprit.autoloc.domain.StatutReservation;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin,
        @NotNull StatutReservation statut,
        @NotNull @Positive Long vehiculeId,
        @NotNull @Positive Long clientId
) {
    @AssertTrue(message = "La date de fin doit être égale ou postérieure à la date de début")
    public boolean isDateRangeValid() {
        return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
    }
}
