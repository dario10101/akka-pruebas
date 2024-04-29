package com.example.dto;

import com.example.entity.Log;

import java.util.List;

public final class Logs{
    public final List<Log> logs;

    public Logs(List<Log> logs) {
        this.logs = logs;
    }
}