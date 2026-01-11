package ma.dentalTech.entities.users;

import java.time.LocalDate;
import lombok.*;
import ma.dentalTech.entities.enums.Sexe;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Secretaire extends Staff {

    private String numCNSS;
    private Double commission;

    @Builder(builderMethodName = "secretaireBuilder")
    public Secretaire(String nom, String prenom, String email, String adresse, String cin, String tel,
                      Sexe sexe, String login, String motDePasse,
                      LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime,
                      LocalDate dateRecrutement, int soldeConge, String numCNSS, Double commission) {

        // Cet appel correspond maintenant exactement au constructeur de Staff créé ci-dessus
        super(nom, prenom, email, adresse, cin, tel, sexe, login, motDePasse, lastLoginDate,
                dateNaissance, salaire, prime, dateRecrutement, soldeConge);

        this.numCNSS = numCNSS;
        this.commission = commission;
    }

    @Override
    public String toString() {
        return "Secretaire { id = %d, nom = '%s', numCNSS = '%s' }".formatted(getId(), getNom(), numCNSS);
    }
}