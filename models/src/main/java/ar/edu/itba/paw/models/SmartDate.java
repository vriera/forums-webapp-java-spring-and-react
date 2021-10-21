package ar.edu.itba.paw.models;

import javax.persistence.Entity;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;


public class SmartDate {

    private String date;

    private Timestamp time;


    public SmartDate(){

    }

    public SmartDate(Timestamp time) {
        if( time.toLocalDateTime().toLocalDate().equals(LocalDate.now()) ){
            LocalDateTime localTime = time.toLocalDateTime();
            date = String.format("%d:%d", localTime.getHour() , localTime.getMinute());
        }
        else {
            date = time.toLocalDateTime().toLocalDate().toString();
        }
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
    }

}
