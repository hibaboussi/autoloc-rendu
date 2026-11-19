package tn.esprit.autoloc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Equipement;
import tn.esprit.autoloc.domain.Maintenance;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IEquipementRepository;
import tn.esprit.autoloc.repository.IMaintenanceRepository;
import tn.esprit.autoloc.repository.IVehiculeRepository;
import tn.esprit.autoloc.web.dto.VehiculeDTO;
import tn.esprit.autoloc.web.mapper.VehiculeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehiculeAssignmentServiceTest {

    @Mock
    private IVehiculeRepository vehiculeRepository;
    @Mock
    private IEquipementRepository equipementRepository;
    @Mock
    private IMaintenanceRepository maintenanceRepository;
    @Mock
    private VehiculeMapper vehiculeMapper;

    @InjectMocks
    private VehiculeServiceImpl vehiculeService;

    @Test
    void affecterVehiculeAEquipementAddsAssociationAndReturnsDto() {
        Vehicule vehicule = new Vehicule();
        vehicule.setEquipements(new ArrayList<>());
        Equipement equipement = new Equipement();
        VehiculeDTO response = new VehiculeDTO(4L, null, null, null, null, null, null, null);
        when(vehiculeRepository.findById(4L)).thenReturn(Optional.of(vehicule));
        when(equipementRepository.findById(9L)).thenReturn(Optional.of(equipement));
        when(vehiculeRepository.save(vehicule)).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(response);

        VehiculeDTO result = vehiculeService.affecterVehiculeAEquipement(4L, 9L);

        assertTrue(vehicule.getEquipements().contains(equipement));
        assertSame(response, result);
        verify(vehiculeRepository).save(vehicule);
    }

    @Test
    void affecterMaintenanceAvehiculeLinksMaintenanceAndReturnsDto() {
        Vehicule vehicule = new Vehicule();
        vehicule.setStatut(StatutVehicule.DISPONIBLE);
        Maintenance maintenance = new Maintenance();
        VehiculeDTO response = new VehiculeDTO(4L, "AA-123", null, null, null, null,
                StatutVehicule.MAINTENANCE, null);
        when(vehiculeRepository.findByImmatriculationIgnoreCase("AA-123"))
                .thenReturn(Optional.of(vehicule));
        when(maintenanceRepository.findAllByDescriptionIgnoreCase("vidange"))
                .thenReturn(List.of(maintenance));
        when(vehiculeRepository.save(vehicule)).thenReturn(vehicule);
        when(vehiculeMapper.toDTO(vehicule)).thenReturn(response);

        VehiculeDTO result = vehiculeService.affecterMaintenanceAvehicule("vidange", "AA-123");

        assertSame(vehicule, maintenance.getVehicule());
        assertSame(StatutVehicule.MAINTENANCE, vehicule.getStatut());
        assertSame(response, result);
        verify(maintenanceRepository).save(maintenance);
        verify(vehiculeRepository).save(vehicule);
    }
}
