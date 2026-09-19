package com.equipment.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.equipment.entity.AiAnalysis;
import com.equipment.service.AiAnalysisService;

import java.util.*;
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:5173")
public class AiAnalysisController {
    private final AiAnalysisService aiAnalysisService;

    public AiAnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }
    @Value("${deepseek.api-key}")
    private String apiKey;
    @PostMapping("/analyze")
    public Map<String, Object> analyze(
            @RequestParam Long equipmentId,
            @RequestBody String description) {

        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.deepseek.com/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put(
                "content",
                "你是一名企业设备维修辅助助手。请根据用户提供的设备故障现象进行分析。" +
                        "请严格使用纯文本回答，不要使用Markdown，不要出现**、#、-等格式符号。" +
                        "回答固定分为三个部分：" +
                        "一、可能原因；二、检查建议；三、注意事项。" +
                        "每一部分使用1、2、3这样的编号，内容简洁清楚。" +
                        "注意事项中提醒用户：分析结果仅供辅助参考，最终应以专业人员现场检查结果为准。"
        );

        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", description);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-v4-flash");
        body.put("messages", Arrays.asList(systemMessage, userMessage));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, request, Map.class);

        Map responseBody = response.getBody();

        List choices = (List) responseBody.get("choices");
        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        String result = (String) message.get("content");

        String cleanResult = result.replace("**", "");

        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setEquipmentId(equipmentId);
        aiAnalysis.setDescription(description);
        aiAnalysis.setAnalysisResult(cleanResult);

        aiAnalysisService.save(aiAnalysis);

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("analysisId", aiAnalysis.getId());
        resultData.put("analysisResult", cleanResult);

        return resultData;
    }
    @PostMapping("/{analysisId}/create-work-order")
    public Map<String, Object> createWorkOrder(
            @PathVariable Long analysisId) {

        Long workOrderId = aiAnalysisService.createWorkOrder(analysisId);

        Map<String, Object> result = new HashMap<>();
        result.put("workOrderId", workOrderId);
        result.put("message", "维修工单创建成功");

        return result;
    }
}