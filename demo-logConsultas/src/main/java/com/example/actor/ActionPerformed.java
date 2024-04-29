package com.example.actor;

public final class ActionPerformed implements Command {
    public final String description;

    public ActionPerformed(String description) {
        this.description = description;
    }
}