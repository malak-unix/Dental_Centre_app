package ma.dentalTech.repository.common;

import java.util.List;

/**
 * Interface générique CRUD pour toutes les entités.
 */
public interface CrudRepository<T, ID> {

    List<T> findAll();

    T findById(ID id);

    void create(T entity);

    void update(T entity);

    void delete(T entity);

    void deleteById(ID id);
}
