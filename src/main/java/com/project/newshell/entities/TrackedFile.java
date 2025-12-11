package com.project.newshell.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter

@Table(name = "tracked_files")
public class TrackedFile {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false, unique = true, length = 500)
    private String filePath;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime modifiedAt = LocalDateTime.now();

    public TrackedFile() {

    }

    @PrePersist
    @PreUpdate
    private void updateTrackingTime() {
        modifiedAt = LocalDateTime.now();
    }

    public TrackedFile(String filePath) {
        this.filePath = filePath;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "TrackedFile{" +
                "filePath='" + filePath + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }

}
