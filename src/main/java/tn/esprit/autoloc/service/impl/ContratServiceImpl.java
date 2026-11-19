package tn.esprit.autoloc.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final ContratMapper contratMapper;

    @Override
    public Contrat create(Contrat contrat) {
        contrat.setIdContrat(null);
        return contratRepository.save(contrat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Contrat> findAll() {
        return contratRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Contrat findById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat introuvable : " + id));
    }

    @Override
    public Contrat update(Long id, Contrat contrat) {
        Contrat existingContrat = findById(id);
        existingContrat.setDateSignature(contrat.getDateSignature());
        existingContrat.setMontantTotal(contrat.getMontantTotal());
        existingContrat.setValide(contrat.getValide());
        return contratRepository.save(existingContrat);
    }

    @Override
    public void deleteById(Long id) {
        contratRepository.delete(findById(id));
    }

    @Override
    public ContratDTO affecterContratAReservation(
            LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin) {
        List<Reservation> reservations = reservationRepository.findAllByDateDebutAndDateFin(dateDebut, dateFin);
        if (reservations.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Réservation introuvable pour la période " + dateDebut + " - " + dateFin);
        }
        if (reservations.size() > 1) {
            throw new IllegalArgumentException(
                    "Plusieurs réservations correspondent à cette période; impossible de choisir le contrat.");
        }

        Reservation reservation = reservations.get(0);
        if (reservation.getContrat() != null) {
            throw new IllegalArgumentException("Cette réservation possède déjà un contrat.");
        }

        long nombreJours = Math.max(1, ChronoUnit.DAYS.between(dateDebut, dateFin));
        BigDecimal montantTotal = reservation.getVehicule().getTarifJournalier()
                .multiply(BigDecimal.valueOf(nombreJours));
        Contrat contrat = new Contrat();
        contrat.setDateSignature(dateSignature);
        contrat.setMontantTotal(montantTotal);
        contrat.setValide(true);
        contrat.setReservation(reservation);

        Contrat savedContrat = contratRepository.save(contrat);
        reservation.setContrat(savedContrat);
        return contratMapper.toDTO(savedContrat);
    }
}
