package com.equipment.service;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.*;

@Service
public class EmbeddingService {

    @Value("${embedding.url}")
    private String embeddingUrl;

    public List<List<Double>> embed(List<String> texts) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("texts", texts);

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(
                        embeddingUrl,
                        request,
                        Map.class
                );

        Map responseBody = response.getBody();

        if (responseBody == null
                || responseBody.get("vectors") == null) {
            throw new RuntimeException("Embedding 服务返回为空");
        }

        return (List<List<Double>>) responseBody.get("vectors");
    }
}