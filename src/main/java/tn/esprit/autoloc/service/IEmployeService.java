package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Employe;

import java.util.List;

public interface IEmployeService {

    Employe create(Employe employe);

    List<Employe> findAll();

    Employe findById(Long id);

    Employe update(Long id, Employe employe);

    void deleteById(Long id);
}
