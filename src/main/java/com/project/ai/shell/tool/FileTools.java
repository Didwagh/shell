package com.project.ai.shell.tool;

import com.project.ai.shell.service.FileService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileTools {

    private final FileService fileService;

    @Tool(name = "fileRetrievalTool" , description = "Tool which allows to retrieve any File in the project uisg the file path")
    public String readFile(@ToolParam(description = "File Path of the file you want to retrive using relativePath") String relativePath)
            throws IOException {

        return fileService.readFile(relativePath);

    }

    @Tool(name = "listAllFilePaths" , description = "Tool which allows to get File from project and get their file path")
    public List<String> getAllFilePaths() throws IOException {
        return fileService.listAllFiles();
    }


    @Tool(name = "writeInFile" , description = "Tool which allows to write in a file / edit a file ")
    public String write(@ToolParam(description = "relative path of file" ) String relativePath, @ToolParam(description = "content you want to write in file" ) String content ) throws IOException {
        fileService.writeFile(relativePath,content);
        return relativePath + File.separator + content + "successfully updated";
    }


}
