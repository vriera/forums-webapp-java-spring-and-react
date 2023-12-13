package ar.edu.itba.paw.models;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public enum SearchOrder {
    MOST_RECENT, LEAST_RECENT, CLOSEST_MATCH, BY_QUESTION_VOTES, BY_ANSWER_VOTES;

    public static class SmartDate {

        private Timestamp time;

        public SmartDate(LocalDateTime dateTime) {
            this(Timestamp.valueOf(dateTime));
        }

        public SmartDate(Timestamp time) {
            this.time = time;

        }

        public Timestamp getTime() {

            return time;
        }

        public void setTime(Timestamp time) {
            this.time = time;
        }

        @Override
        public String toString() {
            if (time.toLocalDateTime().toLocalDate().equals(LocalDate.now())) {
                LocalDateTime localTime = time.toLocalDateTime();
                // <spring:message code="date.today"/>
                return String.format("%02d:%02d", localTime.getHour(), localTime.getMinute());
            } else {
                return time.toLocalDateTime().toLocalDate().toString();
            }
        }

    }
}
