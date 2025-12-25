package ma.dentalTech.service.modules.dossierMedical.dto.prescription;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SavePrescriptionRequestDTO(PrescriptionDTO prescription, ActorDTO actor) {}
