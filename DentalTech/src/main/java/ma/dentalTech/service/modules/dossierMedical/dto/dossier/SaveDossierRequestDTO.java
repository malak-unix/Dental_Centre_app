package ma.dentalTech.service.modules.dossierMedical.dto.dossier;

import ma.dentalTech.service.modules.dossierMedical.dto.common.ActorDTO;

public record SaveDossierRequestDTO(
        DossierDTO dossier,
        ActorDTO actor) {}
