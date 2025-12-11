package com.project.newshell.services;

import com.project.newshell.Repository.TrackedFileRepo;
import com.project.newshell.entities.TrackedFile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
public class TrackedFileService {

    private FileService fileService;
    private TrackedFileRepo trackedFileRepo;
    private VectorDBService vectorDBService;


    public TrackedFileService(FileService fileService, TrackedFileRepo trackedFileRepo, VectorDBService vectorDBService) {
        this.fileService = fileService;
        this.trackedFileRepo = trackedFileRepo;
        this.vectorDBService = vectorDBService;
    }

//    public List<String> saveFiles() throws IOException {
////        find all files paths
//        List<String> strings = fileService.listAllFiles();
//        for (String path : strings) {
//            Optional<TrackedFile> byFilePath = trackedFileRepo.findByFilePath(path);
//
//            if (byFilePath.isPresent()) {
//                TrackedFile trackedFile = byFilePath.get();
//
//                long lastModifiedMillis = fileService.getLastModifiedTime(Path.of(path));
//
//                LocalDateTime dbTime = trackedFile.getModifiedAt();
//                LocalDateTime fileSystemTime = Instant.ofEpochMilli(lastModifiedMillis)
//                        .atZone(ZoneId.systemDefault())
//                        .toLocalDateTime();
//
//                if (fileSystemTime.isAfter(dbTime)) {
//                    System.out.println("File changed — updating timestamp");
//                    trackedFile.setModifiedAt(LocalDateTime.now());
//                    trackedFileRepo.save(trackedFile);
//                    //save document in vector db
//
//                    vectorDBService.SaveVectorDocument(trackedFile.getFilePath());
//                }
//
//
//            }else{
//                System.out.println("saving file " + path);
//                 trackedFileRepo.save(new TrackedFile(path));
//
//            }
//        }
//
//        return strings;
//    }

public List<String> saveFiles() throws IOException {

    List<String> filePaths = fileService.listAllFiles();
    System.out.println(filePaths);
    for (String path : filePaths) {

        Optional<TrackedFile> existing = trackedFileRepo.findByFilePath(path);

        long lastModifiedMillis = fileService.getLastModifiedTime(Path.of(path));
        LocalDateTime fileSystemTime = Instant.ofEpochMilli(lastModifiedMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // CASE A: File is NEW
        if (existing.isEmpty()) {
            System.out.println("New file detected: " + path);

            TrackedFile newFile = new TrackedFile(path);
            newFile.setCreatedAt(LocalDateTime.now());
            newFile.setModifiedAt(fileSystemTime);

            trackedFileRepo.save(newFile);

            // Add embeddings
            vectorDBService.saveOrUpdateVectorDocument(path);

            continue;
        }

        // CASE B: File EXISTS
        TrackedFile trackedFile = existing.get();
        LocalDateTime dbTime = trackedFile.getModifiedAt();

        if (fileSystemTime.isAfter(dbTime)) {
            System.out.println("File changed — updating");

            trackedFile.setModifiedAt(fileSystemTime);
            trackedFileRepo.save(trackedFile);

            // Update embeddings in vector store
            vectorDBService.saveOrUpdateVectorDocument(path);
        }
    }

    return filePaths;
}

}
