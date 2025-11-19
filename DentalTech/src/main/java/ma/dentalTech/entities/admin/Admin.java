package ma.dentalTech.entities.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ma.dentalTech.entities.utilisateur.Utilisateur;

@Entity
@Data @EqualsAndHashCode(callSuper = true)
@Table(name = "admins")
public class Admin extends Utilisateur {

}