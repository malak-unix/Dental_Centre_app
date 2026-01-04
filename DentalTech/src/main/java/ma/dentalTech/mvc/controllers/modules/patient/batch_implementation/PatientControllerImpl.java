package ma.dentalTech.mvc.controllers.modules.patient.batch_implementation;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;
import ma.dentalTech.service.modules.patient.api.PatientAppService;

import java.util.List;

public class PatientControllerImpl implements PatientController {

    private final PatientAppService app;

    public PatientControllerImpl(PatientAppService app) {
        this.app = app;
    }

    @Override
    public List<PatientListDto> lister() {
        try {
            return app.listerPatients();
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: listing patients", e);
        }
    }

    @Override
    public PatientFormDto consulter(Long id) {
        try {
            return app.consulterPatient(id);
        } catch (ValidationException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: consulter patient", e);
        }
    }

    @Override
    public PatientFormDto creer(PatientFormDto dto) {
        try {
            return app.creerPatient(dto);
        } catch (ValidationException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: création patient", e);
        }
    }

    @Override
    public PatientFormDto modifier(Long id, PatientFormDto dto) {
        try {
            return app.modifierPatient(id, dto);
        } catch (ValidationException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: modification patient", e);
        }
    }

    @Override
    public void supprimer(Long id) {
        try {
            app.supprimerPatient(id);
        } catch (ValidationException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: suppression patient", e);
        }
    }

    @Override
    public List<PatientListDto> rechercherParNom(String nom) {
        try {
            return app.rechercherParNom(nom);
        } catch (ValidationException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: recherche nom", e);
        }
    }

    @Override
    public PatientFormDto rechercherParTelephone(String tel) {
        try {
            return app.rechercherParTelephone(tel);
        } catch (ValidationException e) {
            throw new ControllerException(e.getMessage(), e);
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: recherche téléphone", e);
        }
    }

    @Override
    public List<PatientListDto> showRecentPatients() {
        try {
            // simple : derniers patients créés
            return app.listerPatients()
                    .stream()
                    .limit(5)
                    .toList();
        } catch (ServiceException e) {
            throw new ControllerException("Erreur UI: patients récents", e);
        }
    }

}
