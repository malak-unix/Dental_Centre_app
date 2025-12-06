package ma.dentalTech.repository.common;

import ma.dentalTech.common.exceptions.DaoException;

import java.util.List;

public interface CrudRepository<T, ID> {

    List<T> findAll() throws DaoException;

    T findById(ID id) throws DaoException;

    void create(T entity) throws DaoException;

    void update(T entity) throws DaoException;

    void delete(T entity) throws DaoException;

    void deleteById(ID id) throws DaoException;
}
