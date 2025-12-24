package ma.dentalTech.entities.users;

import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.LibelleRole;

/**
 * Entité représentant un rôle avec ses privilèges.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Role extends BaseEntity {

    private String libelle;
    private LibelleRole type;
    private List<String> privileges = new ArrayList<>();

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
        return "Role{id=" + id + ", libelle='" + libelle + "', type=" + type + "}";
    }
}
