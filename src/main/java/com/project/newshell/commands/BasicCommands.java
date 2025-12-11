package com.project.newshell.commands;

import com.project.newshell.services.FileService;
import com.project.newshell.services.TrackedFileService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.io.IOException;
import java.util.List;

@ShellComponent
public class BasicCommands {

    private final FileService fileService;
    private TrackedFileService trackedFileService;

    public BasicCommands(TrackedFileService trackedFileService, FileService fileService) {
        this.trackedFileService = trackedFileService;
        this.fileService = fileService;
    }

    @ShellMethod
    public List<String> command() throws IOException {
        return trackedFileService.saveFiles();
    }

    @ShellMethod
    public List<String> files() throws IOException {
        return fileService.listAllFiles();
    }
}
