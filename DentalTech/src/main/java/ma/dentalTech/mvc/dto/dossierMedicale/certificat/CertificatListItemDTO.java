package ma.dentalTech.mvc.dto.dossierMedicale.certificat;

import java.time.LocalDate;

/**
 * DTO pour afficher les certificats dans la liste (tableau).
 * Inclut le nom du patient pour l'affichage.
 */
public class CertificatListItemDTO {
    private Long certificatId;
    private Long dossierId;
    private Long patientId;
    private String patientNomComplet; // "Nom Prénom"

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer duree; // en jours
    private String noteMedecin;

    // Getters and Setters
    public Long getCertificatId() {
        return certificatId;
    }

    public void setCertificatId(Long certificatId) {
        this.certificatId = certificatId;
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

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public String getNoteMedecin() {
        return noteMedecin;
    }

    public void setNoteMedecin(String noteMedecin) {
        this.noteMedecin = noteMedecin;
    }
}
