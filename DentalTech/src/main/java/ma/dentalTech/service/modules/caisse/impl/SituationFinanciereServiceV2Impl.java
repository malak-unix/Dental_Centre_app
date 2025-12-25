package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.mvc.dto.caisse.SituationFinanciereDTO;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.service.modules.caisse.api.SituationFinanciereServiceV2;

@RequiredArgsConstructor
public class SituationFinanciereServiceV2Impl implements SituationFinanciereServiceV2 {

    private final SituationFinanciereRepository repository;

    @Override
    public SituationFinanciereDTO getDerniereSituationFinanciere() {

        // ✅ méthode correcte selon TON repository
        SituationFinanciere sf = repository.findLast();

        // Si aucune SF en base → DTO cohérent (zéro)
        if (sf == null) {
            return SituationFinanciereDTO.builder()
                    .dossierId(null)
                    .medecinId(null)
                    .totalDesActes(0.0)
                    .totalPaye(0.0)
                    .credit(0.0)
                    .statut(null)
                    .build();
        }

        return SituationFinanciereDTO.builder()
                .dossierId(sf.getDossierId())
                .medecinId(sf.getMedecinId())
                .totalDesActes(nvl(sf.getTotalDesActes()))
                .totalPaye(nvl(sf.getTotalPaye()))
                .credit(nvl(sf.getCredit()))
                .statut(sf.getStatut() == null ? null : sf.getStatut().name())
                .build();
    }

    // ========================= utils =========================
    private Double nvl(Double v) {
        return v == null ? 0.0 : v;
    }
}
