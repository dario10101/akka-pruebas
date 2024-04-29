package com.example.actor;

import akka.actor.typed.ActorRef;
import com.example.entity.Log;

public final class CreateLog implements Command {
    public final Log log;
    public final ActorRef<ActionPerformed> replyTo;

    public CreateLog(Log log, ActorRef<ActionPerformed> replyTo) {
        this.log = log;
        this.replyTo = replyTo;
    }
}
