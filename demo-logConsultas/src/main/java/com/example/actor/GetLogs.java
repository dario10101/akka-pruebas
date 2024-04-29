package com.example.actor;

import akka.actor.typed.ActorRef;
import com.example.dto.Logs;

public final class GetLogs implements Command {
    public final ActorRef<Logs> replyTo;

    public GetLogs(ActorRef<Logs> replyTo) {
        this.replyTo = replyTo;
    }
}