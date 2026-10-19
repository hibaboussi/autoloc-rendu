package tn.esprit.autoloc.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.autoloc.domain.Client;
import tn.esprit.autoloc.repository.IClientRepository;
import tn.esprit.autoloc.service.exception.ResourceNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private IClientRepository clientRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    @Test
    void createClearsClientIdBeforeSaving() {
        Client client = new Client();
        client.setIdClient(42L);
        when(clientRepository.save(client)).thenAnswer(invocation -> invocation.getArgument(0));

        Client created = clientService.create(client);

        assertNull(created.getIdClient());
        verify(clientRepository).save(client);
    }

    @Test
    void updateUsesPathIdAndFailsIfClientDoesNotExist() {
        Client client = new Client();
        when(clientRepository.findById(7L)).thenReturn(Optional.of(new Client()));
        when(clientRepository.save(client)).thenAnswer(invocation -> invocation.getArgument(0));

        Client updated = clientService.update(7L, client);

        assertEquals(7L, updated.getIdClient());
        verify(clientRepository).save(client);
    }

    @Test
    void findByIdThrowsWhenClientDoesNotExist() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> clientService.findById(99L));
    }
}
