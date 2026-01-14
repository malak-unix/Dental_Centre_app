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
    public Staff(String nom, String prenom, String email, String adresse, String cin, String tel,
                 Sexe sexe, String login, String motDePasse, LocalDate lastLoginDate,
                 LocalDate dateNaissance, Double salaire, Double prime,
                 LocalDate dateRecrutement, int soldeConge) {

        // Appel au constructeur de Utilisateur (11 paramètres) //aya
        super(nom, prenom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate, dateNaissance, null);

        this.salaire = salaire;
        this.prime = prime;
        this.dateRecrutement = dateRecrutement;
        this.soldeConge = soldeConge;
    }
}