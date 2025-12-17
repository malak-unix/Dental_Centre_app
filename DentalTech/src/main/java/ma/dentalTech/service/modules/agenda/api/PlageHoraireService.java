package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.entities.agenda.PlageHoraire;

import java.util.List;

public interface PlageHoraireService {
    List<PlageHoraire> getAll();
    PlageHoraire getById(Long id);
    void create(PlageHoraire p);
    void update(PlageHoraire p);
    void delete(PlageHoraire p);
    void deleteById(Long id);

    List<PlageHoraire> getByDetailJournee(Long detailJourneeId);
    List<PlageHoraire> getDisponiblesByDetailJournee(Long detailJourneeId);
}
