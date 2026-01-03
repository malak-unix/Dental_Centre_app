package ma.dentalTech.service.modules.dossierMedical.dto.acte;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveActeRequestDTO(ActeDTO acte, ActorDTO actor) {}
