package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.service.modules.caisse.api.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TestModuleCaisseServices {

    private final ChargesServiceV2 chargesService =
            ApplicationContext.getBean(ChargesServiceV2.class);

    private final RevenusServiceV2 revenusService =
            ApplicationContext.getBean(RevenusServiceV2.class);

    private final FactureServiceV2 factureService =
            ApplicationContext.getBean(FactureServiceV2.class);

    private final SituationFinanciereServiceV2 sfService =
            ApplicationContext.getBean(SituationFinanciereServiceV2.class);

    private final CaisseDashboardServiceV2 caisseDashboardService =
            ApplicationContext.getBean(CaisseDashboardServiceV2.class);

    private final ChartService chartService =
            ApplicationContext.getBean(ChartService.class);


    // ⚠️ adapte selon ta BD
    private final Long cabinetId = 1L;
    private final Long consultationId = 1L;

    public void process() {
        System.out.println("\n==================================================");
        System.out.println("            TEST MODULE CAISSE (V2)");
        System.out.println("==================================================");

        testCharges();
        testRevenus();
        testFactures();
        testSituationFinanciere();
        testCaisseDashboard();
        testChart();

        System.out.println("\n✅ FIN TEST MODULE CAISSE");
    }

    private void testCharges() {
        System.out.println("\n--- [CHARGES] ---");

        ChargeItemDTO created = null;
        try {
            created = chargesService.create(ChargeCreateDTO.builder()
                    .cabinetId(cabinetId)
                    .titre("Matériel dentaire")
                    .description("Achats consommables")
                    .montant(new BigDecimal("250"))
                    .dateCharge(LocalDateTime.now())
                    .build());
            System.out.println("✅ create = " + created);
        } catch (Exception ex) {
            System.err.println("⚠️ create charges échoué (cabinetId ?) : " + ex.getMessage());
        }

        ChargeFilterDTO filter = ChargeFilterDTO.builder()
                .dateDebut(LocalDate.now().minusMonths(1))
                .dateFin(LocalDate.now())
                .build();

        List<ChargeItemDTO> list = chargesService.list(filter);
        System.out.println("list size = " + (list == null ? 0 : list.size()));
        System.out.println("total     = " + chargesService.total(filter));

        if (created != null) {
            try {
                ChargeItemDTO updated = chargesService.update(created.getId(), ChargeUpdateDTO.builder()
                        .titre(created.getTitre() + " (Maj)")
                        .description(created.getDescription())
                        .montant(new BigDecimal("300"))
                        .dateCharge(created.getDateCharge())
                        .build());
                System.out.println("✅ update = " + updated);

                ChargeItemDTO found = chargesService.findById(created.getId());
                System.out.println("✅ findById = " + found);

                chargesService.delete(created.getId());
                System.out.println("✅ delete id=" + created.getId());
            } catch (Exception ex) {
                System.err.println("⚠️ update/find/delete charges échoué : " + ex.getMessage());
            }
        }
    }

    private void testRevenus() {
        System.out.println("\n--- [REVENUS] ---");

        RevenuItemDTO created = null;
        try {
            created = revenusService.create(RevenuCreateDTO.builder()
                    .cabinetId(cabinetId)
                    .titre("Vente produit")
                    .description("Bain de bouche")
                    .montant(new BigDecimal("80"))
                    .dateRevenu(LocalDateTime.now())
                    .build());
            System.out.println("✅ create = " + created);
        } catch (Exception ex) {
            System.err.println("⚠️ create revenus échoué (cabinetId ?) : " + ex.getMessage());
        }

        RevenuFilterDTO filter = RevenuFilterDTO.builder()
                .dateDebut(LocalDate.now().minusMonths(1))
                .dateFin(LocalDate.now())
                .build();

        List<RevenuItemDTO> list = revenusService.list(filter);
        System.out.println("list size = " + (list == null ? 0 : list.size()));
        System.out.println("total     = " + revenusService.total(filter));
        try {
            System.out.println("otherRevenue = " + revenusService.totalOtherRevenue(filter));
        } catch (Exception ignored) { }

        if (created != null) {
            try {
                RevenuItemDTO updated = revenusService.update(created.getId(), RevenuUpdateDTO.builder()
                        .titre(created.getTitre() + " (Maj)")
                        .description(created.getDescription())
                        .montant(new BigDecimal("120"))
                        .dateRevenu(created.getDateRevenu())
                        .build());
                System.out.println("✅ update = " + updated);

                RevenuItemDTO found = revenusService.findById(created.getId());
                System.out.println("✅ findById = " + found);

                revenusService.delete(created.getId());
                System.out.println("✅ delete id=" + created.getId());
            } catch (Exception ex) {
                System.err.println("⚠️ update/find/delete revenus échoué : " + ex.getMessage());
            }
        }
    }

    private void testFactures() {
        System.out.println("\n--- [FACTURES] ---");

        List<CaisseFactureRowDTO> between = factureService.listBetween(
                LocalDate.now().minusMonths(1).atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );
        System.out.println("listBetween size = " + (between == null ? 0 : between.size()));

        CaisseFactureRowDTO created = null;
        try {
            created = factureService.create(FactureCreateDTO.builder()
                    .consultationId(consultationId)
                    .dateFacture(LocalDate.now())
                    .totalFacture(new BigDecimal("300"))
                    .build());
            System.out.println("✅ create = " + created);
        } catch (Exception ex) {
            System.err.println("⚠️ create facture échoué (consultationId ?) : " + ex.getMessage());
        }

        Long factureId = null;
        if (created != null && created.getFactureId() != null) {
            factureId = created.getFactureId();
        } else if (between != null && !between.isEmpty() && between.get(0).getFactureId() != null) {
            factureId = between.get(0).getFactureId();
        }

        if (factureId == null) {
            System.out.println("❌ aucune factureId dispo pour payer/print/pdf");
            return;
        }

        try {
            CaisseFactureRowDTO paid = factureService.payer(factureId, FacturePaiementDTO.builder()
                    .montant(new BigDecimal("50"))
                    .build());
            System.out.println("✅ payer = " + paid);
        } catch (Exception ex) {
            System.err.println("⚠️ payer échoué : " + ex.getMessage());
        }

        try {
            FacturePrintDTO printDTO = factureService.getForPrint(factureId);
            System.out.println("✅ getForPrint numero = " + (printDTO == null ? null : printDTO.getNumeroFacture()));
            // ✅ on ne touche pas aux lignes, car le nom du champ peut être différent
        } catch (Exception ex) {
            System.err.println("⚠️ getForPrint échoué : " + ex.getMessage());
        }

        try {
            byte[] pdf = factureService.exportPdf(factureId);
            System.out.println("✅ exportPdf bytes = " + (pdf == null ? 0 : pdf.length));
        } catch (Exception ex) {
            System.err.println("⚠️ exportPdf échoué : " + ex.getMessage());
        }
    }

    private void testSituationFinanciere() {
        System.out.println("\n--- [SITUATION FINANCIERE] ---");
        try {
            SituationFinanciereDTO dto = sfService.getDerniereSituationFinanciere();
            System.out.println("✅ SF = " + dto);
        } catch (Exception ex) {
            System.err.println("⚠️ SF échoué : " + ex.getMessage());
        }
    }

    private void testCaisseDashboard() {
        System.out.println("\n--- [DASHBOARD CAISSE] ---");

        CaisseDashboardRequestDTO req = CaisseDashboardRequestDTO.builder()
                .dateDebut(LocalDate.now().minusMonths(6))
                .dateFin(LocalDate.now())
                .statut("TOUTES")
                .search("")
                .build();

        try {
            CaisseDashboardResponseDTO res = caisseDashboardService.getDashboard(req, LibelleRole.SECRETAIRE, 1L);
            System.out.println("✅ totalFactures  = " + res.getTotalFactures());
            System.out.println("✅ totalRegle     = " + res.getTotalRegle());
            System.out.println("✅ totalNonRegle  = " + res.getTotalNonRegle());
            System.out.println("✅ totalRevenus   = " + res.getTotalRevenus());
            System.out.println("✅ totalCharges   = " + res.getTotalCharges());
            System.out.println("✅ soldeNet       = " + res.getSoldeNet());
            System.out.println("factures size    = " + (res.getFactures() == null ? 0 : res.getFactures().size()));
        } catch (Exception ex) {
            System.err.println("⚠️ dashboard caisse échoué : " + ex.getMessage());
        }
    }

    private void testChart() {
        System.out.println("\n--- [CHART] ---");
        try {
            CaisseChartDTO chart = chartService.buildRevenusVsCharges(
                    LocalDate.now().minusMonths(6),
                    LocalDate.now()
            );
            System.out.println("labels size = " + (chart.getLabels() == null ? 0 : chart.getLabels().size()));

            byte[] png = chartService.generateRevenusVsChargesPng(chart, 900, 300);
            System.out.println("✅ png bytes = " + (png == null ? 0 : png.length));
        } catch (Exception ex) {
            System.err.println("⚠️ chart échoué : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            new TestModuleCaisseServices().process();
        } catch (Exception e) {
            System.err.println("\n❌ TestModuleCaisseServices FAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
