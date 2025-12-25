package ma.dentalTech.entities.users;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.enums.Sexe;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Staff extends Utilisateur {

    private Double salaire;
    private Double prime;
    private LocalDate dateRecrutement;
    private int soldeConge;

    // ==========================
    // Relation (diagramme) : CabinetMedicale 1 -> * Staff
    // (type Object pour ne pas casser la compilation tant que CabinetMedicale n'existe pas)
    // ==========================
    private Object cabinetMedicale;

    public Staff(String nom, String email, String adresse, String cin, String tel, Sexe sexe, String login, String motDePasse, LocalDate lastLoginDate, LocalDate dateNaissance, Double salaire, Double prime, LocalDate dateRecrutement, Integer soldeCongé) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Staff)) return false;
        Staff that = (Staff) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Staff {
                id = %s,
                nom = '%s',
                prenom = '%s',
                login = '%s',
                salaire = %.2f,
                prime = %.2f,
                dateRecrutement = %s,
                soldeConge = %d
            }
            """.formatted(
                String.valueOf(id),
                getNom(),
                getPrenom(),
                getLogin(),
                salaire != null ? salaire : 0.0,
                prime != null ? prime : 0.0,
                String.valueOf(dateRecrutement),
                soldeConge
        );
    }
}
