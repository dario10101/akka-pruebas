import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.SortedSet;
import java.util.TreeSet;

public class ManagerBehavior extends AbstractBehavior<ManagerBehavior.Command> {

    // COMMANDS
    public interface Command extends Serializable {}
    public static class InstructionCommand implements Command {
        private static final long serialVersionUID = 1;
        private String message;

        public InstructionCommand(String message) {
            this.message = message;
        }
        public String getMessage() {
            return message;
        }
    }
    public static class ResultCommand implements Command{
        private static final long serialVersionUID = 1;
        private BigInteger prime;

        public ResultCommand(BigInteger prime) {
            this.prime = prime;
        }
        public BigInteger getPrime() {
            return prime;
        }
    }

    // Conjunto para almacenar los probable primes
    private SortedSet<BigInteger> primes = new TreeSet<>();

    private ManagerBehavior(ActorContext<Command> context) {
        super(context);
    }

    public static Behavior<Command> create(){
        return Behaviors.setup(ManagerBehavior::new);
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(InstructionCommand.class, command -> {
                    if(command.getMessage().equals("start")){
                        createBigPrimes();
                    }
                    return this;
                })
                .onMessage(ResultCommand.class, command -> {
                    primes.add(command.getPrime());
                    System.out.println("I have received " + primes.size() + " numbers");
                    if(primes.size() == 20){
                        primes.forEach(System.out::println);
                    }
                    return this;
                })
                .build();
    }

    private void createBigPrimes(){
        for(int i = 0; i < 20; i++) {
            String workerName = "Worker" + (i + 1);
            ActorRef<WorkerBehavior.Command> worker = getContext().spawn(WorkerBehavior.create(), workerName);
            worker.tell(new WorkerBehavior.Command("start", getContext().getSelf()));
            worker.tell(new WorkerBehavior.Command("start", getContext().getSelf()));
        }
    }
}
