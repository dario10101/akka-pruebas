import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;


/**
 * AbstractBehavior<String> significa que el actor recibe Strings
 */
public class FirstSimpleBehavior extends AbstractBehavior<String> {

    /**
     * se convierte a privado, para no tener que enviar el objeto context en el constructor*
     */
    private FirstSimpleBehavior(ActorContext<String> context) {
        super(context);
    }

    public static Behavior<String> create(){
        // Forma corta
        return Behaviors.setup(FirstSimpleBehavior::new);

        // Forma larga
        /*
        return Behaviors.setup(context -> {
            return new FirstSimpleBehavior(context);
        });
        */
    }

    /**
     * Manejador de mensajes
     */
    @Override
    public Receive<String> createReceive() {
        return newReceiveBuilder()
                .onMessageEquals("say hello", () -> {
                    System.out.println("Hello :)");
                    return this;
                })
                .onMessageEquals("who are you?", () -> {
                    System.out.println("My path is " + this.getContext().getSelf().path());
                    return this;
                })
                .onMessageEquals("create a child", ()-> {
                    ActorRef<String> secondActor = getContext().spawn(FirstSimpleBehavior.create(), "SecondActor");
                    secondActor.tell("who are you?");
                    return this;
                })
                .onAnyMessage(message -> {
                    System.out.println("Received Message: " + message);
                    return this;
                })
                .build();
    }
}
