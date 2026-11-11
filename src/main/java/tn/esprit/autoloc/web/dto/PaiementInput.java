package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import tn.esprit.autoloc.domain.ModePaiement;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PaiementInput(
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal montant,
        @NotNull LocalDate datePaiement,
        @NotNull ModePaiement modePaiement
) {
}
