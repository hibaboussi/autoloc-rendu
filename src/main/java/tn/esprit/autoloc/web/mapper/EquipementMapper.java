package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.web.dto.EquipementRequest;
import tn.esprit.autoloc.web.dto.EquipementResponse;

@Component
public class EquipementMapper {

    public Equipement toEntity(EquipementRequest request) {
        Equipement equipement = new Equipement();
        equipement.setLibelle(request.libelle());
        return equipement;
    }

    public EquipementResponse toResponse(Equipement equipement) {
        return new EquipementResponse(equipement.getIdEquipement(), equipement.getLibelle());
    }
}
