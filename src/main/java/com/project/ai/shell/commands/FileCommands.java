package com.project.ai.shell.commands;

import com.project.ai.shell.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.io.IOException;
import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class FileCommands {

    private final FileService fileService;

    @ShellMethod(key = "list-files", value = "List all files in the project")
    public String listFiles() {
        try {
            List<String> files = fileService.listAllFiles();

            if (files.isEmpty()) {
                return "No files found.";
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Found %d files:\n", files.size()));
            result.append("─".repeat(50)).append("\n");

            for (String file : files) {
                result.append("  ").append(file).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            return "Error listing files: " + e.getMessage();
        }
    }

    @ShellMethod(key = "list-java", value = "List all Java files in the project")
    public String listJavaFiles() {
        try {
            List<String> files = fileService.listJavaFiles();

            if (files.isEmpty()) {
                return "No Java files found.";
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Found %d Java files:\n", files.size()));
            result.append("─".repeat(50)).append("\n");

            for (String file : files) {
                result.append("  ").append(file).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            return "Error listing Java files: " + e.getMessage();
        }
    }

    @ShellMethod(key = "list-dir", value = "List files in a specific directory")
    public String listDirectory(
            @ShellOption(help = "Directory path relative to project root") String directory) {
        try {
            List<String> files = fileService.listFilesInDirectory(directory);

            if (files.isEmpty()) {
                return "No files found in directory: " + directory;
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Found %d files in '%s':\n", files.size(), directory));
            result.append("─".repeat(50)).append("\n");

            for (String file : files) {
                result.append("  ").append(file).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            return "Error listing directory: " + e.getMessage();
        }
    }

    @ShellMethod(key = "read-file", value = "Read the content of a file")
    public String readFile(
            @ShellOption(help = "File path relative to project root") String filePath) {
        try {
            String content = fileService.readFile(filePath);

            StringBuilder result = new StringBuilder();
            result.append("File: ").append(filePath).append("\n");
            result.append("─".repeat(50)).append("\n");
            result.append(content);
            result.append("\n").append("─".repeat(50));

            return result.toString();
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
    }

    @ShellMethod(key = "file-info", value = "Get information about a file")
    public String fileInfo(
            @ShellOption(help = "File path relative to project root") String filePath) {
        try {
            return fileService.getFileInfo(filePath);
        } catch (IOException e) {
            return "Error getting file info: " + e.getMessage();
        }
    }

    @ShellMethod(key = "search-files", value = "Search for files by name pattern")
    public String searchFiles(
            @ShellOption(help = "Search pattern (use * for wildcard)") String pattern) {
        try {
            List<String> files = fileService.searchFiles(pattern);

            if (files.isEmpty()) {
                return "No files found matching pattern: " + pattern;
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Found %d files matching '%s':\n", files.size(), pattern));
            result.append("─".repeat(50)).append("\n");

            for (String file : files) {
                result.append("  ").append(file).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            return "Error searching files: " + e.getMessage();
        }
    }

    @ShellMethod(key = "project-root", value = "Show the project root directory")
    public String projectRoot() {
        return "Project root: " + fileService.getProjectRoot().toAbsolutePath();
    }

    @ShellMethod(key = "switch-project", value = "Switch to a different project directory")
    public String switchProject(
            @ShellOption(help = "Absolute path to the project directory") String projectPath) {
        return fileService.switchProject(projectPath);
    }

    @ShellMethod(key = "reset-project", value = "Reset to the default project directory")
    public String resetProject() {
        return fileService.resetToDefault();
    }

    @ShellMethod(key = "project-structure", value = "Show project structure summary")
    public String projectStructure() {
        try {
            return fileService.getProjectStructure();
        } catch (IOException e) {
            return "Error getting project structure: " + e.getMessage();
        }
    }

    @ShellMethod(key = "list-by-ext", value = "List files by extension")
    public String listByExtension(
            @ShellOption(help = "File extension (e.g., java, xml, yml)") String extension) {
        try {
            List<String> files = fileService.listFilesByExtension(extension);

            if (files.isEmpty()) {
                return "No files found with extension: " + extension;
            }

            StringBuilder result = new StringBuilder();
            result.append(String.format("Found %d .%s files:\n", files.size(), extension));
            result.append("─".repeat(50)).append("\n");

            for (String file : files) {
                result.append("  ").append(file).append("\n");
            }

            return result.toString();
        } catch (IOException e) {
            return "Error listing files: " + e.getMessage();
        }
    }
}