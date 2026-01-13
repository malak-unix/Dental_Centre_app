package ma.dentalTech.entities.users;

import lombok.*;
import lombok.experimental.SuperBuilder;
import ma.dentalTech.entities.base.BaseEntity;
import ma.dentalTech.entities.enums.LibelleRole;

/**
 * Entité représentant un rôle avec ses privilèges.
 * DB: role(libelle ENUM, privileges VARCHAR)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Role extends BaseEntity {

    // Correspond à la colonne ENUM "libelle" en DB
    private LibelleRole libelle;

    // Correspond à la colonne "privileges" en DB (CSV: "A,B,C")
    private String privileges;

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
        return "Role{id=" + id + ", libelle=" + libelle + "}";
    }
}
