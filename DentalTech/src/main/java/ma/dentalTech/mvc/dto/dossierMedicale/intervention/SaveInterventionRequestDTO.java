package ma.dentalTech.mvc.dto.dossierMedicale.intervention;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveInterventionRequestDTO(InterventionMedecinDTO intervention, ActorDTO actor) {}
