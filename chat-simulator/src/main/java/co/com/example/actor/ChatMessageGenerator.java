package co.com.example.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;

public class ChatMessageGenerator extends AbstractBehavior<ChatMessageGenerator.Command> {

    /** Command generico usado para enviar y recibir mensajes */
    public interface Command extends Serializable {}

    /** Commando para generar un nuevo mensaje aleatorio */
    public static class CreateRandomMessageCommand implements Command {
        private static final long serialVersionUID = 1;

        /** Actor al cual se debe enviar el mensaje generado */
        private final ActorRef<ChatBot.Command> chatBot;

        public CreateRandomMessageCommand(ActorRef<ChatBot.Command> chatBot) {
            this.chatBot = chatBot;
        }
        public ActorRef<ChatBot.Command> getChatBot() {
            return this.chatBot;
        }
    }


    private ChatMessageGenerator(ActorContext<ChatMessageGenerator.Command> context) {
        super(context);
    }

    /** Crear instancia de actor */
    public static Behavior<ChatMessageGenerator.Command> create(){
        return Behaviors.setup(ChatMessageGenerator::new);
    }

    /**
     * Procesamiento de mensages
     * @return Comportamiento por defecto para cada mensaje recibido
     */
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(CreateRandomMessageCommand.class, command -> {
                    String newMessage = "Test from chat manager generator";
                    command.getChatBot().tell(new ChatBot.ProcessMessageCommand(newMessage));
                    return Behaviors.same();
                })
                .build();
    }

}
