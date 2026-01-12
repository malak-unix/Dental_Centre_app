package ma.dentalTech.mvc.dto.dossierMedicale.prescription;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SavePrescriptionRequestDTO(PrescriptionDTO prescription, ActorDTO actor) {}
