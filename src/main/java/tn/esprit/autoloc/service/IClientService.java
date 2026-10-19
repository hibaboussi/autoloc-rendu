package tn.esprit.autoloc.service;

import tn.esprit.autoloc.domain.Client;

import java.util.List;

public interface IClientService {

    Client create(Client client);

    List<Client> findAll();

    Client findById(Long id);

    Client update(Long id, Client client);

    void deleteById(Long id);
}
