package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Equipement;

import java.util.List;

public interface IEquipementService {

    Equipement create(Equipement equipement);

    List<Equipement> findAll();

    Equipement findById(Long id);

    Equipement update(Long id, Equipement equipement);

    void deleteById(Long id);
}
