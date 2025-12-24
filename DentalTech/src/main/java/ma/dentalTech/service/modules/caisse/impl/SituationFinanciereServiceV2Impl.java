package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.mvc.dto.caisse.SituationFinanciereDTO;
import ma.dentalTech.repository.modules.caisse.api.SituationFinanciereRepository;
import ma.dentalTech.service.modules.caisse.api.SituationFinanciereServiceV2;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class SituationFinanciereServiceV2Impl implements SituationFinanciereServiceV2 {

    private final SituationFinanciereRepository repository;

    @Override
    public SituationFinanciereDTO getDerniereSituationFinanciere() {

        SituationFinanciere sf = repository.findLast();
        if (sf == null) {
            return SituationFinanciereDTO.builder()
                    .totalDesActes(0.0)
                    .totalPaye(0.0)
                    .credit(0.0)
                    .statut("NORMAL")
                    .build();
        }

        return SituationFinanciereDTO.builder()
                .dossierId(sf.getDossierId())
                .medecinId(sf.getMedecinId())
                .totalDesActes(nz(sf.getTotalDesActes()))
                .totalPaye(nz(sf.getTotalPaye()))
                .credit(nz(sf.getCredit()))
                .statut(sf.getStatut() == null ? null : sf.getStatut().name())
                .build();
    }

    private double nz(BigDecimal v) { return v == null ? 0.0 : v.doubleValue(); }
}
