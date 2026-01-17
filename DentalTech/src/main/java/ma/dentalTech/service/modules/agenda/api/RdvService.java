package ma.dentalTech.service.modules.agenda.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.agenda.RDV;

import java.time.LocalDate;
import java.util.List;

public interface RdvService {
    List<RDV> getAll();
    RDV getById(Long id);
    void create(RDV r);
    void update(RDV r);
    void delete(RDV r);
    void deleteById(Long id);

    List<RDV> getByPatient(Long patientId);
    List<RDV> getByDetailJournee(Long detailJourneeId);
    List<RDV> getByListeAttente(Long listeAttenteId);
    List<RDV> getByDate(LocalDate date);
    List<RDV> getByStatus(EtatRendezVous status);
    List<RDV> getUpcomingFromToday();

    void confirmer(Long rdvId);
    void annuler(Long rdvId);

    void terminer(Long rdvId);

    void createAndLockPlage(RDV r, Long plageId);

    void deleteAndFreePlage(Long rdvId);
}
