package ma.dentalTech.mvc.controllers.modules.patient.batch_implementation;

import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentAdminController;
import ma.dentalTech.mvc.dto.patient.AntecedentAdminRowDTO;
import ma.dentalTech.service.modules.patient.api.AntecedentAdminService;

import java.util.List;

public class AntecedentAdminControllerImpl implements AntecedentAdminController {

    private final AntecedentAdminService service;

    public AntecedentAdminControllerImpl(AntecedentAdminService service) {
        this.service = service;
    }

    @Override
    public List<AntecedentAdminRowDTO> getAll() {
        return service.getAll();
    }
}
