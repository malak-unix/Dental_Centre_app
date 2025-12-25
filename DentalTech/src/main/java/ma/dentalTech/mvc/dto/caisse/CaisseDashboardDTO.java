package ma.dentalTech.mvc.dto.caisse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO principal pour le Dashboard Caisse
 * Utilisé par :
 * - CaisseDashboardService
 * - CaisseDashboardController
 * - UI Swing (Dashboard Caisse)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaisseDashboardDTO {

    // =========================
    // Période / Contexte
    // =========================
    private LocalDate dateJour;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    // =========================
    // KPI - Facturation
    // =========================
    private Integer totalFacturesDuJour;
    private BigDecimal totalFactures;
    private BigDecimal totalRegle;
    private BigDecimal totalNonRegle;

    // =========================
    // KPI - Revenus & Charges
    // =========================
    private BigDecimal totalRevenusDuJour;
    private BigDecimal totalRevenus;
    private BigDecimal totalCharges;
    private BigDecimal totalChargesDuMois;

    // =========================
    // KPI - Résultat
    // =========================
    private BigDecimal beneficeDuJour;
    private BigDecimal beneficeDuMois;

    // =========================
    // Listes affichées
    // =========================
    private List<CaisseFactureRowDTO> factures;

    // =========================
    // Graphe (Revenus vs Charges)
    // =========================
    private CaisseChartDTO chart;
}
