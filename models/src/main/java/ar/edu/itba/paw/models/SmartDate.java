package ar.edu.itba.paw.models;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class SmartDate {

    private String date;

    private Timestamp time;

    private LocalDateTime dateTime;
    public SmartDate(){
        date = "";
    }

    private void makeString(Timestamp time){
        if( time.toLocalDateTime().toLocalDate().equals(LocalDate.now()) ){
            LocalDateTime localTime = time.toLocalDateTime();
            date = String.format("<spring:message code=\"date.today\"/> %02d:%02d", localTime.getHour() , localTime.getMinute());
        }
        else {
            date = time.toLocalDateTime().toLocalDate().toString();
        }
    }

    public SmartDate(LocalDateTime dateTime){
        this(Timestamp.valueOf(dateTime));
    }
    public SmartDate(Timestamp time) {
        this.time = time;
        makeString(time);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;


    }

    public Timestamp getTime() {

        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
        makeString(time);
    }

}
