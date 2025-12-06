package ma.dentalTech.service.modules.caisse.api;

import ma.dentalTech.common.exceptions.ServiceException;
import ma.dentalTech.mvc.dto.CaisseDashboardDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CaisseDashboardService {

    CaisseDashboardDTO getDashboardBetween(LocalDateTime start, LocalDateTime end) throws ServiceException;

    default CaisseDashboardDTO getDashboardToday() throws ServiceException {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = today.plusDays(1).atStartOfDay().minusNanos(1);
        return getDashboardBetween(start, end);
    }
}
