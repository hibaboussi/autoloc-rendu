package tn.esprit.autoloc.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ClientRequest(
        @NotBlank @Size(max = 50) String nom,
        @NotBlank @Size(max = 50) String prenom,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(max = 20) String telephone,
        @NotBlank @Size(max = 20) String numPermis,
        @NotNull LocalDate dateInscription
) {
}
