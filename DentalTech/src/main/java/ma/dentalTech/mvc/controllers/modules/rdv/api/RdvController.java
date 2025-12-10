package ma.dentalTech.mvc.controllers.modules.rdv.api;

import java.time.LocalDate;

public interface RdvController {

    /**
     * Affiche les RDV d’un jour donné dans la console.
     */
    void showRdvsOfDay(LocalDate date);

    /**
     * Affiche les RDV d’aujourd’hui.
     */
    void showTodayRdvs();
}
