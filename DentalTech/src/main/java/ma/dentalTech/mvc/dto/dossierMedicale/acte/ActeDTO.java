package ma.dentalTech.mvc.dto.dossierMedicale.acte;

public record ActeDTO(
                Long id,
                String libelle,
                String categorie,
                Double prixBase,
                String description) {
}
