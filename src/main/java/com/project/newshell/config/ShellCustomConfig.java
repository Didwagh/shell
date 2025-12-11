package com.project.newshell.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shell.result.CommandNotFoundMessageProvider;
import reactor.core.publisher.Flux;

@Configuration
public class ShellCustomConfig {
private static final Logger log = LoggerFactory.getLogger(ShellCustomConfig.class);

    private ChatClient chatClient;

    public ShellCustomConfig(ChatClient chatClient) {
        log.info("Creating shell chat client");
        this.chatClient = chatClient;
    }

    @Bean
    CommandNotFoundMessageProvider myCommandNotFoundMessageProvider(ChatMemory chatMemory) {
        return ctx -> {
            String raw = ctx.text();
            if (raw == null || raw.isBlank()) {
                return "empty command found";
            }

            System.out.println("\nStreaming response:");
            System.out.println("-------------------");

            // Stream the response with real-time output
            Flux<String> responseFlux = chatClient.prompt(raw)
                    .advisors(new SimpleLoggerAdvisor())
                    .stream()
                    .content();

            // Collect chunks while printing in real-time
            String result = responseFlux
                    .doOnNext(System.out::print) // Print each chunk immediately
                    .reduce("", String::concat)  // Combine all chunks
                    .block(); // Wait for completion

            System.out.println("\n-------------------\n");

            return result;
        };
    }
}