package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ActeController;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.FindActesRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.SaveActeRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;
import ma.dentalTech.service.modules.dossierMedical.api.ActeService;
import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

import java.util.List;
import java.util.stream.Collectors;

public class ActeControllerImpl implements ActeController {

    private final ActeService service;

    public ActeControllerImpl(ActeService service) {
        this.service = service;
    }

    @Override
    public List<ActeListItemDTO> findAll() {
        try {
            return service.findAll(new IdRequestDTO(null)).items().stream()
                    .map(this::toListItemDTO)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la récupération des actes", e);
        }
    }

    @Override
    public List<ActeListItemDTO> search(String categorie, String keyword) {
        try {
            FindActesRequestDTO request = new FindActesRequestDTO(
                    keyword != null && !keyword.isBlank() ? keyword : null,
                    categorie != null && !categorie.isBlank() ? categorie : null,
                    new PageRequestDTO(100, 0)
            );
            return service.find(request).items().stream()
                    .map(this::toListItemDTO)
                    .collect(Collectors.toList());
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la recherche d'actes", e);
        }
    }

    @Override
    public Long create(ActeDTO acte, String username) {
        try {
            if (acte == null) throw new IllegalArgumentException("ActeDTO null");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("username obligatoire");

            SaveActeRequestDTO request = new SaveActeRequestDTO(
                    acte,
                    new ActorDTO(username)
            );
            return service.create(request).id();
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la création de l'acte", e);
        }
    }

    @Override
    public void update(ActeDTO acte, String username) {
        try {
            if (acte == null) throw new IllegalArgumentException("ActeDTO null");
            if (acte.id() == null) throw new IllegalArgumentException("id obligatoire pour update");
            if (username == null || username.isBlank()) throw new IllegalArgumentException("username obligatoire");

            SaveActeRequestDTO request = new SaveActeRequestDTO(
                    acte,
                    new ActorDTO(username)
            );
            service.update(request);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la mise à jour de l'acte", e);
        }
    }

    @Override
    public void delete(Long acteId) {
        try {
            if (acteId == null) throw new IllegalArgumentException("acteId null");
            service.delete(new IdRequestDTO(acteId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la suppression de l'acte", e);
        }
    }

    @Override
    public ActeDTO getById(Long acteId) {
        try {
            if (acteId == null) throw new IllegalArgumentException("acteId null");
            return service.getById(new IdRequestDTO(acteId));
        } catch (ServiceException e) {
            throw new ControllerException("Erreur lors de la récupération de l'acte", e);
        }
    }

    private ActeListItemDTO toListItemDTO(ActeDTO dto) {
        ActeListItemDTO item = new ActeListItemDTO();
        item.setActeId(dto.id());
        item.setLibelle(dto.libelle());
        item.setCategorie(dto.categorie());
        item.setPrixBase(dto.prixBase());
        item.setDescription(dto.description());
        return item;
    }
}
