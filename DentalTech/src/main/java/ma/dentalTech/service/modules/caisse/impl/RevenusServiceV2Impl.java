package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.RevenuesRepository;
import ma.dentalTech.service.modules.caisse.api.RevenusServiceV2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RevenusServiceV2Impl implements RevenusServiceV2 {

    private final RevenuesRepository repository;

    @Override
    public RevenuItemDTO create(RevenuCreateDTO dto) {
        validateCreate(dto);

        Revenues r = Revenues.builder()
                .cabinetId(dto.getCabinetId())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .montant(dto.getMontant())
                .dateRevenu(dto.getDateRevenu())
                .build();

        repository.create(r);   // CrudRepository.create(entity)
        return toItemDTO(r);
    }

    @Override
    public RevenuItemDTO update(Long id, RevenuUpdateDTO dto) {
        if (id == null)
            throw new IllegalArgumentException("id obligatoire");

        validateUpdate(dto);

        Revenues existing = repository.findById(id);
        if (existing == null)
            throw new IllegalArgumentException("Revenu introuvable : " + id);

        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setMontant(dto.getMontant());
        existing.setDateRevenu(dto.getDateRevenu());

        repository.update(existing);
        return toItemDTO(existing);
    }

    @Override
    public void delete(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id obligatoire");

        Revenues existing = repository.findById(id);
        if (existing == null)
            throw new IllegalArgumentException("Revenu introuvable : " + id);

        repository.delete(existing);   // ✅ delete(entity)
    }

    @Override
    public RevenuItemDTO findById(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id obligatoire");

        Revenues r = repository.findById(id);
        if (r == null)
            throw new IllegalArgumentException("Revenu introuvable : " + id);

        return toItemDTO(r);
    }

    @Override
    public List<RevenuItemDTO> list(RevenuFilterDTO filter) {
        LocalDateTime start = toStart(filter);
        LocalDateTime end = toEnd(filter);

        return repository.findByDateBetween(start, end)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double total(RevenuFilterDTO filter) {
        LocalDateTime start = toStart(filter);
        LocalDateTime end = toEnd(filter);

        Double v = repository.calculateTotalRevenus(start, end);
        return v == null ? 0.0 : v;
    }

    @Override
    public Double totalOtherRevenue(RevenuFilterDTO filter) {
        LocalDateTime start = toStart(filter);
        LocalDateTime end = toEnd(filter);

        Double v = repository.calculateTotalOtherRevenue(start, end);
        return v == null ? 0.0 : v;
    }

    // =========================
    // Helpers
    // =========================
    private void validateCreate(RevenuCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO null");
        if (dto.getCabinetId() == null) throw new IllegalArgumentException("cabinetId obligatoire");
        if (dto.getTitre() == null || dto.getTitre().isBlank()) throw new IllegalArgumentException("titre obligatoire");
        if (dto.getMontant() == null) throw new IllegalArgumentException("montant obligatoire");
        if (dto.getMontant().doubleValue() < 0) throw new IllegalArgumentException("montant doit être >= 0");
        if (dto.getDateRevenu() == null) throw new IllegalArgumentException("dateRevenu obligatoire");
    }

    private void validateUpdate(RevenuUpdateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO null");
        if (dto.getTitre() == null || dto.getTitre().isBlank()) throw new IllegalArgumentException("titre obligatoire");
        if (dto.getMontant() == null) throw new IllegalArgumentException("montant obligatoire");
        if (dto.getMontant().doubleValue() < 0) throw new IllegalArgumentException("montant doit être >= 0");
        if (dto.getDateRevenu() == null) throw new IllegalArgumentException("dateRevenu obligatoire");
    }

    private LocalDateTime toStart(RevenuFilterDTO filter) {
        LocalDate d = (filter == null) ? null : filter.getDateDebut();
        return d == null
                ? LocalDate.now().minusMonths(1).atStartOfDay()
                : d.atStartOfDay();
    }

    private LocalDateTime toEnd(RevenuFilterDTO filter) {
        LocalDate d = (filter == null) ? null : filter.getDateFin();
        return d == null
                ? LocalDate.now().atTime(LocalTime.MAX)
                : d.atTime(LocalTime.MAX);
    }

    private RevenuItemDTO toItemDTO(Revenues r) {
        return RevenuItemDTO.builder()
                .id(r.getId())
                .cabinetId(r.getCabinetId())
                .titre(r.getTitre())
                .description(r.getDescription())
                .montant(r.getMontant())
                .dateRevenu(r.getDateRevenu())
                .build();
    }
}
