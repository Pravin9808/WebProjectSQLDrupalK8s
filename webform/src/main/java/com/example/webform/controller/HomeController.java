package com.example.webform.controller;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final RestTemplate restTemplate;

    public HomeController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    @GetMapping("/home")
    public String home(Model model) {
        String drupalApi = "http://drupal/jsonapi/node/article";

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(drupalApi, Map.class);
            Map<String, Object> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("data")) {
                List<Map<String, Object>> articles = (List<Map<String, Object>>) responseBody.get("data");

                if (!articles.isEmpty()) {
                    Map<String, Object> article = articles.get(0);
                    Map<String, Object> attributes = (Map<String, Object>) article.get("attributes");

                    model.addAttribute("title", attributes.get("title"));
                    model.addAttribute("body", ((Map) attributes.get("body")).get("value"));
                }
            }
        } catch (Exception e) {
            model.addAttribute("title", "Error");
            model.addAttribute("body", "Could not load content from Drupal.");
        }

        return "home";
    }
}
