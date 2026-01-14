package ma.dentalTech.mvc.controllers.modules.security.impl;

import ma.dentalTech.mvc.controllers.modules.security.api.SecurityController;
import ma.dentalTech.mvc.dto.security.BackupDTO;
import ma.dentalTech.mvc.dto.security.LogDTO;
import ma.dentalTech.mvc.dto.security.SessionDTO;
import ma.dentalTech.service.modules.security.api.SecurityService;

import java.util.List;

public class SecurityControllerImpl implements SecurityController {

    private final SecurityService service;

    public SecurityControllerImpl(SecurityService service) {
        this.service = service;
    }

    @Override
    public List<LogDTO> getAllLogs() {
        return service.getAllLogs();
    }

    @Override
    public void createBackup() {
        service.createBackup();
    }

    @Override
    public void restoreBackup(String fileName) {
        service.restoreBackup(fileName);
    }

    @Override
    public List<BackupDTO> listBackups() {
        return service.listBackups();
    }

    @Override
    public void deleteBackup(String fileName) {
        service.deleteBackup(fileName);
    }

    @Override
    public List<SessionDTO> getActiveSessions() {
        return service.getActiveSessions();
    }
}
