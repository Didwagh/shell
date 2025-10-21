package com.project.ai.shell.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent

public class HelloCommands {

    private final ChatClient chatClient;

    public HelloCommands(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @ShellMethod(key = "hello" , value = "I will say hello")
    public String HelloCommands() {

        return chatClient.prompt("say good moringin in three launguages").call().content();
    }
    @ShellMethod(key = "bye" , value = "I will say bye")
    public String ByeCommands() {
        return "bye world is working in shell";
    }
}
