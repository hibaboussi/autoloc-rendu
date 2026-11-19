package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.service.IReservationService;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

import java.math.BigDecimal;
import java.util.List;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IContratRepository contratRepository;
    private final ReservationMapper reservationMapper;

    @Override
    public Reservation create(Reservation reservation) {
        reservation.setIdReservation(null);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation findById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation introuvable : " + id));
    }

    @Override
    public Reservation update(Long id, Reservation reservation) {
        findById(id);
        reservation.setIdReservation(id);
        return reservationRepository.save(reservation);
    }

    @Override
    public void deleteById(Long id) {
        reservationRepository.delete(findById(id));
    }

    @Override
    public ReservationResponseDTO affecterReservationAClient(Long idReservation, String nom, String prenom) {
        Reservation reservation = findById(idReservation);
        List<Client> clients = clientRepository.findAllByNomIgnoreCaseAndPrenomIgnoreCase(nom, prenom);
        if (clients.isEmpty()) {
            throw new ResourceNotFoundException("Client introuvable : " + nom + " " + prenom);
        }
        if (clients.size() > 1) {
            throw new IllegalArgumentException("Plusieurs clients correspondent à ce nom et prénom.");
        }

        reservation.setClient(clients.get(0));
        return reservationMapper.toResponseDTO(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponseDTO creerReservationEtContratAssocie(
            ReservationWithContratRequestDTO requestDTO) {
        ReservationRequest reservationRequest = requestDTO.reservation();
        Vehicule vehicule = vehiculeRepository.findById(reservationRequest.vehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Véhicule introuvable : " + reservationRequest.vehiculeId()));
        Client client = clientRepository.findById(reservationRequest.clientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client introuvable : " + reservationRequest.clientId()));

        Reservation reservation = new Reservation();
        reservation.setDateDebut(reservationRequest.dateDebut());
        reservation.setDateFin(reservationRequest.dateFin());
        reservation.setStatut(reservationRequest.statut());
        reservation.setVehicule(vehicule);
        reservation.setClient(client);
        Reservation savedReservation = reservationRepository.save(reservation);

        long nombreJours = Math.max(1,
                ChronoUnit.DAYS.between(savedReservation.getDateDebut(), savedReservation.getDateFin()));
        Contrat contrat = new Contrat();
        contrat.setDateSignature(requestDTO.dateSignature());
        contrat.setMontantTotal(vehicule.getTarifJournalier().multiply(BigDecimal.valueOf(nombreJours)));
        contrat.setValide(true);
        contrat.setReservation(savedReservation);
        Contrat savedContrat = contratRepository.save(contrat);
        savedReservation.setContrat(savedContrat);

        return reservationMapper.toResponseDTO(savedReservation);
    }
}
