package com.equipment.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class KnowledgeService {

    private static final List<String> FILE_NAMES = List.of(
            "motor_fault.txt",
            "bearing_fault.txt",
            "pump_fault.txt"
    );

    public Map<String, String> loadDocuments() {
        Map<String, String> documents = new LinkedHashMap<>();

        for (String fileName : FILE_NAMES) {
            ClassPathResource resource =
                    new ClassPathResource("knowledge/" + fileName);

            try (InputStream inputStream = resource.getInputStream()) {

                String content = new String(
                        inputStream.readAllBytes(),
                        StandardCharsets.UTF_8
                );

                documents.put(fileName, content);

            } catch (IOException e) {
                throw new RuntimeException(
                        "读取知识库文件失败：" + fileName,
                        e
                );
            }
        }

        return documents;
    }
    public List<String> splitDocuments() {
        Map<String, String> documents = loadDocuments();
        List<String> chunks = new java.util.ArrayList<>();

        for (Map.Entry<String, String> entry : documents.entrySet()) {
            String fileName = entry.getKey();
            String content = entry.getValue();

            String[] parts = content.split("\\r?\\n\\s*\\r?\\n");

            for (String part : parts) {
                String text = part.trim();

                if (!text.isEmpty()) {
                    chunks.add(
                            "来源：" + fileName + "\n" + text
                    );
                }
            }
        }

        return chunks;
    }
}