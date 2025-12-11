package com.project.newshell.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FileService {
    private static final Logger log = LoggerFactory.getLogger(FileService.class);

     public Path projectRoot;

    public FileService() {
        this.projectRoot = Paths.get(System.getProperty("project.path", System.getProperty("user.dir")));
        log.info("Project root set to: {}", projectRoot);
    }

    public List<String> listAllFiles() throws IOException {
        try (Stream<Path> paths = Files.walk(projectRoot)) {
            return paths
                    .filter(Files::isRegularFile)
//                    .filter(this::shouldIncludeFile)
                    .map(projectRoot::relativize)
                    .map(Path::toString)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public long getLastModifiedTime(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException e) {
            return 0L;
        }
    }

    public String getContent(String path) throws IOException {
        return Files.readString(Path.of(path), StandardCharsets.UTF_8);
    };


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


}
