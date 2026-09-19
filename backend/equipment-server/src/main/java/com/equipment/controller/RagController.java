package com.equipment.controller;

import com.equipment.service.KnowledgeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import com.equipment.service.RagRetrievalService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import com.equipment.service.RagAnalysisService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/rag")
public class RagController {

    private final KnowledgeService knowledgeService;
    private final RagRetrievalService ragRetrievalService;
    private final RagAnalysisService ragAnalysisService;

    public RagController(
            KnowledgeService knowledgeService,
            RagRetrievalService ragRetrievalService,
            RagAnalysisService ragAnalysisService) {

        this.knowledgeService = knowledgeService;
        this.ragRetrievalService = ragRetrievalService;
        this.ragAnalysisService = ragAnalysisService;
    }

    @GetMapping("/knowledge")
    public Map<String, String> getKnowledge() {
        return knowledgeService.loadDocuments();
    }
    @GetMapping("/chunks")
    public java.util.List<String> getChunks() {
        return knowledgeService.splitDocuments();
    }
    @GetMapping("/search")
    public List<String> search(
            @RequestParam String query) {

        return ragRetrievalService.search(query, 3);
    }
    @PostMapping("/analyze")
    public Map<String, Object> analyze(
            @RequestParam Long equipmentId,
            @RequestBody String description) {

        return ragAnalysisService.analyze(equipmentId, description);
    }
}