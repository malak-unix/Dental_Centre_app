package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.controllers.modules.agenda.api.RdvController;
import ma.dentalTech.mvc.dto.agenda.RdvDto;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.agenda.api.RdvService;

import java.time.LocalDate;
import java.util.List;

public class RdvControllerImpl implements RdvController {

    private final RdvService service;
    private final PatientRepository patientRepo;

    // ✅ Mets ce constructeur et adapte ApplicationContext (ou beans wiring)
    public RdvControllerImpl(RdvService service, PatientRepository patientRepo) {
        this.service = service;
        this.patientRepo = patientRepo;
    }

    @Override
    public List<RdvDto> getAll() {
        return service.getAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> getByDate(LocalDate date) {
        return service.getByDate(date).stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> getUpcomingFromToday() {
        return service.getUpcomingFromToday().stream().map(this::toDto).toList();
    }

    @Override
    public List<RdvDto> getByStatus(EtatRendezVous statut) {
        return service.getByStatus(statut).stream().map(this::toDto).toList();
    }

    private RdvDto toDto(RDV r) {
        if (r == null) return null;

        String patientNom = null;
        if (r.getPatientId() != null && patientRepo != null) {
            var p = patientRepo.findById(r.getPatientId());
            if (p != null) patientNom = p.getNom() + " " + p.getPrenom();
        }

        return RdvDto.builder()
                .id(r.getId())
                .patientId(r.getPatientId())
                .detailJourneeId(r.getDetailJourneeId())
                .listeAttenteId(r.getListeAttenteId())
                // typeRdv: si ton entity RDV ne l’a pas -> null
                .typeRdv(null)
                .dateRdv(r.getDateRdv())
                .heure(r.getHeure())
                .motif(r.getMotif())
                .statut(r.getStatut())
                .noteMedecin(r.getNoteMedecin())
                .patientNom(patientNom)
                .build();
    }
}
