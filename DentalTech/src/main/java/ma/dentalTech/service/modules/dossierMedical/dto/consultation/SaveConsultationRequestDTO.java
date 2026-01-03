package ma.dentalTech.service.modules.dossierMedical.dto.consultation;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveConsultationRequestDTO(ConsultationDTO consultation, ActorDTO actor) {}
