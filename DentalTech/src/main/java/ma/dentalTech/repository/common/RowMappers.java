package ma.dentalTech.repository.common;

import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.entities.revenues.Revenues;
import ma.dentalTech.entities.charges.Charges;
import ma.dentalTech.entities.situationFinanciere.SituationFinanciere;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class RowMappers {

    private RowMappers() {}

    // ------- helpers -------
    private static Long getLong(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
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
    // PATIENT (table patient)
    // ==================================================
    public static Patient mapPatient(ResultSet rs) throws SQLException {
        return Patient.builder()
                .id(getLong(rs, "id"))
                .nom(rs.getString("nom"))
                .prenom(rs.getString("prenom"))
                .dateNaissance(getLd(rs, "date_naissance"))
                .sexe(rs.getString("sexe")) // si ENUM java -> convertis ici
                .telephone(rs.getString("telephone"))
                .adresse(rs.getString("adresse"))
                .numAffiliation(rs.getString("num_affiliation"))
                .etatCivil(rs.getString("etat_civil"))   // si ENUM java -> convertis ici
                .assurance(rs.getString("assurance"))    // si ENUM java -> convertis ici
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // FACTURE (table facture)
    // ==================================================
    public static Facture mapFacture(ResultSet rs) throws SQLException {
        return Facture.builder()
                .id(getLong(rs, "id"))
                .consultationId(getLong(rs, "consultation_id"))
                .dateFacture(getLd(rs, "date_facture"))
                .totalFacture(rs.getBigDecimal("total_facture"))
                .totalPaye(rs.getBigDecimal("total_paye"))
                // "reste" est calculé en DB, tu peux le lire si ton entity a le champ
                // .reste(rs.getBigDecimal("reste"))
                .statut(rs.getString("statut")) // si ENUM java -> convertis ici
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // REVENU (table revenu)
    // ==================================================
    public static Revenues mapRevenu(ResultSet rs) throws SQLException {
        return Revenues.builder()
                .id(getLong(rs, "id"))
                .cabinetId(getLong(rs, "cabinet_id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(rs.getBigDecimal("montant"))
                .dateRevenu(getLdt(rs, "date_revenu"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // CHARGE (table charge)
    // ==================================================
    public static Charges mapCharge(ResultSet rs) throws SQLException {
        return Charges.builder()
                .id(getLong(rs, "id"))
                .cabinetId(getLong(rs, "cabinet_id"))
                .titre(rs.getString("titre"))
                .description(rs.getString("description"))
                .montant(rs.getBigDecimal("montant"))
                .dateCharge(getLdt(rs, "date_charge"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
    // SITUATION_FINANCIERE (table situation_financiere)
    // ==================================================
    public static SituationFinanciere mapSituationFinanciere(ResultSet rs) throws SQLException {
        return SituationFinanciere.builder()
                .id(getLong(rs, "id"))
                .dossierId(getLong(rs, "dossier_id"))
                .medecinId(getLong(rs, "medecin_id"))
                .totalDesActes(rs.getBigDecimal("total_des_actes"))
                .totalPaye(rs.getBigDecimal("total_paye"))
                .credit(rs.getBigDecimal("credit"))
                .statut(rs.getString("statut")) // si ENUM java -> convertis ici
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }
}
