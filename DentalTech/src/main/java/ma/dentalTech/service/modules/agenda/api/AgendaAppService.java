package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.common.exceptions.ValidationException;
import ma.dentalTech.mvc.dto.agenda.AgendaMensuelDto;
import ma.dentalTech.mvc.dto.agenda.RdvDto;

import java.time.LocalDate;

public interface AgendaAppService {

    // Maquette : agenda semaine
    AgendaMensuelDto consulterAgendaSemaine(Long medecinId, LocalDate dateDansSemaine)
            throws ValidationException, ServiceException;

    // Maquette : créer / modifier RDV
    RdvDto creerRdv(RdvDto dto) throws ValidationException, ServiceException;
    RdvDto modifierRdv(Long rdvId, RdvDto dto) throws ValidationException, ServiceException;

    // Use cases
    void annulerRdv(Long rdvId) throws ValidationException, ServiceException;
    void confirmerRdv(Long rdvId) throws ValidationException, ServiceException;
    RdvDto consulterRdv(Long rdvId) throws ValidationException, ServiceException;
}
