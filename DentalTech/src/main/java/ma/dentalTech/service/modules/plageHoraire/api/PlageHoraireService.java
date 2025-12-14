package ma.dentalTech.service.modules.plageHoraire.api;

import ma.dentalTech.entities.plageHoraire.PlageHoraire;

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
