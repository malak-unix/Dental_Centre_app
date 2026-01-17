package ma.dentalTech.mvc.controllers.modules.agenda.api;

import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ListeAttenteController {
    List<ListeAttenteDto> getAll();
    List<ListeAttenteDto> searchByNom(String nom);

    ListeAttenteDto create(ListeAttenteDto dto);
    ListeAttenteDto update(ListeAttenteDto dto);
    void deleteById(Long id);

    void programmer(Long listeAttenteId, Long patientId, Long medecinId,
                    LocalDate date, LocalTime heure, String motif);
}
