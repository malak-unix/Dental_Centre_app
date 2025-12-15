package ma.dentalTech.mvc.controllers.modules.rdv.api;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;

import java.time.LocalDate;
import java.util.List;

public interface RdvController {
    List<RDV> findAll();
    RDV findById(Long id);
    void create(RDV r);
    void update(RDV r);
    void deleteById(Long id);

    List<RDV> findByDate(LocalDate date);
    List<RDV> findByStatus(EtatRendezVous status);
    List<RDV> upcomingFromToday();
    void confirm(Long id);
    void cancel(Long id);
    void runDemo();

}
