package ma.dentalTech.mvc.dto.dossierMedicale.ordonnance;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveOrdonnanceRequestDTO(OrdonnanceDTO ordonnance, ActorDTO actor) {}
