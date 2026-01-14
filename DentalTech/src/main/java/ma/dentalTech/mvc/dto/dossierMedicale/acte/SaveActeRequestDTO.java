package ma.dentalTech.mvc.dto.dossierMedicale.acte;

import ma.dentalTech.mvc.dto.dossierMedicale.common.ActorDTO;

public record SaveActeRequestDTO(ActeDTO acte, ActorDTO actor) {}
