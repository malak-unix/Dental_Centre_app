package ma.dentalTech.service.modules.patient.mappers;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.mvc.dto.patient.PatientFormDto;
import ma.dentalTech.mvc.dto.patient.PatientListDto;

public final class PatientMapper {
    private PatientMapper(){}

    public static PatientFormDto toFormDto(Patient p) {
        if (p == null) return null;
        return PatientFormDto.builder()
                .id(p.getId())
                .nom(p.getNom())
                .prenom(p.getPrenom())
                .dateNaissance(p.getDateNaissance())
                .sexe(p.getSexe())
                .telephone(p.getTelephone())
                .adresse(p.getAdresse())
                .assurance(p.getAssurance())
                .build();
    }

    public static PatientListDto toListDto(Patient p) {
        if (p == null) return null;
        String nomComplet = ((p.getNom() == null) ? "" : p.getNom()) + " " + ((p.getPrenom() == null) ? "" : p.getPrenom());
        return PatientListDto.builder()
                .id(p.getId())
                .nomComplet(nomComplet.trim())
                .telephone(p.getTelephone())
                .build();
    }

    public static Patient toEntity(PatientFormDto d) {
        if (d == null) return null;
        return Patient.builder()
                .id(d.getId())
                .nom(d.getNom())
                .prenom(d.getPrenom())
                .dateNaissance(d.getDateNaissance())
                .sexe(d.getSexe())
                .telephone(d.getTelephone())
                .adresse(d.getAdresse())
                .assurance(d.getAssurance())
                .build();
    }
}
