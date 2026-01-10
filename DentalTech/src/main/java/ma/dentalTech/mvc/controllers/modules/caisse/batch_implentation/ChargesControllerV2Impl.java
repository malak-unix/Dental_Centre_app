package ma.dentalTech.mvc.controllers.modules.caisse.batch_implentation;

import lombok.RequiredArgsConstructor;
import ma.dentalTech.mvc.controllers.modules.caisse.api.ChargesControllerV2;
import ma.dentalTech.mvc.dto.caisse.*;
import ma.dentalTech.service.modules.caisse.api.ChargesServiceV2;

import java.util.List;

@RequiredArgsConstructor
public class ChargesControllerV2Impl implements ChargesControllerV2 {

    private final ChargesServiceV2 service;

    @Override
    public ChargeItemDTO create(ChargeCreateDTO dto) {
        try {
            return service.create(dto);
        } catch (Exception e) {
            throw new RuntimeException("Erreur création charge: " + safeMsg(e), e);
        }
    }

    @Override
    public ChargeItemDTO update(Long id, ChargeUpdateDTO dto) {
        try {
            return service.update(id, dto);
        } catch (Exception e) {
            throw new RuntimeException("Erreur modification charge: " + safeMsg(e), e);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            service.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur suppression charge: " + safeMsg(e), e);
        }
    }

    @Override
    public ChargeItemDTO findById(Long id) {
        try {
            return service.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Erreur récupération charge: " + safeMsg(e), e);
        }
    }

    @Override
    public List<ChargeItemDTO> list(ChargeFilterDTO filter) {
        try {
            return service.list(filter);
        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement charges: " + safeMsg(e), e);
        }
    }

    @Override
    public Double total(ChargeFilterDTO filter) {
        try {
            return service.total(filter);
        } catch (Exception e) {
            throw new RuntimeException("Erreur calcul total charges: " + safeMsg(e), e);
        }
    }

    private String safeMsg(Exception e) {
        return e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
