package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.VehiculeRequest;
import tn.esprit.autoloc.web.dto.VehiculeResponse;

import java.util.List;

@Component
public class VehiculeMapper {

    public Vehicule toEntity(VehiculeRequest request, Agence agence, List<Equipement> equipements) {
        Vehicule vehicule = new Vehicule();
        vehicule.setImmatriculation(request.immatriculation());
        vehicule.setMarque(request.marque());
        vehicule.setModele(request.modele());
        vehicule.setCategorie(request.categorie());
        vehicule.setTarifJournalier(request.tarifJournalier());
        vehicule.setStatut(request.statut());
        vehicule.setAgence(agence);
        vehicule.setEquipements(equipements);
        return vehicule;
    }

    public VehiculeResponse toResponse(Vehicule vehicule) {
        return new VehiculeResponse(
                vehicule.getIdVehicule(),
                vehicule.getImmatriculation(),
                vehicule.getMarque(),
                vehicule.getModele(),
                vehicule.getCategorie(),
                vehicule.getTarifJournalier(),
                vehicule.getStatut(),
                vehicule.getAgence().getIdAgence()
        );
    }
}
