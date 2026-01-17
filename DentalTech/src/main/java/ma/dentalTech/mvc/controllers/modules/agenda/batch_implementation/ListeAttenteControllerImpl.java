package ma.dentalTech.mvc.controllers.modules.agenda.batch_implementation;

import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.mvc.controllers.modules.agenda.api.ListeAttenteController;
import ma.dentalTech.mvc.dto.agenda.ListeAttenteDto;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.agenda.api.AgendaService;
import ma.dentalTech.service.modules.agenda.api.ListeAttenteService;
import ma.dentalTech.service.modules.agenda.api.RdvService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ListeAttenteControllerImpl implements ListeAttenteController {

    private final ListeAttenteService service;
    private final RdvService rdvService;
    private final AgendaService agendaService;
    private final PatientRepository patientRepo;

    public ListeAttenteControllerImpl(ListeAttenteService service,
                                      RdvService rdvService,
                                      AgendaService agendaService,
                                      PatientRepository patientRepo) {
        this.service = service;
        this.rdvService = rdvService;
        this.agendaService = agendaService;
        this.patientRepo = patientRepo;
    }

    @Override
    public List<ListeAttenteDto> getAll() {
        return service.getAll().stream().map(this::toDto).toList();
    }

    @Override
    public List<ListeAttenteDto> searchByNom(String nom) {
        if (nom == null || nom.isBlank()) return getAll();
        return service.searchByNomListe(nom).stream().map(this::toDto).toList();
    }

    @Override
    public ListeAttenteDto create(ListeAttenteDto dto) {
        if (dto == null) throw new IllegalArgumentException("dto null");
        ListeAttente l = new ListeAttente();
        l.setPatientId(dto.getPatientId());
        l.setNom(dto.getNom());
        l.setMotif(dto.getMotif());
        l.setDateAjout(dto.getDateAjout());
        l.setPriorite(dto.getPriorite());
        service.create(l);
        return toDto(l);
    }

    @Override
    public ListeAttenteDto update(ListeAttenteDto dto) {
        if (dto == null || dto.getId() == null)
            throw new IllegalArgumentException("id obligatoire");

        ListeAttente existing = service.getById(dto.getId());
        if (existing == null)
            throw new IllegalArgumentException("ListeAttente introuvable");

        if (dto.getPatientId() != null) existing.setPatientId(dto.getPatientId());
        if (dto.getNom() != null) existing.setNom(dto.getNom());
        if (dto.getMotif() != null) existing.setMotif(dto.getMotif());
        if (dto.getDateAjout() != null) existing.setDateAjout(dto.getDateAjout());
        if (dto.getPriorite() != null) existing.setPriorite(dto.getPriorite());

        service.update(existing);
        return toDto(existing);
    }

    @Override
    public void deleteById(Long id) {
        service.deleteById(id);
    }

    @Override
    public void programmer(Long listeAttenteId, Long patientId, Long medecinId,
                           LocalDate date, LocalTime heure, String motif) {
        if (patientId == null || medecinId == null || date == null || heure == null)
            throw new IllegalArgumentException("patientId/medecinId/date/heure obligatoires");

        String mois = String.valueOf(date.getMonth()).toUpperCase();
        int annee = date.getYear();

        var agenda = agendaService.getAgendaByMedecinMonth(medecinId, mois, annee);
        if (agenda == null) throw new IllegalArgumentException("Agenda mensuel introuvable");

        var detail = agendaService.getDetailByAgendaAndDate(agenda.getId(), date);
        if (detail == null) throw new IllegalArgumentException("Detail journee introuvable pour cette date");

        RDV r = new RDV();
        r.setPatientId(patientId);
        r.setDetailJourneeId(detail.getId());
        r.setListeAttenteId(listeAttenteId);
        r.setDateRdv(date);
        r.setHeure(heure);
        r.setMotif(motif == null ? "RDV" : motif);
        r.setStatut(EtatRendezVous.PLANIFIE);

        rdvService.create(r);

        if (listeAttenteId != null) {
            service.deleteById(listeAttenteId);
        }
    }

    private ListeAttenteDto toDto(ListeAttente l) {
        if (l == null) return null;

        String patientNom = null;
        if (patientRepo != null && l.getPatientId() != null) {
            var p = patientRepo.findById(l.getPatientId());
            if (p != null) {
                patientNom = (p.getNom() + " " + p.getPrenom()).trim();
            }
        }

        return ListeAttenteDto.builder()
                .id(l.getId())
                .patientId(l.getPatientId())
                .patientNom(patientNom)
                .nom(l.getNom())
                .motif(l.getMotif())
                .dateAjout(l.getDateAjout())
                .priorite(l.getPriorite())
                .build();
    }
}
