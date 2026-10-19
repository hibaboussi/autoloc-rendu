package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;

import java.util.List;

public interface IContratService {

    Contrat create(Contrat contrat);

    List<Contrat> findAll();

    Contrat findById(Long id);

    Contrat update(Long id, Contrat contrat);

    void deleteById(Long id);
}
