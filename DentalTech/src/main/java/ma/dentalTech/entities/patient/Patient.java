package ma.dentalTech.entities.patient;

import java.time.LocalDate;
import java.time.LocalDateTime;

// Supprimez l'import de JShell, il n'est pas nécessaire
// import jdk.jshell.Snippet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.Assurance;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.entities.enums.EtatCivil;

@Data @AllArgsConstructor @NoArgsConstructor @SuperBuilder
public class Patient extends BaseEntity {

    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    private String email;
    private LocalDate dateNaissance;
    private String NumAffiliation;
    private EtatCivil etatCivil;
    private Sexe sexe;
    private Assurance assurance;


// Toutes les méthodes `builder()`, `getId()`, `setId()`, etc. sont désormais
    // générées automatiquement par Lombok et n'apparaissent PAS ici.
}