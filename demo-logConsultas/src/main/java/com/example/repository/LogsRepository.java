package com.example.repository;

import com.example.entity.Log;

import java.time.LocalDateTime;
import java.util.List;

public interface LogsRepository {


    public List<Log> getAll();

    public List<Log> getFilteredLogs(LocalDateTime initialDate, LocalDateTime finalDate, String consulterEmail, String consultedId);

    public Log createLog(Log newLog);
}
