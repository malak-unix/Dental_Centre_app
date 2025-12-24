package ma.dentalTech.entities.role;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.LibelleRole;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    private LibelleRole libelle;
    private List<String> privilege; // Liste simple de strings

    // --- RELATIONS ---

    // Relation One-to-Many : Un Role est attribué à plusieurs Utilisateurs
    @Builder.Default
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    @Override
    public String toString() {
        return """
        Role {
          id = %d,
          libelle = %s,
          privileges = %s,
          utilisateursCount = %d
        }
        """.formatted(
                id,
                libelle,
                privilege,
                utilisateurs == null ? 0 : utilisateurs.size()
        );
    }
}