package ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere;

import ma.dentalTech.mvc.dto.dossierMedicale.common.PageRequestDTO;

/**
 * DTO pour filtrer la liste des situations financières.
 */
public record SituationFinanciereListRequestDTO(
        Long medecinId,
        String patientKeyword,
        PageRequestDTO page
) {}
