package ma.dentalTech.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;
import ma.dentalTech.entities.enums.Sexe;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Staff extends Utilisateur {

    protected Double salaire;
    protected Double prime;
    protected LocalDate dateRecrutement;
    protected int soldeConge;
    private Object cabinetMedicale;

    // Constructeur manuel appelé par Secretaire
    // Constructeur manuel appelé par Secretaire
    public Staff(String nom, String prenom, String email, String adresse, String cin, String tel,
                 Sexe sexe, String login, String motDePasse, LocalDate lastLoginDate,
                 LocalDate dateNaissance, Double salaire, Double prime,
                 LocalDate dateRecrutement, int soldeConge) {

        super(); // ✅ ne dépend d’aucun constructeur spécifique

        this.setNom(nom);
        this.setPrenom(prenom);
        this.setEmail(email);
        this.setAdresse(adresse);
        this.setCin(cin);
        this.setTel(tel);
        this.setSexe(sexe);
        this.setLogin(login);
        this.setMotDePasse(motDePasse);
        this.setLastLoginDate(lastLoginDate);
        this.setDateNaissance(dateNaissance);

        this.salaire = salaire;
        this.prime = prime;
        this.dateRecrutement = dateRecrutement;
        this.soldeConge = soldeConge;
    }

}