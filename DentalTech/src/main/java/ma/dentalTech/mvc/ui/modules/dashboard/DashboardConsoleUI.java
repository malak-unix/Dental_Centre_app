package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.dto.CaisseDashboardDTO;
import ma.dentalTech.mvc.dto.DashboardDTO;
import ma.dentalTech.mvc.dto.DashboardFeaturesDTO;

import java.text.DecimalFormat;

public final class DashboardConsoleUI {

    private static final DecimalFormat DF = new DecimalFormat("#0.00");

    private DashboardConsoleUI() {}

    public static void render(DashboardDTO d) {
        if (d == null) {
            System.out.println("Dashboard vide.");
            return;
        }

        DashboardFeaturesDTO f = d.getFeatures();

        printHeader(d);

        if (f != null && f.isVoirCaisse()) {
            printCaisse(d.getCaisseDuJour());
        }

        if (f != null && f.isVoirRdvEtFileAttente()) {
            printRdvEtFileAttente(d);
        }

        if (f != null && f.isVoirNotifications()) {
            printNotifications(d);
        }

        if (f != null && f.isVoirConsultationsEtActes()) {
            printMedecin(d);
        }

        if (f != null && f.isVoirStatsAdmin()) {
            printAdmin(d);
        }

        printFooter();
    }

    private static void printHeader(DashboardDTO d) {
        System.out.println("==================================================");
        System.out.println("                 DASHBOARD GLOBAL");
        System.out.println("--------------------------------------------------");
        System.out.println("Date : " + d.getDateJour());
        System.out.println("Rôle : " + d.getRole());
        System.out.println("==================================================");
    }

    private static void printCaisse(CaisseDashboardDTO c) {
        System.out.println("\n[CAISSE - Aujourd'hui]");
        if (c == null) {
            System.out.println("- (Données caisse indisponibles)");
            return;
        }
        System.out.println("- Total Factures : " + DF.format(n(c.getTotalFactures())));
        System.out.println("- Total Réglé    : " + DF.format(n(c.getTotalRegle())));
        System.out.println("- Total Non Réglé: " + DF.format(n(c.getTotalNonRegle())));
        System.out.println("- Total Revenus  : " + DF.format(n(c.getTotalRevenus())));
        System.out.println("- Total Charges  : " + DF.format(n(c.getTotalCharges())));
        double solde = n(c.getTotalRevenus()) - n(c.getTotalCharges());
        System.out.println("- Solde          : " + DF.format(solde));
    }

    private static void printRdvEtFileAttente(DashboardDTO d) {
        System.out.println("\n[RDV & FILE D'ATTENTE]");
        System.out.println("- RDV du jour            : " + i(d.getNombreRdvDuJour()));
        System.out.println("- Patients en file       : " + i(d.getNombrePatientsEnFileAttente()));
        System.out.println("- RDV en retard          : " + i(d.getNombreRdvEnRetard()));
    }

    private static void printNotifications(DashboardDTO d) {
        System.out.println("\n[NOTIFICATIONS]");
        System.out.println("- Non lues               : " + i(d.getNombreNotificationsNonLues()));
        System.out.println("- Alertes importantes    : " + i(d.getNombreAlertesImportantes()));
        System.out.println("- Notifications système  : " + i(d.getNombreNotificationsSysteme()));
    }

    private static void printMedecin(DashboardDTO d) {
        System.out.println("\n[MEDECIN]");
        System.out.println("- Consultations terminées: " + i(d.getNombreConsultationsTerminees()));
        System.out.println("- Consultations en cours : " + i(d.getNombreConsultationsEnCours()));
        System.out.println("- Actes réalisés         : " + i(d.getNombreActesRealisesDuJour()));
        System.out.println("- Montant actes          : " + DF.format(n(d.getMontantTotalActesDuJour())));

        System.out.println("\n[FINANCIER - Résumé]");
        System.out.println("- Total Factures         : " + DF.format(n(d.getTotalFacturesDuJour())));
        System.out.println("- Total Réglé            : " + DF.format(n(d.getTotalRegleDuJour())));
        System.out.println("- Total Non Réglé        : " + DF.format(n(d.getTotalNonRegleDuJour())));
    }

    private static void printAdmin(DashboardDTO d) {
        System.out.println("\n[ADMIN - STATISTIQUES]");
        System.out.println("- Utilisateurs (Total)   : " + i(d.getNombreUtilisateursTotal()));
        System.out.println("- Médecins               : " + i(d.getNombreMedecins()));
        System.out.println("- Secrétaires            : " + i(d.getNombreSecretaires()));
        System.out.println("- Admins                 : " + i(d.getNombreAdmins()));

        System.out.println("- Patients (Total)       : " + i(d.getNombrePatientsTotal()));
        System.out.println("- Dossiers actifs        : " + i(d.getNombreDossiersActifs()));

        System.out.println("\n[ADMIN - FINANCES]");
        System.out.println("- CA jour                : " + DF.format(n(d.getChiffreAffairesJour())));
        System.out.println("- CA mois                : " + DF.format(n(d.getChiffreAffairesMois())));
        System.out.println("- Charges mois           : " + DF.format(n(d.getTotalChargesMois())));

        System.out.println("\n[ADMIN - ACTIVITÉ]");
        System.out.println("- Connexions du jour     : " + i(d.getNombreConnexionsJour()));
    }

    private static void printFooter() {
        System.out.println("\n==================================================\n");
    }

    private static int i(Integer v) { return v != null ? v : 0; }
    private static double n(Double v) { return v != null ? v : 0.0; }
}
