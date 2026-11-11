package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Employe;
import tn.esprit.autoloc.web.dto.EmployeRequest;
import tn.esprit.autoloc.web.dto.EmployeResponse;

@Component
public class EmployeMapper {

    public Employe toEntity(EmployeRequest request, Agence agence) {
        Employe employe = new Employe();
        employe.setNom(request.nom());
        employe.setPrenom(request.prenom());
        employe.setRole(request.role());
        employe.setAgence(agence);
        return employe;
    }

    public EmployeResponse toResponse(Employe employe) {
        return new EmployeResponse(
                employe.getIdEmploye(),
                employe.getNom(),
                employe.getPrenom(),
                employe.getRole(),
                employe.getAgence().getIdAgence()
        );
    }
}
