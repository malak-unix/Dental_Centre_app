package ma.dentalTech.service.modules.dossierMedical.dto.ordonnance;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveOrdonnanceRequestDTO(OrdonnanceDTO ordonnance, ActorDTO actor) {}
