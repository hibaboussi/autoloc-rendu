package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AgenceRequest(
        @NotBlank @Size(max = 50) String nom,
        @NotBlank @Size(max = 50) String ville,
        @NotBlank @Size(max = 100) String adresse,
        @NotBlank @Size(max = 20) String telephone
) {
}
