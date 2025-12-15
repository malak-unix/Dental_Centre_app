package ma.dentalTech.mvc.controllers.modules.rdv.batch_implementation;

import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.mvc.controllers.modules.rdv.api.RdvController;
import ma.dentalTech.service.modules.rdv.api.RdvService;

import java.time.LocalDate;
import java.util.List;

public class RdvControllerImpl implements RdvController {

    private final RdvService service;

    public RdvControllerImpl(RdvService service) {
        this.service = service;
    }

    @Override
    public List<RDV> findAll() {
        return service.getAll();
    }

    @Override
    public RDV findById(Long id) {
        return service.getById(id);
    }

    @Override
    public void create(RDV r) {
        service.create(r);
    }

    @Override
    public void update(RDV r) {
        service.update(r);
    }

    @Override
    public void deleteById(Long id) {
        service.deleteById(id);
    }

    @Override
    public List<RDV> findByDate(LocalDate date) {
        return service.getByDate(date);
    }

    @Override
    public List<RDV> findByStatus(EtatRendezVous status) {
        return service.getByStatus(status);
    }

    @Override
    public List<RDV> upcomingFromToday() {
        return service.getUpcomingFromToday();
    }

    @Override
    public void confirm(Long id) {
        RDV r = service.getById(id);
        if (r == null) return;
        r.setStatus(EtatRendezVous.CONFIRME);
        service.update(r);
    }

    @Override
    public void cancel(Long id) {
        RDV r = service.getById(id);
        if (r == null) return;
        r.setStatus(EtatRendezVous.ANNULE);
        service.update(r);
    }

    @Override
    public void runDemo() {
        System.out.println("RDV total=" + service.getAll().size());
        System.out.println("RDV today=" + service.getByDate(LocalDate.now()).size());
        System.out.println("RDV upcoming=" + service.getUpcomingFromToday().size());
    }

}
