package ma.dentalTech.repository.modules.caisse.api;

import ma.dentalTech.common.exceptions.DaoException;
import ma.dentalTech.entities.facture.Facture;
import ma.dentalTech.repository.common.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FactureRepository extends CrudRepository<Facture, Long> {

    List<Facture> findByDateBetween(LocalDateTime start, LocalDateTime end) throws DaoException;

    Double calculateTotalFactures(LocalDateTime start, LocalDateTime end) throws DaoException;

    Double calculateTotalRegle(LocalDateTime start, LocalDateTime end) throws DaoException;

    Double calculateTotalNonRegle(LocalDateTime start, LocalDateTime end) throws DaoException;
}
