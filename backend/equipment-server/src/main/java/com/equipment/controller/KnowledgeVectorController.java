package com.equipment.controller;

import com.equipment.service.KnowledgeVectorService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/knowledge-vector")
public class KnowledgeVectorController {

    private final KnowledgeVectorService knowledgeVectorService;

    public KnowledgeVectorController(
            KnowledgeVectorService knowledgeVectorService) {
        this.knowledgeVectorService = knowledgeVectorService;
    }

    @PostMapping("/rebuild")
    public Map<String, Object> rebuild() {

        int count = knowledgeVectorService.rebuildKnowledgeVectors();

        Map<String, Object> result = new HashMap<>();
        result.put("message", "知识向量重建成功");
        result.put("insertedCount", count);

        return result;
    }
}