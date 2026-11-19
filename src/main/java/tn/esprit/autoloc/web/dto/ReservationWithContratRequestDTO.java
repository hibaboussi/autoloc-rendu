package tn.esprit.autoloc.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ReservationWithContratRequestDTO(
        @NotNull @Valid ReservationRequest reservation,
        @NotNull LocalDate dateSignature
) {
}
