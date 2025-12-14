package ma.dentalTech.mvc.controllers.modules.patient.batch_implentation;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.controllers.modules.patient.api.PatientController;
import ma.dentalTech.service.modules.patient.api.PatientService;

import java.util.List;

public class PatientControllerImpl implements PatientController {

    private final PatientService service;

    public PatientControllerImpl(PatientService service) {
        this.service = service;
    }

    @Override
    public List<Patient> findAll() {
        return service.getAll();
    }

    @Override
    public Patient findById(Long id) {
        return service.getById(id);
    }

    @Override
    public void create(Patient p) {
        service.create(p);
    }

    @Override
    public void update(Patient p) {
        service.update(p);
    }

    @Override
    public void deleteById(Long id) {
        service.deleteById(id);
    }

    @Override
    public List<Patient> searchByNom(String nomPart) {
        return service.searchByNom(nomPart);
    }

    @Override
    public Patient findByTelephone(String tel) {
        return service.getByTelephone(tel);
    }

    @Override
    public void showRecentPatients() {
        System.out.println("=== Patients récents ===");

        List<Patient> patients = service.getAll();

        if (patients.isEmpty()) {
            System.out.println("Aucun patient trouvé.");
            return;
        }

        patients.stream()
                .limit(5)
                .forEach(p -> System.out.println(
                        p.getId() + " | " +
                                p.getNom() + " " + p.getPrenom() +
                                " | " + p.getTelephone()
                ));
    }

}
