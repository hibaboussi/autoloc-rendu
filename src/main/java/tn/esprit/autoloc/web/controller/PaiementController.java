package tn.esprit.autoloc.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.autoloc.domain.Paiement;
import tn.esprit.autoloc.service.IContratService;
import tn.esprit.autoloc.service.IPaiementService;
import tn.esprit.autoloc.web.dto.PaiementRequest;
import tn.esprit.autoloc.web.dto.PaiementResponse;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
@RequiredArgsConstructor
public class PaiementController {

    private final IPaiementService paiementService;
    private final IContratService contratService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaiementResponse create(@Valid @RequestBody PaiementRequest request) {
        return toResponse(paiementService.create(toEntity(request)));
    }

    @GetMapping
    public List<PaiementResponse> findAll() {
        return paiementService.findAll().stream().map(PaiementController::toResponse).toList();
    }

    @GetMapping("/{id}")
    public PaiementResponse findById(@PathVariable Long id) {
        return toResponse(paiementService.findById(id));
    }

    @PutMapping("/{id}")
    public PaiementResponse update(@PathVariable Long id, @Valid @RequestBody PaiementRequest request) {
        return toResponse(paiementService.update(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        paiementService.deleteById(id);
    }

    private Paiement toEntity(PaiementRequest request) {
        Paiement paiement = new Paiement();
        paiement.setMontant(request.montant());
        paiement.setDatePaiement(request.datePaiement());
        paiement.setModePaiement(request.modePaiement());
        paiement.setContrat(contratService.findById(request.contratId()));
        return paiement;
    }

    private static PaiementResponse toResponse(Paiement paiement) {
        return new PaiementResponse(
                paiement.getIdPaiement(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getModePaiement(),
                paiement.getContrat().getIdContrat()
        );
    }
}
