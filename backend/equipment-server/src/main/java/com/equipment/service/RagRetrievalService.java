package com.equipment.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagRetrievalService {

    private final EmbeddingService embeddingService;
    private final JdbcTemplate pgVectorJdbcTemplate;

    public RagRetrievalService(
            EmbeddingService embeddingService,
            @Qualifier("pgVectorJdbcTemplate")
            JdbcTemplate pgVectorJdbcTemplate) {

        this.embeddingService = embeddingService;
        this.pgVectorJdbcTemplate = pgVectorJdbcTemplate;
    }

    public List<String> search(String query, int topK) {

        // 1. 生成用户问题向量
        List<List<Double>> vectors =
                embeddingService.embed(List.of(query));

        if (vectors.isEmpty()) {
            throw new RuntimeException("用户问题向量生成失败");
        }

        String vectorText = toVectorText(vectors.get(0));

        // 2. 最低相似度门槛
        double minSimilarity = 0.60;

        // 3. 尝试从问题中识别设备类型
        String deviceType = detectDeviceType(query);
        if ("unsupported".equals(deviceType)) {
            return List.of();
        }
        // 4. 如果明确识别到设备类型，只搜索对应设备资料
        if (deviceType != null) {

            return pgVectorJdbcTemplate.query(
                    """
                    SELECT
                        source,
                        content,
                        1 - (embedding <=> ?::vector) AS similarity
                    FROM knowledge_chunk
                    WHERE device_type = ?
                      AND 1 - (embedding <=> ?::vector) >= ?
                    ORDER BY embedding <=> ?::vector
                    LIMIT ?
                    """,
                    (rs, rowNum) ->
                            "来源: "
                                    + rs.getString("source")
                                    + "\n"
                                    + rs.getString("content"),
                    vectorText,
                    deviceType,
                    vectorText,
                    minSimilarity,
                    vectorText,
                    topK
            );
        }

        // 5. 没有明确设备类型时，搜索整个知识库
        return pgVectorJdbcTemplate.query(
                """
                SELECT
                    source,
                    content,
                    1 - (embedding <=> ?::vector) AS similarity
                FROM knowledge_chunk
                WHERE 1 - (embedding <=> ?::vector) >= ?
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """,
                (rs, rowNum) ->
                        "来源: "
                                + rs.getString("source")
                                + "\n"
                                + rs.getString("content"),
                vectorText,
                vectorText,
                minSimilarity,
                vectorText,
                topK
        );
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

    private String detectDeviceType(String query) {

        boolean hasMotor = query.contains("电机");
        boolean hasBearing = query.contains("轴承");
        boolean hasPump = query.contains("水泵") || query.contains("泵");

        // 当前知识库暂不支持的明确设备类型
        boolean hasUnsupportedDevice =
                query.contains("空压机")
                        || query.contains("空调");

        if (hasUnsupportedDevice) {
            return "unsupported";
        }

        int matchCount = 0;

        if (hasMotor) {
            matchCount++;
        }

        if (hasBearing) {
            matchCount++;
        }

        if (hasPump) {
            matchCount++;
        }

        // 只有明确识别出一种设备时才过滤
        if (matchCount != 1) {
            return null;
        }

        if (hasMotor) {
            return "motor";
        }

        if (hasBearing) {
            return "bearing";
        }

        return "pump";
    }
}