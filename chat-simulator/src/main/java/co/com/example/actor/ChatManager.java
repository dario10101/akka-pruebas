package co.com.example.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;


/**
 * Gestionar un chat, con un numero n de bots que envian periodicamente mensajes
 */
public class ChatManager extends AbstractBehavior<ChatManager.Command> {

    /** Command generico usado para enviar y recibir mensajes */
    public interface Command extends Serializable {}

    /** Comando para indicar el inicio de la simulacion */
    public static class StartCommand implements Command {
        private static final long serialVersionUID = 1;

        /** Numero de bots del chat */
        private final int botsNumber;

        public StartCommand(int botsNumber) {
            this.botsNumber = botsNumber;
        }
        public int getBotsNumber() {
            return this.botsNumber;
        }
    }

    /** Comando para recibir un mensage generado por alguno de los bots */
    public static class NewMessageCommand implements Command {
        private static final long serialVersionUID = 1;

        /** Contenido del nuevo mensaje */
        private final String message;

        /** Nombre del remitente del mensaje */
        private final String senderName;

        public NewMessageCommand(String message, String senderName) {
            this.message = message;
            this.senderName = senderName;
        }
        public String getMessage() {
            return this.message;
        }
        public String getSenderName() {
            return this.senderName;
        }
    }


    private ChatManager(ActorContext<Command> context) {
        super(context);
    }

    /** Crear instancia de actor */
    public static Behavior<Command> create(){
        return Behaviors.setup(ChatManager::new);
    }

    /**
     * Procesamiento de mensages
     * @return Comportamiento por defecto para cada mensaje recibido
     */
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, command -> {
                    this.createChatBots(command.getBotsNumber());
                    return Behaviors.same();
                })
                .onMessage(NewMessageCommand.class, command -> {
                    this.displayMessage(command);
                    return Behaviors.same();
                })
                .onAnyMessage(command -> {
                    System.out.println("default chat manager behavior");
                    return Behaviors.same();
                })
                .build();
    }

    /**
     * Creacion de los chat bots
     * @param botsNumber cantida de bots a crear
     */
    private void createChatBots(int botsNumber) {
        // Actor encargado de crear nuevos mensajes
        String messageGeneratorName = "MESSAGE_GENERATOR_1";
        ActorRef<ChatMessageGenerator.Command> messageGenerator = ActorSystem.create(ChatMessageGenerator.create(), messageGeneratorName);

        for(int i = 0; i < botsNumber; i++) {
            String chatBotName = "CHATBOT_" + i;
            ActorRef<ChatBot.Command> chatBotRef = getContext().spawn(ChatBot.create(), chatBotName);
            chatBotRef.tell(new ChatBot.StartCommand(getContext().getSelf(), messageGenerator));
        }
    }

    private void displayMessage(NewMessageCommand command) {
        System.out.println("[" + command.getSenderName() + "] " + command.getMessage());
    }

}
