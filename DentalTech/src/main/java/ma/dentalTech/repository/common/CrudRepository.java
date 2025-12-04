package ma.dentalTech.repository.common;

import ma.dentalTech.entities.facture.Facture;

import java.util.List;

public interface CrudRepository<T, ID> {

    List<T> findAll();

    Facture findById(ID id);

    void create(T patient);

    void update(T patient);

    void delete(T patient);

    void deleteById(ID id);
}
