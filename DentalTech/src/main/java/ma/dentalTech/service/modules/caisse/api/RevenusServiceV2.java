package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.mvc.dto.caisse.*;

import java.util.List;

public interface RevenusServiceV2 {

    RevenuItemDTO create(RevenuCreateDTO dto);

    RevenuItemDTO update(Long id, RevenuUpdateDTO dto);

    void delete(Long id);

    RevenuItemDTO findById(Long id);

    List<RevenuItemDTO> list(RevenuFilterDTO filter);

    Double total(RevenuFilterDTO filter);

    // Optionnel: si tu veux distinguer “other revenue” (déjà utilisé dans dashboard)
    Double totalOtherRevenue(RevenuFilterDTO filter);
}
