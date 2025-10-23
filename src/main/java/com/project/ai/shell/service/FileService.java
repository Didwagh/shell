package com.project.ai.shell.service;

import com.project.ai.shell.records.FileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileService {

    private final Path projectRoot;

    public FileService() {
        // Get the project root directory
//        this.projectRoot = Paths.get(System.getProperty("user.dir"));
        this.projectRoot = Paths.get(System.getProperty("project.path", System.getProperty("user.dir")));
        log.info("Project root set to: {}", projectRoot);
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


    //getiing all files with info
    public List<FileInfo> listAllFilesWithTime() throws IOException {
        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(this::shouldIncludeFile)
                    .map(path -> new FileInfo(
                            projectRoot.relativize(path).toString(),
                            getLastModifiedTime(path)
                    ))
                    .sorted(Comparator.comparing(FileInfo::relativePath))
                    .collect(Collectors.toList());
        }
    }

    private long getLastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
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
     * Get file information
     */
    public String getFileInfo(String relativePath) throws IOException {
        Path filePath = projectRoot.resolve(relativePath);

        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist: " + relativePath);
        }

        long size = Files.size(filePath);
        String sizeStr = formatFileSize(size);

        return String.format("File: %s\nSize: %s (%d bytes)\nReadable: %s\nWritable: %s",
                relativePath,
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
     * Filter out files we don't want to include (like target/, .git/, etc.)
     */
    private boolean shouldIncludeFile(Path path) {
        String pathStr = path.toString();
        return !pathStr.contains("target" + System.getProperty("file.separator")) &&
                !pathStr.contains(".git" + System.getProperty("file.separator")) &&
                !pathStr.contains(".idea" + System.getProperty("file.separator")) &&
                !pathStr.contains("node_modules" + System.getProperty("file.separator")) &&
                !pathStr.contains(".mvn" + System.getProperty("file.separator")) &&
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


    /**
     * Write content to a file (creates or overwrites)
     */
    public void writeFile(String relativePath, String content) throws IOException {
        Path filePath = projectRoot.resolve(relativePath);

        // Security check: ensure the file is within project root
        if (!filePath.normalize().startsWith(projectRoot.normalize())) {
            throw new IOException("Access denied: File is outside project directory");
        }

        // Create parent directories if they don't exist
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        // Write the content to the file
        Files.writeString(filePath, content);
        log.info("File written successfully: {}", relativePath);
    }

    public Path getProjectRoot() {
        return projectRoot;
    }
}