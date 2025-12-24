package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;

/**
 * Entité représentant un membre du personnel (staff).
 * Classe parent pour Admin, Médecin et Secrétaire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Staff extends Utilisateur {

    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private Integer soldeCongé;

    @Builder(builderMethodName = "staffBuilder")
    public Staff(String nom, String email, String adresse, String cin, String tel,
                 ma.dentalTech.entities.enums.Sexe sexe, String login, String motDePasse,
                 LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime,
                 LocalDate dateRecrutement, Integer soldeCongé) {
        super(nom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate,
                dateNaissance, null, null);
        this.salaire = salaire;
        this.prime = prime;
        this.dateRecrutement = dateRecrutement;
        this.soldeCongé = soldeCongé;
    }

    @Override
    public String toString() {
        return """
            Staff {
                id = %d,
                nom = '%s',
                salaire = %.2f,
                dateRecrutement = %s
            }
            """.formatted(getId(), getNom(), salaire, dateRecrutement);
    }
}
