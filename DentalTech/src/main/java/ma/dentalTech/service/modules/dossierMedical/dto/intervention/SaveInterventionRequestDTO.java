package ma.dentalTech.service.modules.dossierMedical.dto.intervention;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveInterventionRequestDTO(InterventionMedecinDTO intervention, ActorDTO actor) {}
