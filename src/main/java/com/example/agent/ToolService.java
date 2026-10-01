package com.example.agent;

import jakarta.annotation.PostConstruct;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.ai.bedrock.titan.BedrockTitanEmbeddingModel;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Base64;

@Service
public class ToolService {

    Directory directory;
    IndexSearcher searcher;

    @Autowired
    @Qualifier("mymodel")
    EmbeddingModel embeddingModel;

    @Value("${spring.ai.mcp.server.protocol:NOT_FOUND}")
    private String protocol;

    @PostConstruct
    public void create() throws IOException {
        System.out.println("PPPPPPP" + Paths.get(".").toString());


        IndexWriter writer =
                new IndexWriter(
                        FSDirectory.open(Paths.get("./index")),
                        new IndexWriterConfig());

        String content =
                Files.readString(
                        Paths.get("src/main/resources/training/training.txt")
                );

        createChunks(writer, content.split("\r\n"));

        writer.close();

        DirectoryReader reader =
        DirectoryReader.open(FSDirectory.open(Paths.get("./index")));
        searcher = new IndexSearcher(reader);
    }

    private void createChunks(IndexWriter writer, String[] lines) throws IOException {
        for (String chunk : lines) {
            float[] embedding = embeddingModel.embed(chunk);

            Document doc = new Document();
            doc.add(new StoredField("chunk", chunk));
            doc.add(new KnnFloatVectorField("embedding", embedding));

            writer.addDocument(doc);
        }
    }

    @McpTool(
        name = "knowledge_search",
    description = "Searches the internal knowledge base for relevant documents)")
    public String search(String query) throws Exception {
        float[] embedding = embeddingModel.embed(query);
        Query myQuery =
                new KnnFloatVectorQuery(
                    "embedding",
                        embedding,
                    5
                );

        TopDocs topDocs = searcher.search(myQuery, 2);

        StoredFields storedFields = searcher.storedFields();
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = storedFields.document(scoreDoc.doc);

            System.out.println("Score: " + scoreDoc.score);

            return doc.get("chunk");
        }

        return "no match";
    }
}