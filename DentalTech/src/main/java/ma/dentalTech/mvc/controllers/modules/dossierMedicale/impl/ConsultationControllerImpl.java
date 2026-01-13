package ma.dentalTech.mvc.controllers.modules.dossierMedicale.impl;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.dossierMedicale.api.ConsultationController;
import ma.dentalTech.mvc.dto.dossierMedicale.common.IdRequestDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListItemDTO;
import ma.dentalTech.mvc.dto.dossierMedicale.consultation.ConsultationListRequestDTO;
import ma.dentalTech.service.modules.dossierMedical.api.ConsultationService;
import ma.dentalTech.service.modules.dossierMedical.exception.ServiceException;

import java.util.List;

public class ConsultationControllerImpl implements ConsultationController {

    private final ConsultationService service;

    public ConsultationControllerImpl(ConsultationService service) {
        this.service = service;
    }

    @Override
    public List<ConsultationListItemDTO> searchForList(ConsultationListRequestDTO in) {
        try {
            return service.searchForList(in).items();
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            // repository peut lever RuntimeException("Erreur SQL...")
            throw new ControllerException("Erreur UI: chargement consultations", e);
        }
    }

    @Override
    public void delete(Long consultationId) {
        try {
            service.delete(new IdRequestDTO(consultationId));
        } catch (ServiceException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (RuntimeException e) {
            throw new ControllerException("Erreur UI: suppression consultation", e);
        }
    }
}
