package com.project.ai.shell.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.CommandNotFound;
import org.springframework.shell.ResultHandler;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class GlobalThrowableHandler implements ResultHandler<Throwable> {

    private final ChatClient.Builder chatClientBuilder;


    private ChatClient chatClient() {
        return chatClientBuilder.build();
    }

    @Override
    public void handleResult(Throwable result) {
        // If it's a CommandNotFound, handle it as fallback AI handler
        if (result instanceof CommandNotFound cnf) {
            String query = cnf.getText() != null ? cnf.getText() : String.join(" ", cnf.getWords());
            System.out.println("🤖 (fallback via Throwable handler): " + query);
            try {
                String answer = chatClient()
                        .prompt()
                        .user(query)
//                        .advisors(new QuestionAnswerAdvisor((org.springframework.ai.vectorstore.VectorStore) vectorStore),
//                                new SimpleLoggerAdvisor())
                        .call()
                        .content();
                System.out.println("\n" + answer + "\n");
            } catch (Exception e) {
                System.out.println("❌ Error in fallback: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Not our problem — rethrow so other handlers / default behavior can run
            throw new RuntimeException(result);
        }
    }
}
