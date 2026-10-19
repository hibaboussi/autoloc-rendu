package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Agence;

import java.util.List;

public interface IAgenceService {

    Agence create(Agence agence);

    List<Agence> findAll();

    Agence findById(Long id);

    Agence update(Long id, Agence agence);

    void deleteById(Long id);
}
