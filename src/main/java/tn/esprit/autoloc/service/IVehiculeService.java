package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Vehicule;

import java.util.List;

public interface IVehiculeService {

    Vehicule create(Vehicule vehicule);

    List<Vehicule> findAll();

    Vehicule findById(Long id);

    Vehicule update(Long id, Vehicule vehicule);

    void deleteById(Long id);
}
