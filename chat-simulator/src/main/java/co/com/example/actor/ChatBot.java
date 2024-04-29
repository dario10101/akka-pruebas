package co.com.example.actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.io.Serializable;
import java.time.Duration;


/**
 * Simula un bot, que envia mensajes periodicamente
 */
public class ChatBot extends AbstractBehavior<ChatBot.Command> {

    /** Command generico usado para enviar y recibir mensajes */
    public interface Command extends Serializable {}

    /** Comando para indicar el inicio de la simulacion */
    public static class StartCommand implements ChatBot.Command {
        private static final long serialVersionUID = 1;

        /** Actor al que se deben enviar los mensajes */
        private final ActorRef<ChatManager.Command> chatManager;

        /** Actor que genera mensajes nuevos */
        private final ActorRef<ChatMessageGenerator.Command> messageGenerator;

        public StartCommand(ActorRef<ChatManager.Command> chatManager, ActorRef<ChatMessageGenerator.Command> messageGenerator) {
            this.chatManager = chatManager;
            this.messageGenerator = messageGenerator;
        }
        public ActorRef<ChatManager.Command> getChatManager() {
            return this.chatManager;
        }
        public ActorRef<ChatMessageGenerator.Command> getMessageGenerator() {
            return this.messageGenerator;
        }
    }

    /** Comando para crear instruccion de envio de nuevo mensaje al administrador del chat */
    private static class CreateMessageCommand implements ChatBot.Command {
        private static final long serialVersionUID = 1;
    }

    /** Comando para recibir un mensaje creado para poder enviarlo */
    public static class ProcessMessageCommand implements  ChatBot.Command {
        private static final long serialVersionUID = 1;

        /** Nuevo mensage a enviar */
        private final String message;

        public ProcessMessageCommand(String message) {
            this.message = message;
        }
        public String getMessage() {
            return this.message;
        }
    }

    /*-----------------------------------------------------------------------------------------------------------*/

    /** Referencia al actor padre, a donde se van a enviar los mensages */
    private ActorRef<ChatManager.Command> chatManager;

    /** Referencia al actor encargado de generar mensajes nuevos */
    private ActorRef<ChatMessageGenerator.Command> messageGenerator;

    private ChatBot(ActorContext<ChatBot.Command> context) {
        super(context);
    }

    /** Crear instancia de actor */
    public static Behavior<ChatBot.Command> create(){
        return Behaviors.setup(ChatBot::new);
    }

    /**
     * Procesamiento de mensages
     * @return Comportamiento por defecto para cada mensaje recibido
     */
    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, command -> {
                    this.chatManager = command.getChatManager();
                    this.messageGenerator = command.getMessageGenerator();

                    // Envio de mensajes de forma periodica
                    return Behaviors.withTimers(timer -> {
                        this.startTimer(timer);
                        return Behaviors.same();
                    });
                })
                .onMessage(CreateMessageCommand.class, command -> {
                    this.createMessage();
                    return Behaviors.same();
                })
                .onMessage(ProcessMessageCommand.class, command -> {
                    this.processMessage(command);
                    return Behaviors.same();
                })
                .onAnyMessage(command -> {
                    System.out.println("Default chat bot behavior");
                    return Behaviors.same();
                })
                .build();
    }

    /**
     * Crear nuevo mensage del bot
     * Se solicita el nuevo mensaje al actor encargado de crear mensajes
     */
    private void createMessage() {
        if (this.messageGenerator != null && this.chatManager != null) {
            messageGenerator.tell(new ChatMessageGenerator.CreateRandomMessageCommand(getContext().getSelf()));
        }
    }

    /**
     * Iniciar el envio de nuevo mensaje hacia el chat manager
     * @param newMessageCommand parametros de envio
     */
    private void processMessage(ProcessMessageCommand newMessageCommand) {
        String chatBotName = this.getChatBotName(getContext().getSelf().path().toString());
        String newMessage = newMessageCommand.getMessage();
        this.chatManager.tell(new ChatManager.NewMessageCommand(newMessage, chatBotName));
    }

    /**
     * Iniciar el timer que ejecuta cada periodo de tiempo, una instruccion de
     * inicio de envio de nuevo mensaje
     *
     * @param timer timer que se ejecuta periodicamente
     */
    private void startTimer(TimerScheduler<Command> timer) {
        Object timerKey = null;
        int seconds = this.getRandomInt(1,10);

        timer.startTimerAtFixedRate(timerKey, new CreateMessageCommand(), Duration.ofSeconds(seconds));
    }


    private String getChatBotName(String actorPath) {
        try {
            String[] nameArray = actorPath.split("/");
            return nameArray[nameArray.length-1];
        } catch (Exception ex) {
            return "Unknown";
        }
    }

    private int getRandomInt(int min, int max) {
        try {
            return (int) Math.floor(Math.random() * (max - min + 1) + min);
        }catch (Exception ex) {
            return 5;
        }
    }

}
