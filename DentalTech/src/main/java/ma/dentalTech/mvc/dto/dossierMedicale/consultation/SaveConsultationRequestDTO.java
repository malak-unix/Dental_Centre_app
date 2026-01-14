package ma.dentalTech.mvc.dto.dossierMedicale.consultation;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveConsultationRequestDTO(ConsultationDTO consultation, ActorDTO actor) {}
