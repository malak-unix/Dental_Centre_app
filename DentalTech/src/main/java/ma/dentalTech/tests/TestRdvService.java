package ma.dentalTech.tests;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.conf.ApplicationContext;
import ma.dentalTech.entities.enums.EtatRendezVous;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.service.modules.rdv.api.RdvService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TestRdvService {

    public static void main(String[] args) {
        System.out.println("=== TEST RDV SERVICE ===");

        RdvService rdvService = ApplicationContext.getBean(RdvService.class);

        try {
            // 1. Planifier un RDV
            RDV rdv = RDV.builder()
                    .date(LocalDate.now().plusDays(1))
                    .heure(LocalTime.of(9, 30))
                    .motif("Contrôle annuel")
                    .status(EtatRendezVous.PREVU)
                    .creePar("TEST_SERVICE")
                    .modifiePar("TEST_SERVICE")
                    .build();

            rdv = rdvService.planifierRdv(rdv);
            System.out.println("RDV planifié avec id = " + rdv.getId());

            // 2. Confirmer le RDV
            rdv = rdvService.confirmerRdv(rdv.getId());
            System.out.println("RDV confirmé, statut = " + rdv.getStatus());

            // 3. Lister RDV par statut CONFIRME
            List<RDV> confirmes = rdvService.listerRdvsParStatut(EtatRendezVous.CONFIRME);
            System.out.println("RDV CONFIRMÉS : " + confirmes.size());

            // 4. Terminer le RDV
            rdv = rdvService.terminerRdv(rdv.getId());
            System.out.println("RDV terminé, statut = " + rdv.getStatus());

            // 5. Lister RDV à venir
            List<RDV> aVenir = rdvService.listerRdvsAVenir();
            System.out.println("RDV à venir : " + aVenir.size());

        } catch (ServiceException e) {
            e.printStackTrace();
        }

        System.out.println("=== FIN TEST RDV SERVICE ===");
    }
}
