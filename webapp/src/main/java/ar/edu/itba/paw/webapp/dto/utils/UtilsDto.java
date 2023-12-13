package ar.edu.itba.paw.webapp.dto.utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class UtilsDto {

    private UtilsDto() {
        throw new UnsupportedOperationException();
    }

    public static String formatDate(Timestamp timestamp) {
        LocalDate date = timestamp.toLocalDateTime().toLocalDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssX");
        // return date.atStartOfDay().atOffset(ZoneOffset.UTC).toString();
        return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(dtf);
    }
}
