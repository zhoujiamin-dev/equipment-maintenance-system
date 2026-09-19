package com.equipment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import com.equipment.entity.AiAnalysis;
import com.equipment.service.AiAnalysisService;
@Service
public class RagAnalysisService {

    private final RagRetrievalService ragRetrievalService;
    private final AiAnalysisService aiAnalysisService;

    @Value("${deepseek.api-key}")
    private String apiKey;

    public RagAnalysisService(
            RagRetrievalService ragRetrievalService,
            AiAnalysisService aiAnalysisService) {

        this.ragRetrievalService = ragRetrievalService;
        this.aiAnalysisService = aiAnalysisService;
    }

    public Map<String, Object> analyze(Long equipmentId, String description) {

        // 1. 先从知识库检索最相关的 3 个片段
        List<String> retrievedChunks =
                ragRetrievalService.search(description, 3);

        if (retrievedChunks.isEmpty()) {
            Map<String, Object> result = new HashMap<>();
            result.put("analysisResult", "没有检索到相关维修资料，请补充故障描述。");
            result.put("sources", Collections.emptyList());
            return result;
        }

        // 2. 把检索到的资料拼成参考内容
        String knowledge = String.join("\n\n", retrievedChunks);

        // 3. 调用 DeepSeek
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.deepseek.com/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put(
                "content",
                "你是一名企业设备维修辅助助手。" +
                        "请优先根据提供的维修资料回答，不要编造资料中没有明确支持的内容。" +
                        "请使用纯文本回答，不要使用Markdown。" +
                        "回答分为三个部分：" +
                        "一、可能原因；二、检查建议；三、注意事项。" +
                        "如果资料不足，请明确说明资料不足。"
        );

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put(
                "content",
                "用户故障描述：\n" +
                        description +
                        "\n\n维修知识库检索结果：\n" +
                        knowledge
        );

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-v4-flash");
        body.put(
                "messages",
                Arrays.asList(systemMessage, userMessage)
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        url,
                        request,
                        Map.class
                );

        Map responseBody = response.getBody();

        List choices = (List) responseBody.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        String analysisResult =
                (String) message.get("content");
        String sourcesText = String.join("\n\n", retrievedChunks);

        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setEquipmentId(equipmentId);
        aiAnalysis.setDescription(description);
        aiAnalysis.setAnalysisResult(analysisResult);
        aiAnalysis.setSources(sourcesText);

        aiAnalysisService.save(aiAnalysis);

        // 4. 同时把答案和引用资料返回
        Map<String, Object> result = new HashMap<>();
        result.put("analysisId", aiAnalysis.getId());
        result.put("analysisResult", analysisResult);
        result.put("sources", retrievedChunks);

        return result;
    }
}