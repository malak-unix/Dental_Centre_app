package ma.dentalTech.mvc.controllers.modules.patient.batch_implementation;

import ma.dentalTech.common.exceptions.ControllerException;
import ma.dentalTech.entities.patient.Antecedents;
import ma.dentalTech.mvc.controllers.modules.patient.api.AntecedentController;
import ma.dentalTech.mvc.dto.patient.AntecedentFormDto;
import ma.dentalTech.mvc.dto.patient.AntecedentListDto;
import ma.dentalTech.service.modules.patient.api.AntecedentService;

import java.util.List;

public class AntecedentControllerImpl implements AntecedentController {

    private final AntecedentService service;

    public AntecedentControllerImpl(AntecedentService service) {
        this.service = service;
    }

    @Override
    public List<AntecedentListDto> listByPatient(Long patientId) {
        try {
            return service.getByPatientId(patientId)
                    .stream()
                    .map(this::toListDto)
                    .toList();
        } catch (Exception e) {
            throw new ControllerException("Erreur UI: lister antécédents", e);
        }
    }

    @Override
    public AntecedentFormDto getById(Long id) {
        try {
            if (id == null) throw new IllegalArgumentException("id null");

            Antecedents a = service.getById(id);
            if (a == null) {
                throw new ControllerException("Antécédent introuvable id=" + id);
            }

            return toFormDto(a);
        } catch (Exception e) {
            throw new ControllerException("Erreur UI: récupérer antécédent", e);
        }
    }


    @Override
    public AntecedentFormDto create(Long patientId, AntecedentFormDto dto) {
        try {
            if (dto == null) throw new IllegalArgumentException("DTO null");
            dto.setPatientId(patientId);

            Antecedents a = toEntity(dto);
            a.setId(null);
            service.create(a);

            return toFormDto(a);
        } catch (Exception e) {
            throw new ControllerException("Erreur UI: création antécédent", e);
        }
    }

    @Override
    public AntecedentFormDto update(Long id, AntecedentFormDto dto) {
        try {
            if (dto == null) throw new IllegalArgumentException("DTO null");
            dto.setId(id);

            Antecedents a = toEntity(dto);
            service.update(a);

            return toFormDto(a);
        } catch (Exception e) {
            throw new ControllerException("Erreur UI: modification antécédent", e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            service.deleteById(id);
        } catch (Exception e) {
            throw new ControllerException("Erreur UI: suppression antécédent", e);
        }
    }

    private AntecedentListDto toListDto(Antecedents a) {
        return AntecedentListDto.builder()
                .id(a.getId())
                .patientId(a.getPatientId())
                .nom(a.getNom())
                .categorie(a.getCategorie())
                .niveauDeRisque(a.getNiveauDeRisque())
                .description(a.getDescription())
                .build();
    }

    private AntecedentFormDto toFormDto(Antecedents a) {
        return AntecedentFormDto.builder()
                .id(a.getId())
                .patientId(a.getPatientId())
                .nom(a.getNom())
                .categorie(a.getCategorie())
                .niveauDeRisque(a.getNiveauDeRisque())
                .description(a.getDescription())
                .build();
    }

    private Antecedents toEntity(AntecedentFormDto dto) {
        Antecedents a = new Antecedents();
        a.setId(dto.getId());
        a.setPatientId(dto.getPatientId());
        a.setNom(dto.getNom());
        a.setCategorie(dto.getCategorie());
        a.setNiveauDeRisque(dto.getNiveauDeRisque());
        a.setDescription(dto.getDescription());
        return a;
    }
}
