package ma.dentalTech.entities.users;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.LibelleRole;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

    private LibelleRole libelle;
    private List<String> privilege;

    // ==========================
    // Relation (diagramme) : Utilisateur * <-> * Role
    // ==========================
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role)) return false;
        Role that = (Role) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return """
            Role {
                id = %s,
                libelle = %s,
                privilegesCount = %d,
                utilisateursCount = %d
            }
            """.formatted(
                String.valueOf(id),
                String.valueOf(libelle),
                privilege == null ? 0 : privilege.size(),
                utilisateurs == null ? 0 : utilisateurs.size()
        );
    }
}
