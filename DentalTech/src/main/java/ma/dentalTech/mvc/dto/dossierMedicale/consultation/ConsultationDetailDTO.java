package ma.dentalTech.mvc.dto.dossierMedicale.consultation;

import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.mvc.dto.dossierMedicale.acte.ActeInterventionDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour afficher les détails complets d'une consultation.
 * Inclut les actes, ordonnances, certificats liés.
 */
public class ConsultationDetailDTO {
    private Long consultationId;
    private Long dossierId;
    private Long patientId;
    private String patientNomComplet;
    private LocalDateTime dateConsultation;
    private StatutConsultation statut;
    private String observationMedecin;
    
    // Actes effectués
    private List<ActeInterventionDTO> actes;
    private Double totalActes;
    
    // Ordonnances liées
    private List<OrdonnanceSimpleDTO> ordonnances;
    
    // Certificats liés
    private List<CertificatSimpleDTO> certificats;
    
    // Facture
    private Long factureId;
    private Double totalFacture;

    // Getters and Setters
    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
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

    public LocalDateTime getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDateTime dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public StatutConsultation getStatut() {
        return statut;
    }

    public void setStatut(StatutConsultation statut) {
        this.statut = statut;
    }

    public String getObservationMedecin() {
        return observationMedecin;
    }

    public void setObservationMedecin(String observationMedecin) {
        this.observationMedecin = observationMedecin;
    }

    public List<ActeInterventionDTO> getActes() {
        return actes;
    }

    public void setActes(List<ActeInterventionDTO> actes) {
        this.actes = actes;
    }

    public Double getTotalActes() {
        return totalActes;
    }

    public void setTotalActes(Double totalActes) {
        this.totalActes = totalActes;
    }

    public List<OrdonnanceSimpleDTO> getOrdonnances() {
        return ordonnances;
    }

    public void setOrdonnances(List<OrdonnanceSimpleDTO> ordonnances) {
        this.ordonnances = ordonnances;
    }

    public List<CertificatSimpleDTO> getCertificats() {
        return certificats;
    }

    public void setCertificats(List<CertificatSimpleDTO> certificats) {
        this.certificats = certificats;
    }

    public Long getFactureId() {
        return factureId;
    }

    public void setFactureId(Long factureId) {
        this.factureId = factureId;
    }

    public Double getTotalFacture() {
        return totalFacture;
    }

    public void setTotalFacture(Double totalFacture) {
        this.totalFacture = totalFacture;
    }

    // DTOs internes pour simplifier
    public static class OrdonnanceSimpleDTO {
        private Long ordonnanceId;
        private java.time.LocalDate date;

        public OrdonnanceSimpleDTO(Long ordonnanceId, java.time.LocalDate date) {
            this.ordonnanceId = ordonnanceId;
            this.date = date;
        }

        public Long getOrdonnanceId() {
            return ordonnanceId;
        }

        public java.time.LocalDate getDate() {
            return date;
        }
    }

    public static class CertificatSimpleDTO {
        private Long certificatId;
        private java.time.LocalDate dateDebut;
        private java.time.LocalDate dateFin;
        private Integer duree;

        public CertificatSimpleDTO(Long certificatId, java.time.LocalDate dateDebut, java.time.LocalDate dateFin, Integer duree) {
            this.certificatId = certificatId;
            this.dateDebut = dateDebut;
            this.dateFin = dateFin;
            this.duree = duree;
        }

        public Long getCertificatId() {
            return certificatId;
        }

        public java.time.LocalDate getDateDebut() {
            return dateDebut;
        }

        public java.time.LocalDate getDateFin() {
            return dateFin;
        }

        public Integer getDuree() {
            return duree;
        }
    }
}
