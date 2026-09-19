package com.equipment.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class KnowledgeVectorService {

    private final KnowledgeService knowledgeService;
    private final EmbeddingService embeddingService;
    private final JdbcTemplate pgVectorJdbcTemplate;

    public KnowledgeVectorService(
            KnowledgeService knowledgeService,
            EmbeddingService embeddingService,
            @Qualifier("pgVectorJdbcTemplate")
            JdbcTemplate pgVectorJdbcTemplate) {

        this.knowledgeService = knowledgeService;
        this.embeddingService = embeddingService;
        this.pgVectorJdbcTemplate = pgVectorJdbcTemplate;
    }

    public int rebuildKnowledgeVectors() {

        // 1. 读取并切分现有知识库
        List<String> chunks = knowledgeService.splitDocuments();

        List<String> sources = new ArrayList<>();
        List<String> contents = new ArrayList<>();
        List<String> deviceTypes = new ArrayList<>();

        // 2. 把“来源”和真正的正文拆开
        for (String chunk : chunks) {

            int lineBreakIndex = chunk.indexOf("\n");

            if (lineBreakIndex < 0) {
                continue;
            }

            String sourceLine = chunk.substring(0, lineBreakIndex).trim();
            String content = chunk.substring(lineBreakIndex + 1)
                    .replace("\r", "")
                    .trim();
            String source = sourceLine
                    .replace("来源：", "")
                    .replace("来源:", "")
                    .trim();
            String deviceType = getDeviceType(source);
            sources.add(source);
            contents.add(content);
            deviceTypes.add(deviceType);
        }

        // 3. 一次性让 BGE 计算所有知识片段向量
        List<List<Double>> vectors = embeddingService.embed(contents);

        if (vectors.size() != contents.size()) {
            throw new RuntimeException("知识片段数量和向量数量不一致");
        }

        // 4. 当前属于“重建知识库”，所以先清空旧向量
        pgVectorJdbcTemplate.update("DELETE FROM knowledge_chunk");

        // 5. 保存知识片段 + 512维向量
        for (int i = 0; i < contents.size(); i++) {

            String vectorText = toVectorText(vectors.get(i));

            pgVectorJdbcTemplate.update(
                    """
                    INSERT INTO knowledge_chunk(source, content, embedding, device_type)
                    VALUES (?, ?, ?::vector, ?)
                    """,
                    sources.get(i),
                    contents.get(i),
                    vectorText,
                    deviceTypes.get(i)
            );
        }

        return contents.size();
    }

    private String getDeviceType(String source) {

        if (source.contains("motor")) {
            return "motor";
        }

        if (source.contains("bearing")) {
            return "bearing";
        }

        if (source.contains("pump")) {
            return "pump";
        }

        return "unknown";
    }

    private String toVectorText(List<Double> vector) {

        StringBuilder builder = new StringBuilder("[");

        for (int i = 0; i < vector.size(); i++) {

            if (i > 0) {
                builder.append(",");
            }

            builder.append(vector.get(i));
        }

        builder.append("]");

        return builder.toString();
    }
}