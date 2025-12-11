package com.project.newshell.Repository;

import com.project.newshell.entities.TrackedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackedFileRepo extends JpaRepository<TrackedFile, Integer> {
    Optional<TrackedFile> findByFilePath(String filePath);
    // test update
}
