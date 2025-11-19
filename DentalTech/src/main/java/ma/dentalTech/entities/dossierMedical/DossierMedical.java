package ma.dentalTech.entities.dossierMedical;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ma.dentalTech.entities.patient.Patient;
import ma.dentalTech.entities.antecedents.Antecedent;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "dossiers_medicaux")
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDossier;

    @Column(unique = true, nullable = false)
    private String numeroDossier;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @OneToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL)
    private List<Antecedent> antecedents;

    @PrePersist
    public void initDate() {
        this.dateCreation = LocalDateTime.now();
    }
}