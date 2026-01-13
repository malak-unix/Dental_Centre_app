package ma.dentalTech.mvc.dto.dossierMedicale.acte;

/**
 * DTO pour afficher les actes dans la liste (tableau).
 */
public class ActeListItemDTO {
    private Long acteId;
    private String libelle;
    private String categorie;
    private Double prixBase;
    private String description;

    // Getters and Setters
    public Long getActeId() {
        return acteId;
    }

    public void setActeId(Long acteId) {
        this.acteId = acteId;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Double getPrixBase() {
        return prixBase;
    }

    public void setPrixBase(Double prixBase) {
        this.prixBase = prixBase;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
