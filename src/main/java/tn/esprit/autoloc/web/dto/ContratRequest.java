package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratRequest(
        @NotNull LocalDate dateSignature,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal montantTotal,
        @NotNull Boolean valide,
        @NotNull @Positive Long reservationId,
        @Valid PaiementInput paiement
) {
}
