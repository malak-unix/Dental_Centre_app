package ma.dentalTech.mvc.dto.dossierMedicale.ordonnance;

import ma.dentalTech.mvc.dto.dossierMedicale.prescription.PrescriptionDetailDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO pour afficher les détails complets d'une ordonnance.
 * Inclut les prescriptions (médicaments).
 */
public class OrdonnanceDetailDTO {
    private Long ordonnanceId;
    private Long dossierId;
    private Long consultationId;
    private LocalDate date;
    
    // Informations patient
    private Long patientId;
    private String patientNomComplet;
    
    // Informations consultation
    private String consultationLibelle; // "Contrôle annuel" ou date
    
    // Informations médecin
    private String medecinNom;
    
    // Prescriptions
    private List<PrescriptionDetailDTO> prescriptions;

    // Getters and Setters
    public Long getOrdonnanceId() {
        return ordonnanceId;
    }

    public void setOrdonnanceId(Long ordonnanceId) {
        this.ordonnanceId = ordonnanceId;
    }

    public Long getDossierId() {
        return dossierId;
    }

    public void setDossierId(Long dossierId) {
        this.dossierId = dossierId;
    }

    public Long getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(Long consultationId) {
        this.consultationId = consultationId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getConsultationLibelle() {
        return consultationLibelle;
    }

    public void setConsultationLibelle(String consultationLibelle) {
        this.consultationLibelle = consultationLibelle;
    }

    public String getMedecinNom() {
        return medecinNom;
    }

    public void setMedecinNom(String medecinNom) {
        this.medecinNom = medecinNom;
    }

    public List<PrescriptionDetailDTO> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<PrescriptionDetailDTO> prescriptions) {
        this.prescriptions = prescriptions;
    }
}
