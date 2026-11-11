package tn.esprit.autoloc.web.mapper;

import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponse;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequest request, Vehicule vehicule, Client client) {
        Reservation reservation = new Reservation();
        reservation.setDateDebut(request.dateDebut());
        reservation.setDateFin(request.dateFin());
        reservation.setStatut(request.statut());
        reservation.setVehicule(vehicule);
        reservation.setClient(client);
        return reservation;
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getIdReservation(),
                reservation.getDateDebut(),
                reservation.getDateFin(),
                reservation.getStatut(),
                reservation.getVehicule().getIdVehicule(),
                reservation.getClient().getIdClient(),
                reservation.getContrat() == null ? null : reservation.getContrat().getIdContrat()
        );
    }
}
