package emsi.codegenerator.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "prompt-service", url = "http://localhost:8081") // Replace with service name if using Eureka
public interface PromptServiceClient {

    @PostMapping("/api/chat")
    String generateCode(@RequestBody String userInput);
}
