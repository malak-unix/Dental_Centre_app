package ma.dentalTech.repository.modules.dossierMedical.api;

import ma.dentalTech.entities.dossierMedical.DocumentMedical;
import ma.dentalTech.repository.common.CrudRepository;
import java.util.List;

public interface DocumentMedicalRepository extends CrudRepository<DocumentMedical, Long> {

    List<DocumentMedical> findByDossierId(Long dossierId);

    List<DocumentMedical> findByConsultationId(Long consultationId);

    List<DocumentMedical> searchByTitreOrNom(String keyword);

    boolean existsById(Long id);

    long count();

    List<DocumentMedical> findPage(int limit, int offset);
}
