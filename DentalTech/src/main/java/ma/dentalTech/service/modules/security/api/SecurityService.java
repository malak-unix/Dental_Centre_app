package ma.dentalTech.service.modules.security.api;

import ma.dentalTech.mvc.dto.security.BackupDTO;
import ma.dentalTech.mvc.dto.security.LogDTO;
import ma.dentalTech.mvc.dto.security.SessionDTO;

import java.util.List;

public interface SecurityService {
    List<LogDTO> getAllLogs();

    void createBackup();

    void restoreBackup(String fileName);

    List<BackupDTO> listBackups();

    List<SessionDTO> getActiveSessions();

    void deleteBackup(String fileName);
}
