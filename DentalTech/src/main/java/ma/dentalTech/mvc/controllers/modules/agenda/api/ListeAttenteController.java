package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import java.util.List;

public interface ListeAttenteController {
    List<ListeAttenteDto> getAll();
    List<ListeAttenteDto> searchByNom(String nom);
}
