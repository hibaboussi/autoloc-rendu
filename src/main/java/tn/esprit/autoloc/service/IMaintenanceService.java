package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Maintenance;

import java.util.List;

public interface IMaintenanceService {

    Maintenance create(Maintenance maintenance);

    List<Maintenance> findAll();

    Maintenance findById(Long id);

    Maintenance update(Long id, Maintenance maintenance);

    void deleteById(Long id);
}
