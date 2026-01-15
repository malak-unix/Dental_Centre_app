package ma.dentalTech.service.modules.patient.impl;

import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.patient.AntecedentAdminRowDTO;
import ma.dentalTech.repository.modules.patient.api.AntecedentRepository;
import ma.dentalTech.repository.modules.patient.api.PatientRepository;
import ma.dentalTech.service.modules.patient.api.AntecedentAdminService;

import java.util.ArrayList;
import java.util.List;

public class AntecedentAdminServiceImpl implements AntecedentAdminService {

    private final AntecedentRepository antecedentRepo;
    private final PatientRepository patientRepo;

    public AntecedentAdminServiceImpl(AntecedentRepository antecedentRepo, PatientRepository patientRepo) {
        this.antecedentRepo = antecedentRepo;
        this.patientRepo = patientRepo;
    }

    @Override
    public List<AntecedentAdminRowDTO> getAll() {
        List<Antecedents> all = antecedentRepo.findAll();
        List<AntecedentAdminRowDTO> out = new ArrayList<>();

        for (Antecedents a : all) {
            Patient p = patientRepo.findById(a.getPatientId());

            String patientNom = (p == null)
                    ? ("Patient #" + a.getPatientId())
                    : (safe(p.getNom()) + " " + safe(p.getPrenom())).trim();

            out.add(new AntecedentAdminRowDTO(
                    a.getId(),
                    a.getPatientId(),
                    patientNom,
                    a.getNom(),
                    a.getCategorie(),
                    a.getNiveauDeRisque() != null ? a.getNiveauDeRisque().name() : "",
                    a.getDescription()
            ));
        }
        return out;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
