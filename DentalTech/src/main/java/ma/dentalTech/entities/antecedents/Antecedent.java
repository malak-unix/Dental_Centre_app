package ma.dentalTech.entities.antecedents;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ma.dentalTech.entities.dossierMedical.DossierMedical;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "antecedents")
public class Antecedent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAntecedent;

    private String libelle;

    @Lob
    private String description;

    private String niveauRisque;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    private DossierMedical dossierMedical;
}