package com.project.newshell.config;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    @Bean("JdbcChatMemory")
    public ChatMemory jdbcChatMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }
    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory jdbcChatMemory) {
        return MessageChatMemoryAdvisor.builder(jdbcChatMemory).conversationId("user-conversation").build();
    }

}
