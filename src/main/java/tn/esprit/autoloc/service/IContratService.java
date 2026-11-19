package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Contrat;
import tn.esprit.autoloc.web.dto.ContratDTO;

import java.time.LocalDate;

import java.util.List;

public interface IContratService {

    Contrat create(Contrat contrat);

    List<Contrat> findAll();

    Contrat findById(Long id);

    Contrat update(Long id, Contrat contrat);

    void deleteById(Long id);

    ContratDTO affecterContratAReservation(LocalDate dateSignature, LocalDate dateDebut, LocalDate dateFin);
}
