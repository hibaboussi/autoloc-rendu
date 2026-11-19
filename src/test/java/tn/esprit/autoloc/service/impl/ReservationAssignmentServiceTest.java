package tn.esprit.autoloc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.domain.StatutReservation;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.repository.IContratRepository;
import tn.esprit.autoloc.repository.IReservationRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.ReservationRequest;
import tn.esprit.autoloc.web.dto.ReservationResponseDTO;
import tn.esprit.autoloc.web.dto.ReservationWithContratRequestDTO;
import tn.esprit.autoloc.web.mapper.ReservationMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationAssignmentServiceTest {

    @Mock
    private IReservationRepository reservationRepository;
    @Mock
    private IClientRepository clientRepository;
    @Mock
    private IVehiculeRepository vehiculeRepository;
    @Mock
    private IContratRepository contratRepository;
    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void affecterReservationAClientUpdatesTheClientAndReturnsDto() {
        Reservation reservation = new Reservation();
        Client client = new Client();
        ReservationResponseDTO response = new ReservationResponseDTO(
                7L, null, null, null, 2L, 3L, null);
        when(reservationRepository.findById(7L)).thenReturn(Optional.of(reservation));
        when(clientRepository.findAllByNomIgnoreCaseAndPrenomIgnoreCase("Doe", "Jane"))
                .thenReturn(List.of(client));
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        when(reservationMapper.toResponseDTO(reservation)).thenReturn(response);

        ReservationResponseDTO result = reservationService.affecterReservationAClient(7L, "Doe", "Jane");

        assertSame(client, reservation.getClient());
        assertSame(response, result);
        verify(reservationRepository).save(reservation);
    }

    @Test
    void creerReservationEtContratAssocieCreatesBothEntitiesAndCalculatesTotal() {
        LocalDate start = LocalDate.of(2026, 12, 1);
        LocalDate end = LocalDate.of(2026, 12, 4);
        Client client = new Client();
        Vehicule vehicule = new Vehicule();
        vehicule.setTarifJournalier(new BigDecimal("50.00"));
        when(clientRepository.findById(3L)).thenReturn(Optional.of(client));
        when(vehiculeRepository.findById(2L)).thenReturn(Optional.of(vehicule));
        when(reservationRepository.save(org.mockito.ArgumentMatchers.any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation reservation = invocation.getArgument(0);
                    reservation.setIdReservation(7L);
                    return reservation;
                });
        when(contratRepository.save(org.mockito.ArgumentMatchers.any(Contrat.class)))
                .thenAnswer(invocation -> {
                    Contrat contrat = invocation.getArgument(0);
                    contrat.setIdContrat(8L);
                    return contrat;
                });
        ReservationResponseDTO response = new ReservationResponseDTO(
                7L, start, end, StatutReservation.CONFIRMEE, 2L, 3L, 8L);
        when(reservationMapper.toResponseDTO(org.mockito.ArgumentMatchers.any(Reservation.class)))
                .thenReturn(response);
        ReservationWithContratRequestDTO request = new ReservationWithContratRequestDTO(
                new ReservationRequest(start, end, StatutReservation.CONFIRMEE, 2L, 3L),
                LocalDate.of(2026, 11, 19));

        ReservationResponseDTO result = reservationService.creerReservationEtContratAssocie(request);

        assertSame(response, result);
        org.mockito.ArgumentCaptor<Contrat> contratCaptor = org.mockito.ArgumentCaptor.forClass(Contrat.class);
        verify(contratRepository).save(contratCaptor.capture());
        assertEquals(new BigDecimal("150.00"), contratCaptor.getValue().getMontantTotal());
        assertEquals(Boolean.TRUE, contratCaptor.getValue().getValide());
        assertEquals(7L, contratCaptor.getValue().getReservation().getIdReservation());
    }
}
