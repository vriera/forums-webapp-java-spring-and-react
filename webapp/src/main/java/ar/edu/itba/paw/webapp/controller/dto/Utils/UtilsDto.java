package ar.edu.itba.paw.webapp.controller.dto.Utils;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class UtilsDto {

    public static String formatDate(Timestamp timestamp) {
        System.out.println("formatting");
        LocalDate date = timestamp.toLocalDateTime().toLocalDate();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ssX");
        // return date.atStartOfDay().atOffset(ZoneOffset.UTC).toString();
        return date.atStartOfDay().atOffset(ZoneOffset.UTC).format(dtf);
    }
}
