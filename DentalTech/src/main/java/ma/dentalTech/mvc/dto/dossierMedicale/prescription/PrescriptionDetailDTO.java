package ma.dentalTech.mvc.dto.dossierMedicale.prescription;

/**
 * DTO pour afficher une prescription avec les détails du médicament.
 */
public class PrescriptionDetailDTO {
    private Long prescriptionId;
    private Long medicamentId;
    private String medicamentNom;
    private String medicamentForme; // COMPRIME, SIROP, etc.
    private int quantite;
    private String frequence;
    private int dureeEnJours;

    // Getters and Setters
    public Long getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(Long prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public Long getMedicamentId() {
        return medicamentId;
    }

    public void setMedicamentId(Long medicamentId) {
        this.medicamentId = medicamentId;
    }

    public String getMedicamentNom() {
        return medicamentNom;
    }

    public void setMedicamentNom(String medicamentNom) {
        this.medicamentNom = medicamentNom;
    }

    public String getMedicamentForme() {
        return medicamentForme;
    }

    public void setMedicamentForme(String medicamentForme) {
        this.medicamentForme = medicamentForme;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public String getFrequence() {
        return frequence;
    }

    public void setFrequence(String frequence) {
        this.frequence = frequence;
    }

    public int getDureeEnJours() {
        return dureeEnJours;
    }

    public void setDureeEnJours(int dureeEnJours) {
        this.dureeEnJours = dureeEnJours;
    }
}
