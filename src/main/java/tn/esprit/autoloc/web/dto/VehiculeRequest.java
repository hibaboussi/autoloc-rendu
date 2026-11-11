package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;

import java.math.BigDecimal;
import java.util.List;

public record VehiculeRequest(
        @NotBlank @Size(max = 20) String immatriculation,
        @NotBlank @Size(max = 50) String marque,
        @NotBlank @Size(max = 50) String modele,
        @NotNull CategorieVehicule categorie,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal tarifJournalier,
        @NotNull StatutVehicule statut,
        @NotNull @Positive Long agenceId,
        @NotNull List<@NotNull @Positive Long> equipementIds
) {
}
