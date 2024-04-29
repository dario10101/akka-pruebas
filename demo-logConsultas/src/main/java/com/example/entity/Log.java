package com.example.entity;

import com.example.service.LogRegistry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class Log {

    // TODO demas parametros del log
    public final String consulterEmail;
    public final String consultedId;
    public final String status;

    // TODO fecha y hora, validar tipo de dato a usar
    public final LocalDateTime date;

    @JsonCreator
    public Log(
            @JsonProperty("consulterEmail") String consulterEmail,
            @JsonProperty("consultedId") String consultedId,
            @JsonProperty("status") String status,
            @JsonProperty("date") LocalDateTime date) {
        this.consulterEmail = consulterEmail;
        this.consultedId = consultedId;
        this.status = status;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Log{" +
                "consulterEmail='" + consulterEmail + '\'' +
                ", consultedId='" + consultedId + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public String logFormat(){
        return this.consulterEmail + "," + this.consultedId + "," + this.status;
    }

    /**
     * Create a new log from a String array
     * @param array format: [date, Consulter email, Consulted id, status]
     * @return New Log
     */
    public static Log createFromArray(String[] array){
        if(array.length == 4){
            //LocalDateTime date = LocalDateTime.parse("2015-02-20T06:30:00");
            //System.out.println(date);
            LocalDateTime date = null;
            try{
                // TODO hacer con PF
                date = LocalDateTime.parse(array[0].replace(" ", "T"));
            }catch(Exception e){
                return null;
            }
            return new Log(array[1], array[2], array[3], date);
        }
        return null;
    }
}
