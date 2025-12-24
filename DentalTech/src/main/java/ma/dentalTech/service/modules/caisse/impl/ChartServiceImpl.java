package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.mvc.dto.caisse.CaisseChartDTO;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.ChartService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final RevenuesRepository revenuesRepository;
    private final ChargesRepository chargesRepository;

    @Override
    public CaisseChartDTO buildRevenusVsCharges(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) {
            throw new IllegalArgumentException("dateDebut/dateFin obligatoires");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("dateFin doit être après dateDebut");
        }

        YearMonth start = YearMonth.from(dateDebut);
        YearMonth end = YearMonth.from(dateFin);

        List<String> labels = new ArrayList<>();
        List<Double> revenus = new ArrayList<>();
        List<Double> charges = new ArrayList<>();

        YearMonth cur = start;
        while (!cur.isAfter(end)) {

            LocalDateTime from = cur.atDay(1).atStartOfDay();
            LocalDateTime to = cur.atEndOfMonth().atTime(LocalTime.MAX);

            Double r = revenuesRepository.calculateTotalOtherRevenue(from, to);
            Double c = chargesRepository.calculateTotalCharges(from, to);

            String label = cur.getMonth().getDisplayName(TextStyle.SHORT, Locale.FRENCH) + " " + cur.getYear();
            labels.add(label);
            revenus.add(r == null ? 0.0 : r);
            charges.add(c == null ? 0.0 : c);

            cur = cur.plusMonths(1);
        }

        return CaisseChartDTO.builder()
                .title("Revenus vs Charges")
                .labels(labels)
                .revenus(revenus)
                .charges(charges)
                .build();
    }

    @Override
    public byte[] generateRevenusVsChargesPng(CaisseChartDTO chartDTO, int width, int height) {
        if (chartDTO == null) throw new IllegalArgumentException("chartDTO obligatoire");

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        List<String> labels = chartDTO.getLabels();
        List<Double> revenus = chartDTO.getRevenus();
        List<Double> charges = chartDTO.getCharges();

        int n = labels == null ? 0 : labels.size();
        for (int i = 0; i < n; i++) {
            String label = labels.get(i);
            Double r = (revenus != null && i < revenus.size()) ? revenus.get(i) : 0.0;
            Double c = (charges != null && i < charges.size()) ? charges.get(i) : 0.0;

            dataset.addValue(r, "Revenus", label);
            dataset.addValue(c, "Charges", label);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                chartDTO.getTitle() == null ? "Revenus vs Charges" : chartDTO.getTitle(),
                "Période",
                "Montant",
                dataset
        );

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(out, chart, width, height);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur génération graphe PNG: " + e.getMessage(), e);
        }
    }
}
