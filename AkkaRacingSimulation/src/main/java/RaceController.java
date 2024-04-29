import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class RaceController extends AbstractBehavior<RaceController.Command> {

    // COMMANDS
    public interface Command extends Serializable {}
    public static class StartCommand implements Command{
        private static final long serialVersionUID = 1;
    }
    public static class RacerUpdateCommand implements Command{
        private static final long serialVersionUID = 1;
        private ActorRef<Racer.Command> racer;
        private int position;

        public RacerUpdateCommand(ActorRef<Racer.Command> racer, int position) {
            this.racer = racer;
            this.position = position;
        }
        public ActorRef<Racer.Command> getRacer() {
            return racer;
        }
        public int getPosition() {
            return position;
        }
    }
    private static class GetPositionsCommand implements Command {
        private static final long serialVersionUID = 1L;
    }
    public static class RacerFinishedCommand implements Command{
        private static final long serialVersionUID = 1L;
        private ActorRef<Racer.Command> racer;

        public RacerFinishedCommand(ActorRef<Racer.Command> racer) {
            this.racer = racer;
        }
        public ActorRef<Racer.Command> getRacer() {
            return racer;
        }
    }


    public RaceController(ActorContext<Command> context) {
        super(context);
    }
    public static Behavior<Command> create() {
        return Behaviors.setup(RaceController::new);
    }

    private Map<ActorRef<Racer.Command>, Integer> currentPositions;
    private Map<ActorRef<Racer.Command>, Long> finishingTimes;
    private long start;
    private final int raceLength = 10;
    private Object TIMER_KEY;


    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
                .onMessage(StartCommand.class, message -> {
                    start = System.currentTimeMillis();
                    currentPositions = new HashMap<>();
                    finishingTimes = new HashMap<>();
                    for (int i = 0; i < 10; i++) {
                        ActorRef<Racer.Command> racer = getContext().spawn(Racer.create(), "racer"+i);
                        currentPositions.put(racer, 0);
                        racer.tell(new Racer.StartCommand(raceLength));
                    }
                    return Behaviors.withTimers(timer -> {
                        timer.startTimerAtFixedRate(TIMER_KEY, new GetPositionsCommand(), Duration.ofSeconds(1));
                        return Behaviors.same();
                    });
                })
                .onMessage(GetPositionsCommand.class, message -> {
                    for (ActorRef<Racer.Command> racer : currentPositions.keySet()) {
                        racer.tell(new Racer.PositionCommand(getContext().getSelf()));
                        displayRace();
                    }
                    return Behaviors.same();
                })
                .onMessage(RacerUpdateCommand.class, message -> {
                    currentPositions.put(message.getRacer(), message.getPosition());
                    return Behaviors.same();
                })
                .onMessage(RacerFinishedCommand.class, message -> {
                    finishingTimes.put(message.getRacer(), System.currentTimeMillis());
                    if(finishingTimes.size() == 10){
                        return raceCompleteMessagehandler();
                    }else {
                        return Behaviors.same();
                    }
                })
                .build();
    }

    public Receive<RaceController.Command> raceCompleteMessagehandler(){
        return newReceiveBuilder()
                .onMessage(GetPositionsCommand.class, command -> {
                    // No se recomienda cerrar aqui, el padre mata a los hiijos al morir
                    /*for(ActorRef<Racer.Command> racer : currentPositions.keySet()){
                        getContext().stop(racer);
                    }*/
                    displayResults();
                    return Behaviors.withTimers(timers -> {
                        timers.cancelAll();
                        return Behaviors.stopped();
                    });
                })
                .build();
    }

    private void displayResults() {
        System.out.println("Results");
        finishingTimes.values().stream().sorted().forEach(it -> {
            for (ActorRef<Racer.Command> key : finishingTimes.keySet()) {
                if (finishingTimes.get(key) == it) {
                    String racerId = key.path().toString().substring(key.path().toString().length() -1);
                    System.out.println("Racer " + racerId + " finished in " + ( (double)it - start ) / 1000 + " seconds.");
                }
            }
        });
    }


    private void displayRace() {
        int displayLength = 160;
        for (int i = 0; i < 50; ++i) System.out.println();
        System.out.println("Race has been running for " + ((System.currentTimeMillis() - start) / 1000) + " seconds.");
        System.out.println("    " + new String (new char[displayLength]).replace('\0', '='));
        int i = 0;
        for (ActorRef<Racer.Command> racer : currentPositions.keySet()) {
            System.out.println(i + " : "  + new String (new char[currentPositions.get(racer) * displayLength / 100]).replace('\0', '*'));
            i++;
        }
    }

}
