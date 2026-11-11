package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

import java.time.LocalDate;

public record MaintenanceRequest(
        @NotNull LocalDate dateDebut,
        @NotNull LocalDate dateFin,
        @NotBlank @Size(max = 500) String description,
        @NotNull @Positive Long vehiculeId
) {
    @AssertTrue(message = "La date de fin doit être égale ou postérieure à la date de début")
    public boolean isDateRangeValid() {
        return dateDebut == null || dateFin == null || !dateFin.isBefore(dateDebut);
    }
}
