import akka.actor.typed.ActorSystem;

public class Main {


    public static void main(String[] args) {
        /*
        ActorSystem actorSystem = ActorSystem.create(FirstSimpleBehavior.create(), "FirstActorSystem");
        actorSystem.tell("say hello");
        actorSystem.tell("say hello");
        actorSystem.tell("who are you?");
        actorSystem.tell("create a child");
        actorSystem.tell("hello actor :D");
        */

        ActorSystem<ManagerBehavior.Command> actorSystem = ActorSystem.create(ManagerBehavior.create(), "FirstActorSystem");
        actorSystem.tell(new ManagerBehavior.InstructionCommand("start"));
    }

}
