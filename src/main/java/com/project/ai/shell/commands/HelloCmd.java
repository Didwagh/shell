package com.project.ai.shell.commands;

//import lombok.RequiredArgsConstructor;
//import org.springframework.core.Ordered;
//import org.springframework.core.annotation.Order;
//import org.springframework.shell.CommandNotFound;
//import org.springframework.shell.ResultHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.ai.chat.client.ChatClient;
//
//@Component("aiCommandNotFoundHandler")
//@Order(Ordered.HIGHEST_PRECEDENCE)
//@RequiredArgsConstructor
//
//public class CommandNotFoundResultHandler implements ResultHandler<CommandNotFound> {
//
//    private final ChatClient.Builder chatClientBuilder;
//
//    @Override
//    public void handleResult(CommandNotFound result) {
//        // quick log so we immediately know it's invoked
//        String raw = result.getText() != null ? result.getText() : String.join(" ", result.getWords());
//        System.out.println(">>> CommandNotFoundResultHandler invoked for: [" + raw + "]");
//
//        try {
//            String reply = chatClientBuilder.build()
//                    .prompt()
//                    .user("You asked: " + raw + " — (this is the fallback handler replying)")
//                    .call()
//                    .content();
//            System.out.println(">>> fallback answer: " + reply);
//        } catch (Exception e) {
//            System.out.println(">>> fallback error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//}

import com.project.ai.shell.tool.AiTools;
import com.project.ai.shell.tool.FileTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent

public class HelloCmd {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final FileTools fileTools;
    private final AiTools aiTools;

    public HelloCmd(ChatClient.Builder builder, VectorStore vectorStore, FileTools fileTools, AiTools aiTools) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.fileTools = fileTools;
        this.aiTools = aiTools;
    }

        @ShellMethod(value = "Analyze a file using AI")
    public String ask(@ShellOption String query){
        return chatClient.prompt().system("""
        You are a code generation assistant with file management capabilities.
        
        When asked to create or update files:
        1. Generate the COMPLETE file content (all imports, package, full code)
        2. Call the 'writeInFile' tool with the file path and content
        3. Confirm the file was written successfully
        
        CRITICAL: You MUST actually call writeInFile tool to save files. 
       
        Do not just show code - actually write it using the tool.
        """).tools(fileTools , aiTools ).user(query).advisors(new SimpleLoggerAdvisor()).call().content();
        }

}
