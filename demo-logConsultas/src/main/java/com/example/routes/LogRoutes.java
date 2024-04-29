package com.example.routes;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Scheduler;
import akka.actor.typed.javadsl.AskPattern;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.Route;
import com.example.actor.*;
import com.example.dto.Logs;
import com.example.entity.Log;
import com.example.service.LogRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.concurrent.CompletionStage;

//import static akka.http.javadsl.server.Directives.pathEnd;
//import static akka.http.javadsl.server.Directives.pathPrefix;
//import static akka.http.javadsl.server.Directives.concat;
import static akka.http.javadsl.server.Directives.*;
import static akka.http.javadsl.server.Directives.onSuccess;


public class LogRoutes {

    private final static Logger log = LoggerFactory.getLogger(LogRoutes.class);
    //private final static Logger log = LoggerFactory.getLogger("get-logs");

    private final ActorRef<Command> logRegistryActor;
    private final Duration askTimeout;
    private final Scheduler scheduler;

    public LogRoutes(ActorSystem<?> system, ActorRef<Command> logRegistryActor) {
        this.logRegistryActor = logRegistryActor;
        scheduler = system.scheduler();
        askTimeout = system.settings().config().getDuration("my-app.routes.ask-timeout");
    }

    private CompletionStage<Logs> getLogs() {
        return AskPattern.ask(logRegistryActor, GetLogs::new, askTimeout, scheduler);
    }

    private CompletionStage<ActionPerformed> createLog(Log log) {
        return AskPattern.ask(logRegistryActor, ref -> new CreateLog(log, ref), askTimeout, scheduler);
    }

    private CompletionStage<Logs> getFilteredLogs(String initialDate, String finalDate, String consulterEmail, String idConsulted) {
        return AskPattern.ask(
            logRegistryActor,
            ref -> new GetFilteredLogs(initialDate, finalDate, consulterEmail, idConsulted, ref),
            askTimeout,
            scheduler
        );
    }

    /**
     * This method creates one route (of possibly many more that will be part of your Web App)
     */
    //#all-routes
    public Route logRoutes() {
        return pathPrefix("logs", () ->
            concat(
                pathEnd(() ->
                    concat(
                        get(() ->
                            onSuccess(getLogs(),
                                logs -> {
                                    log.info("new search by auditor");
                                    return complete(StatusCodes.OK, logs, Jackson.marshaller());
                                }
                            )
                        ),
                        post(() ->
                            entity(
                                Jackson.unmarshaller(Log.class),
                                newLog ->
                                    onSuccess(createLog(newLog), performed -> {
                                        log.info("Create log: {}", performed.description);
                                        return complete(StatusCodes.CREATED, performed, Jackson.marshaller());
                                    })
                            )
                        )
                    )
                ),
                pathPrefix("filter", () ->
                    get(() ->
                        parameter("initialDate", initialDate ->
                            parameter("finalDate", finalDate ->
                                parameterOptional("consulterEmail", consulterEmail ->
                                    parameterOptional("idConsulted", idConsulted ->
                                        onSuccess(
                                            getFilteredLogs(initialDate, finalDate, consulterEmail.orElse(""), idConsulted.orElse("")),
                                            logs -> {
                                                log.info("new filtered search by auditor");
                                                return complete(StatusCodes.OK, logs, Jackson.marshaller());
                                            }
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        );


        /*"Initial date: '" + initialDate
                + "', final date: '" + finalDate
                + "', Consulter email: '" + consulterEmail
                + "' and id consulted: '" + idConsulted + "'"*/

    }
}
