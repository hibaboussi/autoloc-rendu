package tn.esprit.autoloc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.web.dto.ContratDTO;
import tn.esprit.autoloc.web.mapper.ContratMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratAssignmentServiceTest {

    @Mock
    private IContratRepository contratRepository;
    @Mock
    private IReservationRepository reservationRepository;
    @Mock
    private ContratMapper contratMapper;

    @InjectMocks
    private ContratServiceImpl contratService;

    @Test
    void affecterContratAReservationCreatesContractForMatchingPeriod() {
        LocalDate start = LocalDate.of(2026, 12, 1);
        LocalDate end = LocalDate.of(2026, 12, 4);
        LocalDate signature = LocalDate.of(2026, 11, 19);
        Vehicule vehicule = new Vehicule();
        vehicule.setTarifJournalier(new BigDecimal("80.00"));
        Reservation reservation = new Reservation();
        reservation.setIdReservation(7L);
        reservation.setDateDebut(start);
        reservation.setDateFin(end);
        reservation.setVehicule(vehicule);
        when(reservationRepository.findAllByDateDebutAndDateFin(start, end))
                .thenReturn(List.of(reservation));
        when(contratRepository.save(org.mockito.ArgumentMatchers.any(Contrat.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        ContratDTO response = new ContratDTO(8L, signature, new BigDecimal("240.00"), true, 7L, null);
        when(contratMapper.toDTO(org.mockito.ArgumentMatchers.any(Contrat.class))).thenReturn(response);

        ContratDTO result = contratService.affecterContratAReservation(signature, start, end);

        ArgumentCaptor<Contrat> contratCaptor = ArgumentCaptor.forClass(Contrat.class);
        verify(contratRepository).save(contratCaptor.capture());
        assertEquals(new BigDecimal("240.00"), contratCaptor.getValue().getMontantTotal());
        assertSame(reservation, contratCaptor.getValue().getReservation());
        assertSame(response, result);
    }
}
