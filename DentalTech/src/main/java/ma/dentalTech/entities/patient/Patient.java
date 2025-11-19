package ma.dentalTech.entities.patient;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import java.time.LocalDate;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatient;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true)
    private String cin;

    private String adresse;
    private String telephone;
    private String email;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    private String mutuelle;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private DossierMedical dossierMedical;
}