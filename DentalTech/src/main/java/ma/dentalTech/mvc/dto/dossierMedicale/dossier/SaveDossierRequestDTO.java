package ma.dentalTech.mvc.dto.dossierMedicale.dossier;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveDossierRequestDTO(
        DossierDTO dossier,
        ActorDTO actor) {}
