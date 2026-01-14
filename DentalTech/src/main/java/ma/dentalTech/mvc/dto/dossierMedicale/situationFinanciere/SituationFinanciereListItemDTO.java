package ma.dentalTech.mvc.dto.dossierMedicale.situationFinanciere;

import java.time.LocalDate;

/**
 * DTO pour afficher une situation financière dans la liste.
 * Basé sur la maquette: Nom, Solde, Dernière facture, Prochain paiement
 */
public class SituationFinanciereListItemDTO {
    private Long situationFinanciereId;
    private Long dossierId;
    private Long patientId;
    private String patientNomComplet;
    private Double solde; // totalDesActes - totalPaye + credit (peut être négatif)
    private String derniereFacture; // Ex: "F0099 - 10/04/2024" ou vide
    private LocalDate prochainPaiement; // Date du prochain paiement ou null

    // Getters and Setters
    public Long getSituationFinanciereId() {
        return situationFinanciereId;
    }

    public void setSituationFinanciereId(Long situationFinanciereId) {
        this.situationFinanciereId = situationFinanciereId;
    }

    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getPatientNomComplet() {
        return patientNomComplet;
    }

    public void setPatientNomComplet(String patientNomComplet) {
        this.patientNomComplet = patientNomComplet;
    }

    public Double getSolde() {
        return solde;
    }

    public void setSolde(Double solde) {
        this.solde = solde;
    }

    public String getDerniereFacture() {
        return derniereFacture;
    }

    public void setDerniereFacture(String derniereFacture) {
        this.derniereFacture = derniereFacture;
    }

    public LocalDate getProchainPaiement() {
        return prochainPaiement;
    }

    public void setProchainPaiement(LocalDate prochainPaiement) {
        this.prochainPaiement = prochainPaiement;
    }
}
