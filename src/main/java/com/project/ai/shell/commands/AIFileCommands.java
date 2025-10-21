package com.project.ai.shell.commands;

import com.project.ai.shell.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;

@ShellComponent
@RequiredArgsConstructor
public class AIFileCommands {

    private final FileService fileService;
    private final ChatClient.Builder chatClientBuilder;

    @ShellMethod(key = "analyze-file", value = "Analyze a file using AI")
    public String analyzeFile(
            @ShellOption(help = "File path relative to project root") String filePath) {
        try {
            String content = fileService.readFile(filePath);

            ChatClient chatClient = chatClientBuilder.build();

            String prompt = String.format(
                    "Analyze the following file (%s) and provide insights:\n\n%s",
                    filePath,
                    content
            );

            return chatClient.prompt(prompt).call().content();
        } catch (IOException e) {
            return "Error analyzing file: " + e.getMessage();
        }
    }

    @ShellMethod(key = "explain-code", value = "Explain code in a file using AI")
    public String explainCode(
            @ShellOption(help = "File path relative to project root") String filePath) {
        try {
            String content = fileService.readFile(filePath);

            ChatClient chatClient = chatClientBuilder.build();

            String prompt = String.format(
                    "Explain the following code in simple terms:\n\nFile: %s\n\n%s",
                    filePath,
                    content
            );

            return chatClient.prompt(prompt).call().content();
        } catch (IOException e) {
            return "Error explaining code: " + e.getMessage();
        }
    }

    @ShellMethod(key = "review-code", value = "Review code in a file using AI")
    public String reviewCode(
            @ShellOption(help = "File path relative to project root") String filePath) {
        try {
            String content = fileService.readFile(filePath);

            ChatClient chatClient = chatClientBuilder.build();

            String prompt = String.format(
                    "Review the following code and provide suggestions for improvements, " +
                            "potential bugs, and best practices:\n\nFile: %s\n\n%s",
                    filePath,
                    content
            );

            return chatClient.prompt(prompt).call().content();
        } catch (IOException e) {
            return "Error reviewing code: " + e.getMessage();
        }
    }

    @ShellMethod(key = "compare-files", value = "Compare two files using AI")
    public String compareFiles(
            @ShellOption(help = "First file path") String file1,
            @ShellOption(help = "Second file path") String file2) {
        try {
            String content1 = fileService.readFile(file1);
            String content2 = fileService.readFile(file2);

            ChatClient chatClient = chatClientBuilder.build();

            String prompt = String.format(
                    "Compare these two files and highlight the differences:\n\n" +
                            "File 1: %s\n%s\n\n" +
                            "File 2: %s\n%s",
                    file1, content1, file2, content2
            );

            return chatClient.prompt(prompt).call().content();
        } catch (IOException e) {
            return "Error comparing files: " + e.getMessage();
        }
    }

    @ShellMethod(key = "ask-about-file", value = "Ask AI a question about a file")
    public String askAboutFile(
            @ShellOption(help = "File path relative to project root") String filePath,
            @ShellOption(help = "Your question about the file") String question) {
        try {
            String content = fileService.readFile(filePath);

            ChatClient chatClient = chatClientBuilder.build();

            String prompt = String.format(
                    "Based on the following file content, answer this question: %s\n\n" +
                            "File: %s\n\n%s",
                    question, filePath, content
            );

            return chatClient.prompt(prompt).call().content();
        } catch (IOException e) {
            return "Error processing file: " + e.getMessage();
        }
    }

    @ShellMethod(key = "summarize-project", value = "Summarize all Java files in the project")
    public String summarizeProject() {
        try {
            var javaFiles = fileService.listJavaFiles();

            if (javaFiles.isEmpty()) {
                return "No Java files found in the project.";
            }

            StringBuilder allContent = new StringBuilder();
            allContent.append("Project Structure:\n");

            for (String file : javaFiles) {
                allContent.append("\n--- ").append(file).append(" ---\n");
                allContent.append(fileService.readFile(file));
                allContent.append("\n");
            }

            ChatClient chatClient = chatClientBuilder.build();

            String prompt = String.format(
                    "Provide a comprehensive summary of this Java project, including:\n" +
                            "1. Main purpose and functionality\n" +
                            "2. Key components and their responsibilities\n" +
                            "3. Architecture overview\n\n%s",
                    allContent.toString()
            );

            return chatClient.prompt(prompt).call().content();
        } catch (IOException e) {
            return "Error summarizing project: " + e.getMessage();
        }
    }
}
