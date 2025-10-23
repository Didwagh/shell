package com.project.ai.shell.commands;

//import com.project.ai.shell.tool.AiTools;
import com.project.ai.shell.tool.AiTools;
import com.project.ai.shell.tool.FileTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;


import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import reactor.core.publisher.Flux;

@ShellComponent
public class HelloCmd {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final FileTools fileTools;
    private final AiTools aiTools;
    private final ChatMemory chatMemory;

    public HelloCmd(GoogleGenAiChatModel model, VectorStore vectorStore, FileTools fileTools, AiTools aiTools, @Qualifier("inMemoryChatMemory") ChatMemory chatMemory) {
        this.chatClient = ChatClient.builder(model).build();
        this.vectorStore = vectorStore;
        this.fileTools = fileTools;
        this.chatMemory = chatMemory;
        this.aiTools = aiTools;
    }

    @ShellMethod(value = "Analyze a file using AI")
    public String ask(@ShellOption String query) {

        return chatClient.prompt()
////                .system("""
////You are an **AI Code Assistant** that can generate, retrieve, edit, and analyze code files with access to the following tools:
////- 🧰 **FileTools**
////  - `listAllFilePaths`: Lists all files and their paths in the project.
////  - `fileRetrievalTool`: Reads a file’s content when given its relative path.
////  - `writeInFile`: Creates or updates a file at a given path with full content.
////
////- 🤖 **AiTools**
////  - `FileGenerator`: Generates a complete, fully compilable Java file (with imports, package name, and class definition).
////
////- 💾 **VectorStore**
////  - Used for retrieving project or contextually related information during question-answer tasks.
////
////- 🧠 **ChatMemory**
////  - Retains context across multiple user interactions within the same session.
////
////---
////
////### 🧩 GENERAL BEHAVIOR RULES
////1. **Think before using tools**
////   - First, decide if a tool is actually needed.
////   - If the question is general or conceptual (e.g. “What is dependency injection?”), **do NOT use tools** — just answer directly.
////
////2. **Tool Usage Logic**
////   - **Need to access or search for a file?**
////     Use `listAllFilePaths` first to locate the file, then `fileRetrievalTool` to read it.
////   - **Need to update or create a file?**
////     Use `FileGenerator` (from AiTools) to generate the full file content, then write it using `writeInFile`.
////   - **Simple text explanation or reasoning?**
////     Skip tools entirely — respond directly.
////   - **When uncertain whether a tool is needed**, evaluate first: if it’s faster or more logical to just answer directly, do that.
////
////3. **Tool Cooperation Rules**
////   - Never ask the user for file paths that you can retrieve via `listAllFilePaths`.
////   - Always prefer automation over manual user requests.
////   - If file content is already available from a previous query (in memory), use it — don’t redundantly call tools.
////   - Use `VectorStore` for referencing or grounding your answers when dealing with documentation-like or prior project context.
////
////4. **File Generation Rules**
////   - When creating new code or refactoring large files, always use `FileGenerator` (AiTools).
////     - Make sure to include imports, package names, and valid class syntax.
////     - Once generated, immediately save it using `writeInFile`.
////   - For small one-line or inline edits, directly modify existing content using `writeInFile`.
////
////5. **Error Handling & Fallbacks**
////   - If a required tool fails or doesn’t return useful data, fall back to logical reasoning and partial completion — don’t stop mid-process.
////   - Never claim “I can’t do this” if the task doesn’t *actually* require a tool.
////
////6. **Memory & Context**
////   - Always consider past conversation memory when responding.
////   - Maintain consistency across turns (e.g. project context, file paths, class names).
////
////---
////
////### 🧠 SMART STRATEGY EXAMPLES
////
////#### ✅ Example 1: Simple Question
////**User:** “What is an interface in Java?”
////→ Do not use any tool. Respond directly.
////
////#### ✅ Example 2: Locate a File
////**User:** “Open the main controller file.”
////→ Use `listAllFilePaths` to find it, then `fileRetrievalTool` to show content.
////
////#### ✅ Example 3: Modify a Class
////**User:** “Add a new method to UserService.java.”
////→ Use `listAllFilePaths` → `fileRetrievalTool` → edit → `writeInFile`.
////
////#### ✅ Example 4: Create a New File
////**User:** “Create a Spring Boot service class for managing user sessions.”
////→ Use `FileGenerator` to generate the full class → save using `writeInFile`.
////
////#### ✅ Example 5: Conceptual + Code Task
////**User:** “Explain how dependency injection works and show an example implementation.”
////→ Provide the explanation directly, then use `FileGenerator` only for the example file (not for the explanation).
////
////#### ✅ Example 6: Complex Multi-Step Request
////**User:** “Create a full CRUD module for Employee entity.”
////→ Step 1: Use `FileGenerator` to create multiple files (Entity, Repository, Service, Controller).
////→ Step 2: Save each via `writeInFile`.
////→ Step 3: Confirm completion.
////
////---
////
////### ⚙️ MANDATORY BEHAVIOR SUMMARY
////- Use tools only when essential.
////- Never request user input for data you can fetch via tools.
////- Always generate **complete** files — not snippets.
////- When creating or updating files, **save them automatically**.
////- Keep responses practical, structured, and to the point.
////- Maintain consistent context using `ChatMemory`.
////- Skip redundant tool calls or re-fetching known data.
////
////""")
                .tools(fileTools , aiTools )
                .system("""
                        rule 1 : never ask for a question or permission
                        rule 2 : always first use the ListAllFiles tool to fetch the file Paths
                        rule 3 : always use the path provided from ListAllFiles
                        rule 4 : just do it
                        rule 5 : always use the tool named FileGenerator to generat code
                        """)
                .user(query)
                .advisors(
                        new SimpleLoggerAdvisor(),

                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId("shell-conversation")
                                .build()


//                       MessageChatMemoryAdvisor.builder(chatMemory).conversationId("shell").build()
//                         , new QuestionAnswerAdvisor(vectorStore)
                )
                .call()
                .content();



    }


}
