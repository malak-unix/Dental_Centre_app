package ma.dentalTech.repository.common;

import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.SituationFinanciere;

import ma.dentalTech.entities.patient.Patient;

// AGENDA
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.agenda.DetailJournee;
import ma.dentalTech.entities.agenda.PlageHoraire;
import ma.dentalTech.entities.agenda.ListeAttente;
import ma.dentalTech.entities.agenda.RDV;

import ma.dentalTech.entities.enums.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RowMappers {

    // ==================================================
    // Helpers (get nullable)
    // ==================================================
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

    private static BigDecimal getBigDecimal(ResultSet rs, String col) throws SQLException {
        BigDecimal v = rs.getBigDecimal(col);
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

    // ==================================================
    // Enums
    // ==================================================
    private static StatutFacture toStatutFacture(String s) {
        if (s == null) return null;
        return StatutFacture.valueOf(s);
    }

    private static StatutSituationFinanciere toStatutSituationFinanciere(String s) {
        if (s == null) return null;
        return StatutSituationFinanciere.valueOf(s);
    }

    private static Sexe toSexe(String s) {
        if (s == null) return null;
        return Sexe.valueOf(s);
    }

    private static Assurance toAssurance(String s) {
        if (s == null) return null;
        return Assurance.valueOf(s);
    }

    private static Mois toMois(String s) {
        if (s == null) return null;
        return Mois.valueOf(s);
    }

    // ==================================================
    // PATIENT
    // ==================================================
    public static Patient mapPatient(ResultSet rs) throws SQLException {
        return Patient.builder()
                .id(getLong(rs, "id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .dateNaissance(getLd(rs, "date_naissance"))
                .sexe(toSexe(rs.getString("sexe")))
                .telephone(rs.getString("telephone"))
                .adresse(rs.getString("adresse"))
                .assurance(toAssurance(rs.getString("assurance")))
                .baseEntityId(getLong(rs, "base_entity_id"))
                .dateCreation(getLdt(rs, "date_creation"))
                .datedeModification(getLdt(rs, "date_derniere_modification"))
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
                .totalFacture(getBigDecimal(rs, "total_facture"))
                .totalPaye(getBigDecimal(rs, "total_paye"))
                .reste(getBigDecimal(rs, "reste"))
                .statut(toStatutFacture(rs.getString("statut")))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
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
                .montant(getBigDecimal(rs, "montant"))
                .dateRevenu(getLdt(rs, "date_revenu"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
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
                .montant(getBigDecimal(rs, "montant"))
                .dateCharge(getLdt(rs, "date_charge"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
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
                .totalDesActes(getBigDecimal(rs, "total_des_actes"))
                .totalPaye(getBigDecimal(rs, "total_paye"))
                .credit(getBigDecimal(rs, "credit"))
                .statut(toStatutSituationFinanciere(rs.getString("statut")))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }
// ==================================================
// AGENDA - MAPPERS (corrigés حسب Entities)
// ==================================================

    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        return AgendaMensuel.builder()
                .id(getLong(rs, "id"))
                .medecinId(getLong(rs, "medecin_id"))
                .mois(toMois(rs.getString("mois")))
                .annee(getInt(rs, "annee"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static DetailJournee mapDetailJournee(ResultSet rs) throws SQLException {
        LocalDate dateJour = rs.getObject("date_jour", LocalDate.class);
        java.time.LocalTime hDebut = rs.getObject("heure_debut_travail", java.time.LocalTime.class);
        java.time.LocalTime hFin   = rs.getObject("heure_fin_travail", java.time.LocalTime.class);

        return DetailJournee.builder()
                .id(getLong(rs, "id"))
                .agendaId(getLong(rs, "agenda_id"))
                .dateJour(dateJour)
                .heureDebutTravail(hDebut)
                .heureFinTravail(hFin)
                .etatJour(rs.getString("etat_jour"))
                .commentaire(rs.getString("commentaire"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static PlageHoraire mapPlageHoraire(ResultSet rs) throws SQLException {
        java.time.LocalTime hDebut = rs.getObject("heure_debut", java.time.LocalTime.class);
        java.time.LocalTime hFin   = rs.getObject("heure_fin", java.time.LocalTime.class);

        return PlageHoraire.builder()
                .id(getLong(rs, "id"))
                .detailJourneeId(getLong(rs, "detail_journee_id"))
                .heureDebut(hDebut)
                .heureFin(hFin)
                .disponible(getBoolean(rs, "disponible"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static RDV mapRdv(ResultSet rs) throws SQLException {
        LocalDate dateRdv = rs.getObject("date_rdv", LocalDate.class);
        java.time.LocalTime heure = rs.getObject("heure", java.time.LocalTime.class);

        return RDV.builder()
                .id(getLong(rs, "id"))
                .patientId(getLong(rs, "patient_id"))
                .detailJourneeId(getLong(rs, "detail_journee_id"))
                .listeAttenteId(getLong(rs, "liste_attente_id"))
                .dateRdv(dateRdv)
                .heure(heure)
                .motif(rs.getString("motif"))
                .statut(rs.getString("statut"))
                .noteMedecin(rs.getString("note_medecin"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static ListeAttente mapListeAttente(ResultSet rs) throws SQLException {
        return ListeAttente.builder()
                .id(getLong(rs, "id"))
                .nom(rs.getString("nom"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

}
