package ma.dentalTech.entities.users;

import lombok.Data;

@Data
public class Admin extends Utilisateur {

    // Constructeur vide explicite
    public Admin() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admin)) return false;
        Admin that = (Admin) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Admin {
                id = %s,
                nom = '%s',
                prenom = '%s',
                login = '%s',
                actif = %s
            }
            """.formatted(
                String.valueOf(id),
                getNom(),
                getPrenom(),
                getLogin(),
                isActif()
        );
    }
}
