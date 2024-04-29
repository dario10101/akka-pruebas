package com.example;

import akka.NotUsed;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.server.Route;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.ActorSystem;
import com.example.actor.Command;
import com.example.repository.FileLogManager;
import com.example.repository.LogsRepository;
import com.example.routes.LogRoutes;
import com.example.service.LogRegistry;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletionStage;

//#main-class
public class QuickstartApp {
    // #start-http-server
    static void startHttpServer(Route route, ActorSystem<?> system) {
        CompletionStage<ServerBinding> futureBinding =
            Http.get(system).newServerAt("localhost", 8081).bind(route);

        futureBinding.whenComplete((binding, exception) -> {
            if (binding != null) {
                InetSocketAddress address = binding.localAddress();
                system.log().info("Server online at http://{}:{}/",
                    address.getHostString(),
                    address.getPort());
            } else {
                system.log().error("Failed to bind HTTP endpoint, terminating system", exception);
                system.terminate();
            }
        });
    }
    // #start-http-server

    public static void main(String[] args) throws Exception {

        LogsRepository repository = new FileLogManager();

        //#server-bootstrapping
        Behavior<NotUsed> rootBehavior = Behaviors.setup(context -> {

            ActorRef<Command> logRegistryActor =
                    context.spawn(LogRegistry.create(repository), "LogRegistry");

            LogRoutes logRoutes = new LogRoutes(context.getSystem(), logRegistryActor);
            startHttpServer(logRoutes.logRoutes(), context.getSystem());

            return Behaviors.empty();
        });

        // boot up server using the route as defined below
        ActorSystem.create(rootBehavior, "HelloAkkaHttpServer");
        //#server-bootstrapping
    }

}
//#main-class


