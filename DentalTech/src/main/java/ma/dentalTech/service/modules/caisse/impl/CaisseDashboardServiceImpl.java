package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.FactureRepository;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseDashboardService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CaisseDashboardServiceImpl implements CaisseDashboardService {

    private final FactureRepository factureRepository;
    private final ChargesRepository chargesRepository;

    @Override
    public CaisseDashboardResponseDTO getDashboard(
            CaisseDashboardRequestDTO request,
            Role role,
            Long currentUserId
    ) {

        // =========================
        // 1️⃣ Sécurité & rôle
        // =========================
        Long medecinId = request.getMedecinId();

        if (role == Role.MEDECIN) {
            // un médecin ne voit QUE ses données
            medecinId = currentUserId;
        }

        // =========================
        // 2️⃣ Récupération données
        // =========================
        List<Facture> factures = factureRepository.search(
                request.getDateDebut(),
                request.getDateFin(),
                medecinId,
                request.getStatut(),
                request.getSearch()
        );

        Double totalRevenus = factureRepository.sumMontantPaye(
                request.getDateDebut(), request.getDateFin(), medecinId
        );

        Double totalCharges = chargesRepository.sumCharges(
                request.getDateDebut(), request.getDateFin()
        );

        Double benefice = totalRevenus - totalCharges;

        // =========================
        // 3️⃣ Mapping Factures → DTO (table maquette)
        // =========================
        List<CaisseFactureDTO> factureDTOs = factures.stream()
                .map(f -> CaisseFactureDTO.builder()
                        .factureId(f.getId())
                        .nom(f.getPatient().getNom())
                        .prenom(f.getPatient().getPrenom())
                        .montant(f.getTotalTtc())
                        .dateEmission(f.getDateEmission())
                        .statut(f.getStatut().name())
                        .reste(f.getResteAPayer())

                        // actions selon rôle + statut
                        .canView(true)
                        .canPrint(true)
                        .canPay(role != Role.MEDECIN && f.isImpayee())
                        .canCancel(role == Role.ADMIN && !f.isPayee())
                        .build()
                ).collect(Collectors.toList());

        // =========================
        // 4️⃣ Graphe (préparé pour JFreeChart)
        // =========================
        CaisseChartDTO chart = CaisseChartDTO.builder()
                .title("Revenus vs Charges")
                .labels(factureRepository.getChartLabels(
                        request.getDateDebut(), request.getDateFin()))
                .revenus(factureRepository.getChartRevenus(
                        request.getDateDebut(), request.getDateFin(), medecinId))
                .charges(chargesRepository.getChartCharges(
                        request.getDateDebut(), request.getDateFin()))
                .build();

        // =========================
        // 5️⃣ Construction réponse finale (MAQUETTE)
        // =========================
        return CaisseDashboardResponseDTO.builder()
                .appliedFilters(request)

                // cards
                .ca(totalRevenus)
                .charges(totalCharges)
                .benefice(benefice)
                .nbImpayes(factureRepository.countImpayees(
                        request.getDateDebut(), request.getDateFin(), medecinId))

                // graph
                .chart(chart)

                // table
                .factures(factureDTOs)

                // totaux bas de page
                .totalFactures(factures.size())
                .totalPaye(totalRevenus)
                .totalImpaye(factureRepository.sumImpayees(
                        request.getDateDebut(), request.getDateFin(), medecinId))
                .totalMontant(factureRepository.sumTotal(
                        request.getDateDebut(), request.getDateFin(), medecinId))

                .build();
    }
}
