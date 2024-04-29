package com.example.actor;

import akka.actor.typed.ActorRef;
import com.example.dto.Logs;
import com.example.service.LogRegistry;

import java.time.LocalDateTime;

public final class GetFilteredLogs implements Command {
    public final ActorRef<Logs> replyTo;
    public final LocalDateTime initialDate;
    public final LocalDateTime finalDate;
    public final String consulterEmail;
    public final String consultedId;

    public GetFilteredLogs(String initialDate, String finalDate, String consulterEmail, String consultedId, ActorRef<Logs> replyTo){
        this.replyTo = replyTo;

        this.initialDate = LocalDateTime.parse(initialDate);
        this.finalDate = LocalDateTime.parse(finalDate);
        this.consulterEmail = consulterEmail;
        this.consultedId = consultedId;
    }
}