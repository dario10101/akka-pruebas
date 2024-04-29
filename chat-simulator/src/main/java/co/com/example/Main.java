package co.com.example;

import akka.actor.typed.ActorSystem;
import co.com.example.actor.ChatManager;

public class Main {

    public static void main(String[] args) {
        String actorChatName = "CHAT_MANAGER_1";
        int botsNumber = 20;

        ActorSystem<ChatManager.Command> chatManager = ActorSystem.create(ChatManager.create(), actorChatName);
        chatManager.tell(new ChatManager.StartCommand(botsNumber));
    }

}
