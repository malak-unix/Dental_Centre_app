package ma.dentalTech.service.modules.security.impl;

import ma.dentalTech.common.utilitaire.RepoFactory;
import ma.dentalTech.common.utilitaire.Transaction;
import ma.dentalTech.entities.log.Log;
import ma.dentalTech.mvc.dto.security.BackupDTO;
import ma.dentalTech.mvc.dto.security.LogDTO;
import ma.dentalTech.mvc.dto.security.SessionDTO;
import ma.dentalTech.repository.modules.log.api.LogRepository;
import ma.dentalTech.service.modules.security.api.SecurityService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityServiceImpl implements SecurityService {

    private final RepoFactory<LogRepository> logRepoFactory;
    private static final String BACKUP_DIR = "backups";
    private static final String DB_NAME = "dentalsoft_db";
    private static final String DB_USER = "root"; // Adapt if needed
    private static final String DB_PASS = ""; // Adapt if needed

    public SecurityServiceImpl(RepoFactory<LogRepository> logRepoFactory) {
        this.logRepoFactory = logRepoFactory;
        initBackupDir();
    }

    private void initBackupDir() {
        File dir = new File(BACKUP_DIR);
        if (!dir.exists())
            dir.mkdirs();
    }

    @Override
    public List<LogDTO> getAllLogs() {
        return Transaction.initTransaction(cnx -> {
            LogRepository repo = logRepoFactory.create(cnx);
            try {
                return repo.findAll().stream()
                        .map(this::mapLog)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        });
    }

    @Override
    public void createBackup() {
        String fileName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + ".sql";
        File file = new File(BACKUP_DIR, fileName);

        // Command for mysqldump
        // Note: mysqldump must be in PATH
        List<String> commands = new ArrayList<>();
        commands.add("mysqldump");
        commands.add("-u" + DB_USER);
        if (!DB_PASS.isEmpty())
            commands.add("-p" + DB_PASS);
        commands.add(DB_NAME);
        commands.add("-r" + file.getAbsolutePath());

        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Backup failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void restoreBackup(String fileName) {
        File file = new File(BACKUP_DIR, fileName);
        if (!file.exists())
            throw new RuntimeException("File not found: " + fileName);

        List<String> commands = new ArrayList<>();
        commands.add("mysql");
        commands.add("-u" + DB_USER);
        if (!DB_PASS.isEmpty())
            commands.add("-p" + DB_PASS);
        commands.add(DB_NAME);
        commands.add("-e");
        commands.add("source " + file.getAbsolutePath());

        try {
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Restore failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<BackupDTO> listBackups() {
        List<BackupDTO> list = new ArrayList<>();
        try {
            Files.list(Paths.get(BACKUP_DIR)).forEach(path -> {
                try {
                    String name = path.getFileName().toString();
                    if (name.endsWith(".sql")) {
                        long time = path.toFile().lastModified();
                        long size = Files.size(path);
                        list.add(new BackupDTO(name,
                                LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()),
                                size));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            list.sort(Comparator.comparing(BackupDTO::creationDate).reversed());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<SessionDTO> getActiveSessions() {
        // Mock data for sessions as we don't have real-time session tracking in DB
        // currently
        List<SessionDTO> sessions = new ArrayList<>();
        sessions.add(new SessionDTO(1L, "admin", "ADMIN", LocalDateTime.now().minusMinutes(10), "ACTIVE"));
        sessions.add(new SessionDTO(5L, "drjihane", "MEDECIN", LocalDateTime.now().minusHours(1), "IDLE"));
        return sessions;
    }

    @Override
    public void deleteBackup(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(BACKUP_DIR, fileName));
        } catch (IOException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    private LogDTO mapLog(Log l) {
        return new LogDTO(l.getId(), l.getUtilisateurId() != null ? "User " + l.getUtilisateurId() : "System",
                l.getAction(), l.getDescription(), l.getDateAction());
    }
}
