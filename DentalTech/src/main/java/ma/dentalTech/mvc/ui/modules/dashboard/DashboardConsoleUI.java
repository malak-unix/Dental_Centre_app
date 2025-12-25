package ma.dentalTech.mvc.ui.modules.dashboard;

import ma.dentalTech.mvc.dto.dashboard.DashboardDTO;
import ma.dentalTech.mvc.dto.dashboard.DashboardFeaturesDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.AdminDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.admin.ReferentielStatsDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.MedecinDashboardResponseDTO;
import ma.dentalTech.mvc.dto.dashboard.medecin.PatientCurrentDTO;
import ma.dentalTech.mvc.dto.dashboard.secretaire.SecretaireDashboardResponseDTO;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public final class DashboardConsoleUI {

    private static final DecimalFormat DF = new DecimalFormat("#,##0.00");

    private DashboardConsoleUI() { }

    /**
     * Console renderer (optionnel). Gardé uniquement pour debug.
     * Compatible avec les nouveaux DTO dashboards.
     */
    public static void render(DashboardDTO d) {
        if (d == null) {
            System.out.println("[Dashboard] DTO null");
            return;
        }

        System.out.println("========================================");
        System.out.println("           DASHBOARD (" + safe(d.getRole()) + ")");
        System.out.println("========================================");

        DashboardFeaturesDTO f = d.getFeatures();
        if (f != null) {
            System.out.println("Features: "
                    + "rdv/fileAttente=" + f.isVoirRdvEtFileAttente()
                    + ", clientEnCours=" + f.isVoirClientEnCours()
                    + ", statsAdmin=" + f.isVoirStatsAdmin()
                    + ", caisse=" + f.isVoirCaisse()
                    + ", notifications=" + f.isVoirNotifications());
            System.out.println("----------------------------------------");
        }

        // ===== Secrétaire =====
        SecretaireDashboardResponseDTO sec = d.getSecretaire();
        if (sec != null) {
            System.out.println("[Secrétaire]");
            System.out.println("  - RDV du jour      : " + n(sec.getNbRdvDuJour()));
            System.out.println("  - En attente       : " + n(sec.getNbEnAttente()));
            System.out.println("  - Recette du jour  : " + money(sec.getRecetteDuJour()) + " DH");
            System.out.println("  - RDV list size    : " + (sec.getRdvDuJour() == null ? 0 : sec.getRdvDuJour().size()));
            System.out.println("  - File att. size   : " + (sec.getFileAttente() == null ? 0 : sec.getFileAttente().size()));
            System.out.println("----------------------------------------");
        }

        // ===== Médecin =====
        MedecinDashboardResponseDTO med = d.getMedecin();
        if (med != null) {
            System.out.println("[Médecin]");
            System.out.println("  - Patients du jour : " + n(med.getNbPatientsDuJour()));
            System.out.println("  - RDV du jour      : " + n(med.getNbRdvDuJour()));
            System.out.println("  - Actes réalisés   : " + n(med.getNbActesRealises()));
            System.out.println("  - Recette du jour  : " + money(med.getRecetteDuJour()) + " DH");
            System.out.println("  - RDV list size    : " + (med.getRdvDuJour() == null ? 0 : med.getRdvDuJour().size()));

            PatientCurrentDTO p = med.getPatientEnCours();
            if (p != null) {
                System.out.println("  - Patient en cours : " + safe(p.getNomComplet())
                        + " (tel: " + safe(p.getTel()) + ", statut: " + safe(p.getStatutTraitement()) + ")");
            }
            System.out.println("----------------------------------------");
        }

        // ===== Admin =====
        AdminDashboardResponseDTO admin = d.getAdmin();
        if (admin != null) {
            System.out.println("[Admin]");
            System.out.println("  - Utilisateurs     : " + n(admin.getNbUtilisateurs()));
            System.out.println("  - Admins           : " + n(admin.getNbAdmins()));
            System.out.println("  - Actes réalisés   : " + n(admin.getNbActesRealises()));
            System.out.println("  - Recette du jour  : " + money(admin.getRecetteDuJour()) + " DH");
            System.out.println("  - Users list size  : " + (admin.getUtilisateurs() == null ? 0 : admin.getUtilisateurs().size()));

            ReferentielStatsDTO r = admin.getReferentiels();
            if (r != null) {
                System.out.println("  - Référentiels: actes=" + n(r.getNbActes())
                        + ", médicaments=" + n(r.getNbMedicaments())
                        + ", antécédents=" + n(r.getNbAntecedents())
                        + ", assurances=" + n(r.getNbAssurances()));
            }
            System.out.println("----------------------------------------");
        }

        System.out.println("FIN DASHBOARD");
        System.out.println("========================================");
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static int n(Integer v) {
        return v == null ? 0 : v;
    }

    private static String money(BigDecimal v) {
        if (v == null) return "0.00";
        return DF.format(v);
    }
}
