package tn.esprit.autoloc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.domain.Reservation;
import tn.esprit.autoloc.repository.IContratRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContratServiceImplTest {

    @Mock
    private IContratRepository contratRepository;

    @InjectMocks
    private ContratServiceImpl contratService;

    @Test
    void updateCopiesEditableFieldsAndPreservesExistingPayment() {
        Paiement paiement = new Paiement();
        Contrat existingContrat = new Contrat();
        existingContrat.setIdContrat(7L);
        existingContrat.setPaiement(paiement);
        when(contratRepository.findById(7L)).thenReturn(Optional.of(existingContrat));
        when(contratRepository.save(existingContrat)).thenReturn(existingContrat);

        Reservation reservation = new Reservation();
        Contrat changes = new Contrat();
        changes.setDateSignature(LocalDate.of(2026, 11, 11));
        changes.setMontantTotal(new BigDecimal("125.00"));
        changes.setValide(true);
        changes.setReservation(reservation);

        Contrat updated = contratService.update(7L, changes);

        assertSame(existingContrat, updated);
        assertSame(paiement, updated.getPaiement());
        assertSame(reservation, updated.getReservation());
        assertEquals(LocalDate.of(2026, 11, 11), updated.getDateSignature());
        assertEquals(new BigDecimal("125.00"), updated.getMontantTotal());
        assertTrue(updated.getValide());
        verify(contratRepository).save(existingContrat);
    }
}
