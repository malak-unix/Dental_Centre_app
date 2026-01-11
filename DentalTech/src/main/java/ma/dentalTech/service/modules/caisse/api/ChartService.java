package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.mvc.dto.caisse.CaisseChartDTO;

import java.time.LocalDate;

public interface ChartService {

    //construire les données du graphe (labels + séries)
    CaisseChartDTO buildRevenusVsCharges(LocalDate dateDebut, LocalDate dateFin);

    //générer l'image PNG (API externe JFreeChart)
    byte[] generateRevenusVsChargesPng(CaisseChartDTO chartDTO, int width, int height);
}
