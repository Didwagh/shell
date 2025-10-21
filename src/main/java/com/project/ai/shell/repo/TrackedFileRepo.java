package com.project.ai.shell.repo;

import com.project.ai.shell.model.TrackedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackedFileRepo extends JpaRepository<TrackedFile, String> {

}
