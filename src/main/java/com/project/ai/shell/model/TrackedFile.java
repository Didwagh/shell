package com.project.ai.shell.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tracked_files")
public class TrackedFile {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relative path of the file (e.g. "src/main/java/com/example/MyService.java")
     */
    @Column(nullable = false, unique = true, length = 500)
    private String filePath;

    /**
     * When the file was last modified on disk
     */
    @Column(nullable = false)
    private LocalDateTime lastModifiedTime;

    /**
     * When the record was created or last updated in DB
     */
    @Column(nullable = false)
    private LocalDateTime trackedAt;

    @PrePersist
    @PreUpdate
    private void updateTrackingTime() {
        trackedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TrackedFile{" +
                "filePath='" + filePath + '\'' +
                ", lastModifiedTime=" + lastModifiedTime +
                ", trackedAt=" + trackedAt +
                '}';
    }
}
