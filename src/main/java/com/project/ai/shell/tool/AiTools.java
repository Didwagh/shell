package com.project.ai.shell.tool;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

@Component
public class AiTools {
    private static final Logger log = LoggerFactory.getLogger(AiTools.class);


    private final ChatClient chatClient;

    public AiTools(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }


    @Tool(name = "FileGenerator" , description = "Tool which allows to generate complete file")
    public String readFile(@ToolParam(description = "query of how file you want to generate") String query)
            throws IOException {

        String content = chatClient.prompt().system("""
                you are a file generator which will generate complete file
                not just a snippet but complete file including the imports and package names
                and most importantly you alway generate code in java
                """).user(query).advisors(new SimpleLoggerAdvisor()).call().content();
        log.info("File genereation is success successfully: {}", content);

        return content;
    }
}
