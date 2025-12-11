package com.project.newshell.config;

import ch.qos.logback.core.net.server.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.common.GoogleGenAiThinkingLevel;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private static final Logger log = LoggerFactory.getLogger(ChatClientConfig.class);

    private final MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    private MongoDBAtlasVectorStore vectorStore;

    public ChatClientConfig(MessageChatMemoryAdvisor messageChatMemoryAdvisor, MongoDBAtlasVectorStore vectorStore) {
        this.messageChatMemoryAdvisor = messageChatMemoryAdvisor;
        this.vectorStore = vectorStore;
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel, GoogleGenAiChatOptions chatOptions) {
        log.info("Creating chat client");
        return ChatClient.builder(chatModel).defaultOptions(chatOptions).defaultAdvisors(new SimpleLoggerAdvisor() , messageChatMemoryAdvisor , questionAnswerAdvisor() ).build();
    }

   @Bean("chatOptionsForGemini")
    public GoogleGenAiChatOptions chatOptions() {
        log.info("Creating google genai chat options");
        return GoogleGenAiChatOptions.builder()
                .maxOutputTokens(500)
                .includeThoughts(false)
//                .thinkingLevel(GoogleGenAiThinkingLevel.LOW)
                .thinkingBudget(512)
                .googleSearchRetrieval(true)
                .model("gemini-2.5-flash-lite")
                .temperature(0.7)
                .topK(3)
                .build();
   }
   @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor() {
        log.info("Creating question answer advisor");
        return QuestionAnswerAdvisor.builder(vectorStore).build();

   }
}
