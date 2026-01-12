package ma.dentalTech.mvc.dto.dossierMedicale.consultation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import ma.dentalTech.entities.enums.StatutConsultation;

public class ConsultationListItemDTO {
    private Long consultationId;
    private Long dossierId;

    private Long patientId;
    private String patientNomComplet;

    private LocalDateTime dateConsultation;
    private StatutConsultation statut;          // sert à afficher la couleur côté UI

    private Long factureId;                     // null si pas de facture
    private BigDecimal totalFacture;          // affichage "€60"
    //GETTERS, SETTERS
    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }

    public Long getDossierId() { return dossierId; }
    public void setDossierId(Long dossierId) { this.dossierId = dossierId; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientNomComplet() { return patientNomComplet; }
    public void setPatientNomComplet(String patientNomComplet) { this.patientNomComplet = patientNomComplet; }

    public LocalDateTime getDateConsultation() { return dateConsultation; }
    public void setDateConsultation(LocalDateTime dateConsultation) { this.dateConsultation = dateConsultation; }

    public StatutConsultation getStatut() { return statut; }
    public void setStatut(StatutConsultation statut) { this.statut = statut; }

    public Long getFactureId() { return factureId; }
    public void setFactureId(Long factureId) { this.factureId = factureId; }

    public BigDecimal getTotalFacture() { return totalFacture; }
    public void setTotalFacture(BigDecimal totalFacture) { this.totalFacture = totalFacture; }
}

