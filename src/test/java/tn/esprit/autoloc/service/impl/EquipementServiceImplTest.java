package tn.esprit.autoloc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipementServiceImplTest {

    @Mock
    private IEquipementRepository equipementRepository;

    @InjectMocks
    private EquipementServiceImpl equipementService;

    @Test
    void createClearsEquipementIdBeforeSaving() {
        Equipement equipement = new Equipement();
        equipement.setIdEquipement(42L);
        when(equipementRepository.save(equipement)).thenAnswer(invocation -> invocation.getArgument(0));

        Equipement created = equipementService.create(equipement);

        assertNull(created.getIdEquipement());
        verify(equipementRepository).save(equipement);
    }

    @Test
    void updateUsesPathIdAndFailsIfEquipementDoesNotExist() {
        Equipement equipement = new Equipement();
        when(equipementRepository.findById(7L)).thenReturn(Optional.of(new Equipement()));
        when(equipementRepository.save(equipement)).thenAnswer(invocation -> invocation.getArgument(0));

        Equipement updated = equipementService.update(7L, equipement);

        assertEquals(7L, updated.getIdEquipement());
        verify(equipementRepository).save(equipement);
    }

    @Test
    void findByIdThrowsWhenEquipementDoesNotExist() {
        when(equipementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> equipementService.findById(99L));
    }
}
