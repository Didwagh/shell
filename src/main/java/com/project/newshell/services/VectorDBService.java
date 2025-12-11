package com.project.newshell.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class VectorDBService {

   private FileService fileService;
  private VectorStore vectorStore;
  private MongoTemplate mongoTemplate;

    public VectorDBService(FileService fileService, VectorStore vectorStore, MongoTemplate mongoTemplate) {
        this.fileService = fileService;
        this.vectorStore = vectorStore;
        this.mongoTemplate = mongoTemplate;
    }

    private Document createVectorDocument(String path) throws IOException {
        System.out.println("createVectorDocument");
        String codeSnippet = fileService.getContent(path);

        // The actual code text
        if (codeSnippet == null || codeSnippet.trim().length() < 5) {
            throw new IllegalArgumentException("File content too small for embeddings");
        }

        return new Document(
                codeSnippet,  // The actual code text
                Map.of("file_path", "src/main/java/Utils.java")
        );
    }

//    public void SaveVectorDocument(String path) throws IOException {
//        System.out.println("SaveVectorDocument");
//        Document vectorDocument = createVectorDocument(path);
//        vectorStore.add(List.of(vectorDocument));
//    }

    public void saveOrUpdateVectorDocument(String path) throws IOException {
//        vectorStore.delete(Map.of("file_path", path));

        Query query = new Query();
        query.addCriteria(
                Criteria.where("metadata.file_path").is(path)
        );

        mongoTemplate.remove(query, "newShell");
        // 2. Generate new document
        Document doc = createVectorDocument(path);

        // 3. Insert into vector store
        vectorStore.add(List.of(doc));
    }
}
