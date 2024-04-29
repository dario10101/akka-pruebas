import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public class WorkerBehavior extends AbstractBehavior<WorkerBehavior.Command> {

    public static class Command implements Serializable {
        private static final long serialVersionUID = 1;
        private String message;
        private ActorRef<ManagerBehavior.Command> sender;

        public Command(String message, ActorRef<ManagerBehavior.Command> sender) {
            this.message = message;
            this.sender = sender;
        }
        public String getMessage() {
            return message;
        }
        public ActorRef<ManagerBehavior.Command> getSender() {
            return sender;
        }
    }


    // PropablePrime creado
    private BigInteger prime;

    private WorkerBehavior(ActorContext<Command> context) {
        super(context);
    }
    public static Behavior<Command> create(){
        return Behaviors.setup(WorkerBehavior::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return handleMessagesWhenWeDontYetHaveAPrimeNumber();
    }

    public Receive<Command> handleMessagesWhenWeDontYetHaveAPrimeNumber() {
        return newReceiveBuilder()
                .onAnyMessage(command -> {
                    BigInteger bigInteger = new BigInteger(2000, new Random());
                    prime = bigInteger.nextProbablePrime();
                    command.getSender().tell(new ManagerBehavior.ResultCommand(prime));

                    // La proxima vez, ejecuta el otro handler (para no crear el prime de nuevo)
                    return handleMessagesWhenWeAlreadyHaveAPrimeNumber();
                })
                .build();
    }

    public Receive<Command> handleMessagesWhenWeAlreadyHaveAPrimeNumber(){
        return newReceiveBuilder()
                .onAnyMessage(command -> {
                    if(command.getMessage().equals("start")){
                        command.getSender().tell(new ManagerBehavior.ResultCommand(prime));
                    }
                    return this;
                })
                .build();
    }
}
