package ma.dentalTech.mvc.dto.dossierMedicale.acte;

/**
 * DTO pour afficher un acte avec son prix dans une consultation.
 */
public class ActeInterventionDTO {
    private Long interventionId;
    private Long acteId;
    private String acteLibelle;
    private Double prixPatient;
    private Integer numDent;

    // Getters and Setters
    public Long getInterventionId() {
        return interventionId;
    }

    public void setInterventionId(Long interventionId) {
        this.interventionId = interventionId;
    }

    public Long getActeId() {
        return acteId;
    }

    public void setActeId(Long acteId) {
        this.acteId = acteId;
    }

    public String getActeLibelle() {
        return acteLibelle;
    }

    public void setActeLibelle(String acteLibelle) {
        this.acteLibelle = acteLibelle;
    }

    public Double getPrixPatient() {
        return prixPatient;
    }

    public void setPrixPatient(Double prixPatient) {
        this.prixPatient = prixPatient;
    }

    public Integer getNumDent() {
        return numDent;
    }

    public void setNumDent(Integer numDent) {
        this.numDent = numDent;
    }
}
