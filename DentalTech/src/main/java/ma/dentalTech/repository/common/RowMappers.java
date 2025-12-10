import ma.dentalTech.entities.ordonnance.Ordonnance;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class RowMappers {

    private RowMappers() {}

    // ... mapPatient, mapAntecedent, etc.

    public static Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        LocalDate date = null;
        Date sqlDate = rs.getDate("date_ordo");
        if (sqlDate != null) {
            date = sqlDate.toLocalDate();
        }

        LocalDateTime dateCreation = null;
        Timestamp ts = rs.getTimestamp("date_creation");
        if (ts != null) {
            dateCreation = ts.toLocalDateTime();
        }

        return Ordonnance.builder()
                .id(rs.getLong("id"))
                .date(date)
                .dateCreation(dateCreation)
                .build();
    }
}
