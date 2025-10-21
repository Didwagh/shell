package com.project.ai.shell.repo;

import com.project.ai.shell.model.TrackedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackedFileRepo extends JpaRepository<TrackedFile, String> {
//    Optional<TrackedFile> findByFilePath(String filePath);
Optional<TrackedFile> findByFilePath(String filePath);

}
