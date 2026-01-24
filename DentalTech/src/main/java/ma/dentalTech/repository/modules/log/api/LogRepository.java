package ma.dentalTech.repository.modules.log.api;

import ma.dentalTech.entities.log.Log;

import java.util.List;

public interface LogRepository {
    void create(Log log);
    List<Log> findRecent(int limit);
}
