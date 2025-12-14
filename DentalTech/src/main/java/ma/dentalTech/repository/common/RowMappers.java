package ma.dentalTech.repository.common;

import ma.dentalTech.entities.agendaMensuel.AgendaMensuel;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.detailJournee.DetailJournee;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.listeDattente.ListeAttente;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.plageHoraire.PlageHoraire;
import ma.dentalTech.entities.rdv.RDV;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public final class RowMappers {

    private RowMappers() {}

    // =========================
    // Helpers null-safe
    // =========================
    private static Long getLong(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    private static Integer getInt(ResultSet rs, String col) throws SQLException {
        int v = rs.getInt(col);
        return rs.wasNull() ? null : v;
    }

    private static Double getDouble(ResultSet rs, String col) throws SQLException {
        double v = rs.getDouble(col);
        return rs.wasNull() ? null : v;
    }

    private static Boolean getBoolean(ResultSet rs, String col) throws SQLException {
        boolean v = rs.getBoolean(col);
        return rs.wasNull() ? null : v;
    }

    private static LocalDateTime getLdt(ResultSet rs, String col) throws SQLException {
        Timestamp ts = rs.getTimestamp(col);
        return ts == null ? null : ts.toLocalDateTime();
    }

    private static LocalDate getLd(ResultSet rs, String col) throws SQLException {
        java.sql.Date d = rs.getDate(col);
        return d == null ? null : d.toLocalDate();
    }

    private static LocalTime getLt(ResultSet rs, String col) throws SQLException {
        java.sql.Time t = rs.getTime(col);
        return t == null ? null : t.toLocalTime();
    }

    private static StatutFacture toStatutFacture(String v) {
        if (v == null) return null;
        try { return StatutFacture.valueOf(v); }
        catch (Exception e) { return null; }
    }

    private static EtatRendezVous toEtatRendezVous(String v) {
        if (v == null) return null;
        try { return EtatRendezVous.valueOf(v); }
        catch (Exception e) { return null; }
    }

    private static StatutJournee toStatutJournee(String v) {
        if (v == null) return null;
        try { return StatutJournee.valueOf(v); }
        catch (Exception e) { return null; }
    }

    private static Sexe toSexe(String v) {
        if (v == null) return null;
        // DB: 'H'/'F'  | Enum: Homme/Femme
        if ("H".equalsIgnoreCase(v)) return Sexe.Homme;
        if ("F".equalsIgnoreCase(v)) return Sexe.Femme;
        try { return Sexe.valueOf(v); } catch (Exception e) { return null; }
    }

    private static Assurance toAssurance(String v) {
        if (v == null) return null;
        String x = v.trim().toUpperCase();
        // DB: CNSS/CNOPS/MUTUELLE/AUTRE/AUCUNE
        return switch (x) {
            case "CNSS" -> Assurance.CNSS;
            case "CNOPS" -> Assurance.CNOPS;
            case "MUTUELLE" -> Assurance.Mutuelle;
            case "AUTRE" -> Assurance.Autre;
            case "AUCUNE" -> Assurance.Aucune;
            default -> {
                try { yield Assurance.valueOf(v); } catch (Exception e) { yield null; }
            }
        };
    }

    private static EtatCivil toEtatCivil(String v) {
        if (v == null) return null;
        try { return EtatCivil.valueOf(v); }
        catch (Exception e) { return null; }
    }

    private static StatutSituationFinanciere toStatutSituationFinanciere(String v) {
        if (v == null) return null;
        try { return StatutSituationFinanciere.valueOf(v); }
        catch (Exception e) { return null; }
    }

    // ==================================================
    // AGENDA_MENSUEL
    // ==================================================
    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        String moisStr = rs.getString("mois");

        return AgendaMensuel.builder()
                .id(getLong(rs, "id"))
                .medecinId(getLong(rs, "medecin_id"))
                .mois(moisStr != null ? Mois.valueOf(moisStr) : null)
                .annee(getInt(rs, "annee") != null ? getInt(rs, "annee") : 0)
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // DETAIL_JOURNEE
    // ==================================================
    public static DetailJournee mapDetailJournee(ResultSet rs) throws SQLException {
        String etat = rs.getString("etat_jour");

        return DetailJournee.builder()
                .id(getLong(rs, "id"))
                .agendaId(getLong(rs, "agenda_id"))
                .dateJour(getLd(rs, "date_jour"))
                .heureDebutTravaillee(getLt(rs, "heure_debut_travail"))
                .heureFinTravaillee(getLt(rs, "heure_fin_travail"))
                .etatJour(toStatutJournee(etat))
                .commentaire(rs.getString("commentaire"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // LISTE_ATTENTE
    // ==================================================
    public static ListeAttente mapListeAttente(ResultSet rs) throws SQLException {
        return ListeAttente.builder()
                .id(getLong(rs, "id"))
                .nomListe(rs.getString("nom")) // colonne DB = nom
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // RDV
    // ==================================================
    public static RDV mapRdv(ResultSet rs) throws SQLException {
        String statut = rs.getString("statut");

        return RDV.builder()
                .id(getLong(rs, "id"))
                .patientId(getLong(rs, "patient_id"))
                .detailJourneeId(getLong(rs, "detail_journee_id"))
                .listeAttenteId(getLong(rs, "liste_attente_id"))
                .date(getLd(rs, "date_rdv"))
                .heure(getLt(rs, "heure"))
                .motif(rs.getString("motif"))
                .status(toEtatRendezVous(statut))
                .noteMedecin(rs.getString("note_medecin"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // PLAGE_HORAIRE
    // ==================================================
    public static PlageHoraire mapPlageHoraire(ResultSet rs) throws SQLException {
        Boolean dispo = getBoolean(rs, "disponible");

        return PlageHoraire.builder()
                .id(getLong(rs, "id"))
                .detailJourneeId(getLong(rs, "detail_journee_id"))
                .heureDebut(getLt(rs, "heure_debut"))
                .heureFin(getLt(rs, "heure_fin"))
                .disponible(dispo != null ? dispo : Boolean.TRUE)
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // PATIENT
    // ==================================================
    public static Patient mapPatient(ResultSet rs) throws SQLException {
        return Patient.builder()
                .id(getLong(rs, "id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .adresse(rs.getString("adresse"))
                .telephone(rs.getString("telephone"))
                .dateNaissance(getLd(rs, "date_naissance"))
                .numAffiliation(rs.getString("num_affiliation"))
                .etatCivil(toEtatCivil(rs.getString("etat_civil")))
                .sexe(toSexe(rs.getString("sexe")))
                .assurance(toAssurance(rs.getString("assurance")))
                // email n'existe pas dans ton SQL patient, on laisse null
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // FACTURE
    // ==================================================
    public static Facture mapFacture(ResultSet rs) throws SQLException {
        return Facture.builder()
                .id(getLong(rs, "id"))
                .consultationId(getLong(rs, "consultation_id"))
                .dateFacture(getLd(rs, "date_facture"))
                .totalFacture(getDouble(rs, "total_facture"))
                .totalPaye(getDouble(rs, "total_paye"))
                .reste(getDouble(rs, "reste")) // colonne calculée OK
                .statut(toStatutFacture(rs.getString("statut")))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // REVENU
    // ==================================================
    public static Revenues mapRevenu(ResultSet rs) throws SQLException {
        return Revenues.builder()
                .id(getLong(rs, "id"))
                .cabinetId(getLong(rs, "cabinet_id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(getDouble(rs, "montant"))
                .dateRevenu(getLdt(rs, "date_revenu"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // CHARGE
    // ==================================================
    public static Charges mapCharge(ResultSet rs) throws SQLException {
        return Charges.builder()
                .id(getLong(rs, "id"))
                .cabinetId(getLong(rs, "cabinet_id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(getDouble(rs, "montant"))
                .dateCharge(getLdt(rs, "date_charge"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // SITUATION_FINANCIERE
    // ==================================================
    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        return SituationFinanciere.builder()
                .id(getLong(rs, "id"))
                .dossierId(getLong(rs, "dossier_id"))
                .medecinId(getLong(rs, "medecin_id"))
                .totalDesActes(getDouble(rs, "total_des_actes"))
                .totalPaye(getDouble(rs, "total_paye"))
                .credit(getDouble(rs, "credit"))
                .statut(toStatutSituationFinanciere(rs.getString("statut")))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }
}
