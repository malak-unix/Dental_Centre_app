package ma.dentalTech.mvc.controllers.modules.rdv.batch_implementation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.mvc.controllers.modules.rdv.api.RdvController;
import ma.dentalTech.service.modules.rdv.api.RdvService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RdvControllerImpl implements RdvController {

    private RdvService service;

    @Override
    public void showRdvsOfDay(LocalDate date) {
        try {
            List<RDV> rdvs = service.listerRdvsParDate(date)
                    ;
            if (rdvs.isEmpty()) {
                System.out.println("Aucun RDV pour le " + date);
                return;
            }

            DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");

            System.out.println("=== RDV du " + date + " ===");
            for (RDV rdv : rdvs) {
                String heure = (rdv.getHeure() != null) ? rdv.getHeure().format(tf) : "--:--";
                String statut = (rdv.getStatus() != null) ? rdv.getStatus().name() : "N/A";
                System.out.printf("- [%s] %s | %s%n", heure, statut, rdv.getMotif());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l’affichage des RDV : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void showTodayRdvs() {
        showRdvsOfDay(LocalDate.now());
    }
}
