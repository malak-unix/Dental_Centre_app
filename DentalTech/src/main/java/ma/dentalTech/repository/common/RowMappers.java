package ma.dentalTech.repository.common;

import ma.dentalTech.entities.agenda.*;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.entities.cabinet.Facture;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.entities.cabinet.SituationFinanciere;
import ma.dentalTech.entities.dossierMedical.*;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.entities.patient.Patient;

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

    // =========================
    // Enum converters
    // =========================
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
        if ("H".equalsIgnoreCase(v)) return Sexe.Homme;
        if ("F".equalsIgnoreCase(v)) return Sexe.Femme;
        try { return Sexe.valueOf(v); } catch (Exception e) { return null; }
    }

    private static Assurance toAssurance(String v) {
        if (v == null) return null;
        return switch (v.toUpperCase()) {
            case "CNSS" -> Assurance.CNSS;
            case "CNOPS" -> Assurance.CNOPS;
            case "MUTUELLE" -> Assurance.MUTUELLE;
            case "AUTRE" -> Assurance.AUTRE;
            case "AUCUNE" -> Assurance.AUCUNE;
            default -> null;
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

    public static AgendaMensuel mapAgendaMensuel(ResultSet rs) throws SQLException {
        return AgendaMensuel.builder()
                .id(getLong(rs, "id"))
                .medecinId(getLong(rs, "medecin_id"))
                .mois(Mois.valueOf(rs.getString("mois")))
                .annee(getInt(rs, "annee"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static DetailJournee mapDetailJournee(ResultSet rs) throws SQLException {
        return DetailJournee.builder()
                .id(getLong(rs, "id"))
                .agendaId(getLong(rs, "agenda_id"))
                .dateJour(getLd(rs, "date_jour"))
                .heureDebutTravail(getLt(rs, "heure_debut_travail"))
                .heureFinTravail(getLt(rs, "heure_fin_travail"))
                .etatJour(toStatutJournee(rs.getString("etat_jour")))
                .commentaire(rs.getString("commentaire"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static ListeAttente mapListeAttente(ResultSet rs) throws SQLException {
        return ListeAttente.builder()
                .id(getLong(rs, "id"))
                .patientId(getLong(rs, "patient_id"))
                .nom(rs.getString("nom"))
                .motif(rs.getString("motif"))
                .dateAjout(getLdt(rs, "date_ajout"))
                .priorite(rs.getString("priorite"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    public static RDV mapRdv(ResultSet rs) throws SQLException {
        String st = rs.getString("statut");
        EtatRendezVous statut = null;
        try { statut = (st != null) ? EtatRendezVous.valueOf(st) : null; } catch (Exception ignored) {}

        return RDV.builder()
                .id(getLong(rs, "id"))
                .patientId(getLong(rs, "patient_id"))
                .detailJourneeId(getLong(rs, "detail_journee_id"))
                .listeAttenteId(getLong(rs, "liste_attente_id"))
                .dateRdv(getLd(rs, "date_rdv"))
                .heure(getLt(rs, "heure"))
                .motif(rs.getString("motif"))
                .statut(statut)
                .noteMedecin(rs.getString("note_medecin"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }



    public static PlageHoraire mapPlageHoraire(ResultSet rs) throws SQLException {
        return PlageHoraire.builder()
                .id(getLong(rs, "id"))
                .detailJourneeId(getLong(rs, "detail_journee_id"))
                .heureDebut(getLt(rs, "heure_debut"))
                .heureFin(getLt(rs, "heure_fin"))
                .disponible(getBoolean(rs, "disponible"))
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
                .sexe(toSexe(rs.getString("sexe")))
                .assurance(toAssurance(rs.getString("assurance")))
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
                .reste(getDouble(rs, "reste"))
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

    // ==================================================
// DOSSIER_MEDICAL
// ==================================================
    public static ma.dentalTech.entities.dossierMedical.DossierMedical mapDossierMedical(ResultSet rs) throws SQLException {
        return DossierMedical.builder()
                .id(getLong(rs, "id"))
                .patientId(getLong(rs, "patient_id"))
                .medecinId(getLong(rs, "medecin_id"))
                .notes(rs.getString("notes"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
// ACTE
// ==================================================
    public static ma.dentalTech.entities.dossierMedical.Acte mapActe(ResultSet rs) throws SQLException {
        return Acte.builder()
                .id(getLong(rs, "id"))
                .libelle(rs.getString("libelle"))
                .categorie(rs.getString("categorie"))
                .prixBase(getDouble(rs, "prix_base"))
                .description(rs.getString("description"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
// CONSULTATION
// ==================================================
    private static StatutConsultation toStatutConsultation(String v) {
        if (v == null) return null;
        try { return ma.dentalTech.entities.enums.StatutConsultation.valueOf(v); }
        catch (Exception e) { return null; }
    }

    public static Consultation mapConsultation(ResultSet rs) throws SQLException {
        // colonne SQL = date_consultation (DATETIME)
        LocalDateTime ldt = getLdt(rs, "date_consultation");

        return Consultation.builder()
                .id(getLong(rs, "id"))
                .dossierId(getLong(rs, "dossier_id"))
                .date(ldt == null ? null : ldt) //
                .status(toStatutConsultation(rs.getString("statut")))
                .observationMedecin(rs.getString("observation_medecin"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
// INTERVENTION_MEDECIN
// ==================================================
    public static InterventionMedecin mapInterventionMedecin(ResultSet rs) throws SQLException {
        return InterventionMedecin.builder()
                .id(getLong(rs, "id"))
                .consultationId(getLong(rs, "consultation_id"))
                .acteId(getLong(rs, "acte_id"))
                .prixDePatient(getDouble(rs, "prix_patient"))
                .numDent(getInt(rs, "num_dent"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
// ORDONNANCE
// ==================================================
    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        return Ordonnance.builder()
                .id(getLong(rs, "id"))
                .dossierId(getLong(rs, "dossier_id"))
                .consultationId(getLong(rs, "consultation_id"))
                .date(getLd(rs, "date_ordo"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
// MEDICAMENT
// ==================================================
    private static FormeMedicament toFormeMedicament(String v) {
        if (v == null) return null;
        try { return FormeMedicament.valueOf(v); }
        catch (Exception e) { return null; }
    }

    public static Medicament mapMedicament(ResultSet rs) throws SQLException {
        return Medicament.builder()
                .id(getLong(rs, "id"))
                .nom(rs.getString("nom"))
                .laboratoire(rs.getString("laboratoire"))
                .type(rs.getString("type_medicament"))
                .forme(toFormeMedicament(rs.getString("forme")))
                .remboursable(rs.getBoolean("remboursable"))
                .prixUnitaire(getDouble(rs, "prix_unitaire"))
                .description(rs.getString("description"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }


    // ==================================================
// PRESCRIPTION
// ==================================================
    public static Prescription mapPrescription(ResultSet rs) throws SQLException {
        return Prescription.builder()
                .id(getLong(rs, "id"))
                .ordonnanceId(getLong(rs, "ordonnance_id"))
                .medicamentId(getLong(rs, "medicament_id"))
                .quantite(rs.getInt("quantite"))
                .frequence(rs.getString("frequence"))
                .dureeEnJours(rs.getInt("duree_en_jours"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }
    public static Certificat mapCertificat(ResultSet rs) throws SQLException {
        return Certificat.builder()
                .id(getLong(rs, "id"))
                .dossierId(getLong(rs, "dossier_id"))
                .dateDebut(getLd(rs, "date_debut"))
                .dateFin(getLd(rs, "date_fin"))
                .duree(rs.getInt("duree"))
                .noteMedecin(rs.getString("note_medecin"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }

    // ==================================================
// DOCUMENT_MEDICAL
// ==================================================
    private static TypeDocument toTypeDocument(String v) {
        if (v == null) return null;
        try { return TypeDocument.valueOf(v); }
        catch (Exception e) { return TypeDocument.AUTRE; }
    }

    public static DocumentMedical mapDocumentMedical(ResultSet rs) throws SQLException {
        return DocumentMedical.builder()
                .id(getLong(rs, "id"))
                .dossierId(getLong(rs, "dossier_id"))
                .consultationId(getLong(rs, "consultation_id"))
                .typeDocument(toTypeDocument(rs.getString("type_document")))
                .titre(rs.getString("titre"))
                .nomFichier(rs.getString("nom_fichier"))
                .cheminFichier(rs.getString("chemin_fichier"))
                .tailleOctets(getLong(rs, "taille_octets"))
                .dateDocument(getLdt(rs, "date_document"))
                .dateCreation(getLdt(rs, "date_creation"))
                .dateDerniereModification(getLdt(rs, "date_modification"))
                .creePar(rs.getString("cree_par"))
                .modifiePar(rs.getString("modifie_par"))
                .build();
    }



}
