package com.project.ai.shell.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileService {

    private Path projectRoot;
    private final Path defaultProjectRoot;

    public FileService() {
        // Get the project root directory with fallback
        this.projectRoot = Paths.get(System.getProperty("project.path", System.getProperty("user.dir")));
        this.defaultProjectRoot = this.projectRoot;
        log.info("Project root set to: {}", projectRoot.toAbsolutePath());
    }

    /**
     * Switch to a different project directory
     */
    public String switchProject(String newPath) {
        Path newProjectRoot = Paths.get(newPath);

        if (!Files.exists(newProjectRoot)) {
            return "Error: Directory does not exist: " + newPath;
        }

        if (!Files.isDirectory(newProjectRoot)) {
            return "Error: Path is not a directory: " + newPath;
        }

        this.projectRoot = newProjectRoot.toAbsolutePath();
        log.info("Switched project root to: {}", projectRoot);

        return "Successfully switched to project: " + projectRoot;
    }

    /**
     * Reset to the default project directory
     */
    public String resetToDefault() {
        this.projectRoot = this.defaultProjectRoot;
        log.info("Reset project root to: {}", projectRoot);
        return "Reset to default project: " + projectRoot;
    }

    /**
     * Get current project root
     */
    public Path getProjectRoot() {
        return projectRoot;
    }

    /**
     * List all files in the project, excluding common directories like target, .git, etc.
     */
    public List<String> listAllFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .map(projectRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * List files in a specific directory
     */
    public List<String> listFilesInDirectory(String directory) throws IOException {
        Path dirPath = projectRoot.resolve(directory);

        if (!Files.exists(dirPath)) {
            throw new IOException("Directory does not exist: " + directory);
        }

        if (!Files.isDirectory(dirPath)) {
            throw new IOException("Path is not a directory: " + directory);
        }

        try (Stream<Path> paths = Files.walk(dirPath)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .map(projectRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * List only Java files
     */
    public List<String> listJavaFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .filter(this::shouldIncludeFile)
                    .map(projectRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * List files by extension
     */
    public List<String> listFilesByExtension(String extension) throws IOException {
        String ext = extension.startsWith(".") ? extension : "." + extension;

        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(ext))
                    .filter(this::shouldIncludeFile)
                    .map(projectRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Read the content of a file
     */
    public String readFile(String relativePath) throws IOException {
        Path filePath = projectRoot.resolve(relativePath);

        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist: " + relativePath);
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("Path is not a file: " + relativePath);
        }

        // Security check: ensure the file is within project root
        if (!filePath.normalize().startsWith(projectRoot.normalize())) {
            throw new IOException("Access denied: File is outside project directory");
        }

        return Files.readString(filePath);
    }

    /**
     * Read a file from an absolute path (useful for cross-project operations)
     */
    public String readFileAbsolute(String absolutePath) throws IOException {
        Path filePath = Paths.get(absolutePath);

        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist: " + absolutePath);
        }

        if (!Files.isRegularFile(filePath)) {
            throw new IOException("Path is not a file: " + absolutePath);
        }

        return Files.readString(filePath);
    }

    /**
     * Get file information
     */
    public String getFileInfo(String relativePath) throws IOException {
        Path filePath = projectRoot.resolve(relativePath);

        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist: " + relativePath);
        }

        long size = Files.size(filePath);
        String sizeStr = formatFileSize(size);

        return String.format("File: %s\nFull path: %s\nSize: %s (%d bytes)\nReadable: %s\nWritable: %s",
                relativePath,
                filePath.toAbsolutePath(),
                sizeStr,
                size,
                Files.isReadable(filePath),
                Files.isWritable(filePath));
    }

    /**
     * Search for files by name pattern
     */
    public List<String> searchFiles(String pattern) throws IOException {
        String regex = pattern.replace("*", ".*").replace("?", ".");

        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .filter(p -> p.getFileName().toString().matches(regex))
                    .map(projectRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    /**
     * Get project structure summary
     */
    public String getProjectStructure() throws IOException {
        StringBuilder structure = new StringBuilder();
        structure.append("Project: ").append(projectRoot.toAbsolutePath()).append("\n");
        structure.append("─".repeat(50)).append("\n");

        // Count file types
        var allFiles = listAllFiles();
        long javaFiles = allFiles.stream().filter(f -> f.endsWith(".java")).count();
        long xmlFiles = allFiles.stream().filter(f -> f.endsWith(".xml")).count();
        long yamlFiles = allFiles.stream().filter(f -> f.endsWith(".yml") || f.endsWith(".yaml")).count();
        long propsFiles = allFiles.stream().filter(f -> f.endsWith(".properties")).count();

        structure.append("Total files: ").append(allFiles.size()).append("\n");
        structure.append("Java files: ").append(javaFiles).append("\n");
        structure.append("XML files: ").append(xmlFiles).append("\n");
        structure.append("YAML files: ").append(yamlFiles).append("\n");
        structure.append("Properties files: ").append(propsFiles).append("\n");

        return structure.toString();
    }

    /**
     * Filter out files we don't want to include (like target/, .git/, etc.)
     */
    private boolean shouldIncludeFile(Path path) {
        String pathStr = path.toString();
        return !pathStr.contains("target" + System.getProperty("file.separator")) &&
                !pathStr.contains(".git" + System.getProperty("file.separator")) &&
                !pathStr.contains(".idea" + System.getProperty("file.separator")) &&
                !pathStr.contains("node_modules" + System.getProperty("file.separator")) &&
                !pathStr.contains(".mvn" + System.getProperty("file.separator")) &&
                !pathStr.contains("build" + System.getProperty("file.separator")) &&
                !pathStr.contains("dist" + System.getProperty("file.separator")) &&
                !pathStr.endsWith(".class");
    }

    /**
     * Format file size in human-readable format
     */
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }
}