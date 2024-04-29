package com.example.service;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import com.example.actor.*;
import com.example.dto.Logs;
import com.example.entity.Log;
import com.example.repository.FileLogManager;
import com.example.repository.LogsRepository;
import java.util.*;

public class LogRegistry extends AbstractBehavior<Command> {

    //interface Command {}

    /*public final static class GetLogs implements Command {
        public final ActorRef<Logs> replyTo;

        public GetLogs(ActorRef<Logs> replyTo) {
            this.replyTo = replyTo;
        }
    }

    public final static class CreateLog implements Command {
        public final Log log;
        public final ActorRef<ActionPerformed> replyTo;

        public CreateLog(Log log, ActorRef<ActionPerformed> replyTo) {
            this.log = log;
            this.replyTo = replyTo;
        }
    }

    public final static class GetFilteredLogs implements Command {
        public final ActorRef<Logs> replyTo;

        public GetFilteredLogs(String initialDate, String finalDate, String consulterEmail, String idConsulted, ActorRef<Logs> replyTo){
            this.replyTo = replyTo;
        }
    }*/

    /*public final static class GetLogResponse {
        public final Optional<Log> maybeLog;

        public GetLogResponse(Optional<Log> maybeLog) {
            this.maybeLog = maybeLog;
        }
    }*/

   /* public final static class GetLog implements Command {
        public final String name;
        public final ActorRef<UserRegistry.GetUserResponse> replyTo;
        public GetLog(String name, ActorRef<UserRegistry.GetUserResponse> replyTo) {
            this.name = name;
            this.replyTo = replyTo;
        }
    }

    public final static class ActionPerformed implements Command {
        public final String description;

        public ActionPerformed(String description) {
            this.description = description;
        }
    }*/





    // -----------------------------------------------------------------------------------------
    //private final static Logger postLogs = LoggerFactory.getLogger("post-logs");

    private final LogsRepository repository;


    private LogRegistry(ActorContext<Command> context, LogsRepository repository) {
        super(context);
        this.repository = repository;
    }

    public static Behavior<Command> create(LogsRepository repository) {

        return Behaviors.setup((context) -> {
                LogRegistry newLogRegistry = new LogRegistry(context, repository);
                return newLogRegistry;
            }
        );
        //return Behaviors.setup(LogRegistry::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(GetLogs.class, this::onGetLogs)
                .onMessage(CreateLog.class, this::onCreateLog)
                .onMessage(GetFilteredLogs.class, this::onGetFilteredLogs)
                .build();
    }

    private Behavior<Command> onGetLogs(GetLogs command) {
        // We must be careful not to send out users since it is mutable
        // so for this response we need to make a defensive copy

        List<Log> logsRegistry = repository.getAll();
        command.replyTo.tell(new Logs(Collections.unmodifiableList(new ArrayList<>(logsRegistry))));

        return this;
    }

    private Behavior<Command> onGetFilteredLogs(GetFilteredLogs command) {
        // We must be careful not to send out users since it is mutable
        // so for this response we need to make a defensive copy

        List<Log> logsRegistry = repository.getFilteredLogs(
                command.initialDate,
                command.finalDate,
                command.consulterEmail,
                command.consultedId);
        command.replyTo.tell(new Logs(Collections.unmodifiableList(new ArrayList<>(logsRegistry))));

        return this;
    }

    private Behavior<Command> onCreateLog(CreateLog command) {
        command.replyTo.tell(new ActionPerformed(String.format("Log %s created.", command.log.consulterEmail)));

        repository.createLog(command.log);

        return this;
    }



}
