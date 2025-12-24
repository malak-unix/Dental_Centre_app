package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.service.modules.caisse.api.ChargesServiceV2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChargesServiceV2Impl implements ChargesServiceV2 {

    private final ChargesRepository repository;

    @Override
    public ChargeItemDTO create(ChargeCreateDTO dto) {
        validateCreate(dto);

        Charges c = Charges.builder()
                .cabinetId(dto.getCabinetId())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .montant(dto.getMontant())
                .dateCharge(dto.getDateCharge())
                .build();

        repository.create(c);
        return toItemDTO(c);
    }

    @Override
    public ChargeItemDTO update(Long id, ChargeUpdateDTO dto) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        validateUpdate(dto);

        Charges existing = repository.findById(id);
        if (existing == null) throw new IllegalArgumentException("Charge introuvable: " + id);

        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setMontant(dto.getMontant());
        existing.setDateCharge(dto.getDateCharge());

        repository.update(existing);
        return toItemDTO(existing);
    }
    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id obligatoire");
        }

        Charges charge = repository.findById(id);
        if (charge == null) {
            throw new IllegalArgumentException("Charge introuvable : " + id);
        }

        repository.delete(charge); // ✅ CORRECT
    }

    @Override
    public ChargeItemDTO findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        Charges c = repository.findById(id);
        if (c == null) throw new IllegalArgumentException("Charge introuvable: " + id);
        return toItemDTO(c);
    }

    @Override
    public List<ChargeItemDTO> list(ChargeFilterDTO filter) {
        LocalDateTime start = toStart(filter);
        LocalDateTime end = toEnd(filter);

        return repository.findByDateBetween(start, end)
                .stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Double total(ChargeFilterDTO filter) {
        LocalDateTime start = toStart(filter);
        LocalDateTime end = toEnd(filter);
        Double v = repository.calculateTotalCharges(start, end);
        return v == null ? 0.0 : v;
    }

    // ========================= Helpers =========================

    private void validateCreate(ChargeCreateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO null");
        if (dto.getCabinetId() == null) throw new IllegalArgumentException("cabinetId obligatoire");
        if (dto.getTitre() == null || dto.getTitre().isBlank()) throw new IllegalArgumentException("titre obligatoire");
        if (dto.getMontant() == null) throw new IllegalArgumentException("montant obligatoire");
        if (dto.getMontant().doubleValue() < 0) throw new IllegalArgumentException("montant doit être >= 0");
        if (dto.getDateCharge() == null) throw new IllegalArgumentException("dateCharge obligatoire");
    }

    private void validateUpdate(ChargeUpdateDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO null");
        if (dto.getTitre() == null || dto.getTitre().isBlank()) throw new IllegalArgumentException("titre obligatoire");
        if (dto.getMontant() == null) throw new IllegalArgumentException("montant obligatoire");
        if (dto.getMontant().doubleValue() < 0) throw new IllegalArgumentException("montant doit être >= 0");
        if (dto.getDateCharge() == null) throw new IllegalArgumentException("dateCharge obligatoire");
    }

    private LocalDateTime toStart(ChargeFilterDTO filter) {
        LocalDate d = (filter == null) ? null : filter.getDateDebut();
        return d == null ? LocalDate.now().minusMonths(1).atStartOfDay() : d.atStartOfDay();
    }

    private LocalDateTime toEnd(ChargeFilterDTO filter) {
        LocalDate d = (filter == null) ? null : filter.getDateFin();
        return d == null ? LocalDate.now().atTime(LocalTime.MAX) : d.atTime(LocalTime.MAX);
    }

    private ChargeItemDTO toItemDTO(Charges c) {
        return ChargeItemDTO.builder()
                .id(c.getId())
                .cabinetId(c.getCabinetId())
                .titre(c.getTitre())
                .description(c.getDescription())
                .montant(c.getMontant())
                .dateCharge(c.getDateCharge())
                .build();
    }
}
