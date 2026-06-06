package tw.edu.fju.miniclinic.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "service", "miniclinic"
        );
    }

    @GetMapping("/api/about")
    public Map<String, String> about() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("student_id", "414570198");
        info.put("student_name", "程瑞堂");
        info.put("project", "MiniClinic");
        info.put("version", "0.1.0");
        info.put("chapter", "Ch09-A");
        return info;
    }
}