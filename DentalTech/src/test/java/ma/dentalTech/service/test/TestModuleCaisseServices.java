package ma.dentalTech.service.test;

import ma.dentalTech.configuration.ApplicationContext;
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
    private final ChartService chartService =
            ApplicationContext.getBean(ChartService.class);

    public void process() {
        System.out.println("\n==================================================");
        System.out.println("            TEST MODULE CAISSE SERVICES");
        System.out.println("==================================================");

        testFactures();
        testCharges();
        testRevenus();
        testChart();
    }

    private void testFactures() {
        System.out.println("\n--- FACTURES ---");

        // seed: facture id 1,2,3 existent normalement
        CaisseFactureRowDTO f1 = factureService.getById(1L);
        System.out.println("Facture#1 statut=" + f1.getStatut() + ", total=" + f1.getTotalFacture() + ", payé=" + f1.getTotalPaye());

        // payer (paiement partiel)
        FacturePaiementDTO pay = FacturePaiementDTO.builder()
                .montant(new BigDecimal("50.00"))
                .build();

        CaisseFactureRowDTO after = factureService.payer(1L, pay);
        System.out.println("Après paiement: statut=" + after.getStatut() + ", payé=" + after.getTotalPaye());

        // liste entre 2 dates
        List<CaisseFactureRowDTO> list = factureService.listBetween(
                LocalDateTime.now().minusYears(2),
                LocalDateTime.now().plusYears(2)
        );
        System.out.println("Factures count=" + list.size());
    }

    private void testCharges() {
        System.out.println("\n--- CHARGES ---");

        ChargeCreateDTO dto = ChargeCreateDTO.builder()
                .cabinetId(1L)
                .titre("Charge test")
                .description("Ajout via test")
                .montant(new BigDecimal("100.00"))
                .dateCharge(LocalDateTime.now())
                .build();

        ChargeItemDTO created = chargesService.create(dto);
        System.out.println("Created charge id=" + created.getId() + ", montant=" + created.getMontant());

        ChargeFilterDTO filter = ChargeFilterDTO.builder()
                .dateDebut(LocalDate.now().minusYears(2))
                .dateFin(LocalDate.now().plusYears(2))
                .build();

        List<ChargeItemDTO> list = chargesService.list(filter);
        Double total = chargesService.total(filter);

        System.out.println("Charges count=" + list.size());
        System.out.println("Charges total=" + total);
    }

    private void testRevenus() {
        System.out.println("\n--- REVENUS ---");

        RevenuCreateDTO dto = RevenuCreateDTO.builder()
                .cabinetId(1L)
                .titre("Revenu test")
                .description("Ajout via test")
                .montant(new BigDecimal("200.00"))
                .dateRevenu(LocalDateTime.now())
                .build();

        RevenuItemDTO created = revenusService.create(dto);
        System.out.println("Created revenu id=" + created.getId() + ", montant=" + created.getMontant());

        RevenuFilterDTO filter = RevenuFilterDTO.builder()
                .dateDebut(LocalDate.now().minusYears(2))
                .dateFin(LocalDate.now().plusYears(2))
                .build();

        List<RevenuItemDTO> list = revenusService.list(filter);
        Double total = revenusService.total(filter);

        System.out.println("Revenus count=" + list.size());
        System.out.println("Revenus total=" + total);
    }

    private void testChart() {
        System.out.println("\n--- CHART ---");

        LocalDate d1 = LocalDate.now().minusMonths(6);
        LocalDate d2 = LocalDate.now();

        CaisseChartDTO chart = chartService.buildRevenusVsCharges(d1, d2);
        System.out.println("chart labels=" + (chart.getLabels() != null ? chart.getLabels().size() : 0));

        byte[] png = chartService.generateRevenusVsChargesPng(chart, 900, 300);
        System.out.println("png bytes=" + (png != null ? png.length : 0));
    }

    public static void main(String[] args) {
        try {
            new TestModuleCaisseServices().process();
            System.out.println("\n✅ TestModuleCaisseServices OK");
        } catch (Exception e) {
            System.err.println("\n❌ TestModuleCaisseServices FAIL: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
