package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.mvc.dto.caisse.*;

import java.util.List;

public interface ChargesServiceV2 {

    ChargeItemDTO create(ChargeCreateDTO dto);

    ChargeItemDTO update(Long id, ChargeUpdateDTO dto);

    void delete(Long id);

    ChargeItemDTO findById(Long id);

    List<ChargeItemDTO> list(ChargeFilterDTO filter);

    Double total(ChargeFilterDTO filter);
}
