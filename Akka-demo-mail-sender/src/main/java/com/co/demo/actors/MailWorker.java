package com.co.demo.actors;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

public class MailWorker extends AbstractBehavior<MailWorker.Command> {

    /** Mensajes que puede recibir el actor */
    public static class Command {}

    private MailWorker(ActorContext<Command> context) {
        super(context);
    }

    /** Metodo de ayuda para crear instancia de un actor */
    public static Behavior<Command> create() {
        return Behaviors.setup(MailWorker::new);
    }

    public Receive<Command> createReceive() {
        // TODO
        return null;
    }



}
