package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.web.dto.ContratRequest;
import tn.esprit.autoloc.web.dto.ContratResponse;

@Component
public class ContratMapper {

    public Contrat toEntity(ContratRequest request, Reservation reservation) {
        Contrat contrat = new Contrat();
        contrat.setDateSignature(request.dateSignature());
        contrat.setMontantTotal(request.montantTotal());
        contrat.setValide(request.valide());
        contrat.setReservation(reservation);
        if (request.paiement() != null) {
            Paiement paiement = new Paiement();
            paiement.setMontant(request.paiement().montant());
            paiement.setDatePaiement(request.paiement().datePaiement());
            paiement.setModePaiement(request.paiement().modePaiement());
            paiement.setContrat(contrat);
            contrat.setPaiement(paiement);
        }
        return contrat;
    }

    public ContratResponse toResponse(Contrat contrat) {
        return new ContratResponse(
                contrat.getIdContrat(),
                contrat.getDateSignature(),
                contrat.getMontantTotal(),
                contrat.getValide(),
                contrat.getReservation().getIdReservation(),
                contrat.getPaiement() == null ? null : contrat.getPaiement().getIdPaiement()
        );
    }
}
