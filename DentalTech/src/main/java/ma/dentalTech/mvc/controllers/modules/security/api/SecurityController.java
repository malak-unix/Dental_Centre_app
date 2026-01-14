package ma.dentalTech.mvc.controllers.modules.security.api;

import ma.dentalTech.mvc.dto.security.BackupDTO;
import ma.dentalTech.mvc.dto.security.LogDTO;
import ma.dentalTech.mvc.dto.security.SessionDTO;

import java.util.List;

public interface SecurityController {
    List<LogDTO> getAllLogs();

    void createBackup();

    void restoreBackup(String fileName);

    List<BackupDTO> listBackups();

    void deleteBackup(String fileName);

    List<SessionDTO> getActiveSessions();
}
