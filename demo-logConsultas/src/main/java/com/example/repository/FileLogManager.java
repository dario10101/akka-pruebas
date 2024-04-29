package com.example.repository;

import com.example.entity.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileLogManager implements LogsRepository{

    private final static String LOGS_PATH = "./logs/post-logs.log";
    private final static Logger postLogs = LoggerFactory.getLogger("post-logs");

    private LocalDateTime initialDate;
    private LocalDateTime finalDate;
    private String consulterEmail = "";
    private String consultedId = "";

    @Override
    public List<Log> getAll() {
        return this.readAll(LOGS_PATH);
    }

    @Override
    public List<Log> getFilteredLogs(LocalDateTime initialDate, LocalDateTime finalDate, String consulterEmail, String consultedId) {
        this.initialDate = initialDate;
        this.finalDate = finalDate;
        this.consulterEmail = consulterEmail;
        this.consultedId = consultedId;

        return this.readFiltered(LOGS_PATH);
    }

    @Override
    public Log createLog(Log newLog) {
        String logInfo = newLog.logFormat();
        postLogs.info(logInfo);
        return null;
    }

    private Boolean isValid(Log log){
        if(this.finalDate != null && this.initialDate != null){
            if(!(log.date.isAfter(this.initialDate) && log.date.isBefore(this.finalDate))){
                return false;
            }
        }
        if (!this.consulterEmail.isEmpty()){
            // TODO ¿equalsIgnoreCase?
            if(!log.consulterEmail.equals(this.consulterEmail)){
                return false;
            }
        }
        if (!this.consultedId.isEmpty()){
            // TODO ¿equalsIgnoreCase?
            if(!log.consultedId.equals(this.consultedId)){
                return false;
            }
        }

        return true;
    }

    public List<Log> readAll(String file){
        List<Log> logsInFile = new ArrayList<Log>();
        final String SEPARATOR=",";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (null!=line) {
                String [] fields = line.split(SEPARATOR);
                Log newLog = Log.createFromArray(fields);
                if(newLog != null){
                    logsInFile.add(newLog);
                }

                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si va bien como si salta
            // una excepcion.
            try {
                if (null!=br) {
                    br.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logsInFile;
    }

    public List<Log> readFiltered(String file){
        List<Log> logsInFile = new ArrayList<Log>();
        final String SEPARATOR=",";

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (null!=line) {
                String [] fields = line.split(SEPARATOR);
                Log newLog = Log.createFromArray(fields);
                if(newLog != null){
                    if(this.isValid(newLog)) {
                        logsInFile.add(newLog);
                    }
                }

                line = br.readLine();
            }
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si va bien como si salta
            // una excepcion.
            try {
                if (null!=br) {
                    br.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        return logsInFile;
    }
}
