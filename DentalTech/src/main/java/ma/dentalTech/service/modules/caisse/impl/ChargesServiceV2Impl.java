package ma.dentalTech.service.modules.caisse.impl;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.repository.modules.caisse.api.ChargesRepository;
import ma.dentalTech.service.modules.caisse.api.CaisseValidationService;
import ma.dentalTech.service.modules.caisse.api.ChargesServiceV2;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ChargesServiceV2Impl implements ChargesServiceV2 {

    private final ChargesRepository repository;

    private final CaisseValidationService validation =
            ApplicationContext.getBean(CaisseValidationService.class);

    @Override
    public ChargeItemDTO create(ChargeCreateDTO dto) {
        validation.validateChargeCreate(dto);

        Charges c = Charges.builder()
                .cabinetId(dto.getCabinetId())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                // ✅ entity = Double
                .montant(toDouble(dto.getMontant()))
                .dateCharge(dto.getDateCharge())
                .build();

        repository.create(c);
        return toItemDTO(c);
    }

    @Override
    public ChargeItemDTO update(Long id, ChargeUpdateDTO dto) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");
        validation.validateChargeUpdate(dto);

        Charges existing = repository.findById(id);
        if (existing == null) throw new IllegalArgumentException("Charge introuvable: " + id);

        existing.setTitre(dto.getTitre());
        existing.setDescription(dto.getDescription());
        existing.setMontant(toDouble(dto.getMontant())); // ✅ Double
        existing.setDateCharge(dto.getDateCharge());

        repository.update(existing);
        return toItemDTO(existing);
    }

    @Override
    public void delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id obligatoire");

        Charges charge = repository.findById(id);
        if (charge == null) throw new IllegalArgumentException("Charge introuvable : " + id);

        repository.delete(charge); // ✅ ton repo delete(entity)
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
                .montant(toBigDecimal(c.getMontant())) // ✅ DTO = BigDecimal
                .dateCharge(c.getDateCharge())
                .build();
    }

    private Double toDouble(BigDecimal bd) {
        return bd == null ? null : bd.doubleValue();
    }

    private BigDecimal toBigDecimal(Double d) {
        return d == null ? null : BigDecimal.valueOf(d);
    }
}
