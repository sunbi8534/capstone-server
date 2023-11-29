package Capstone.server.DTO.ChatGpt;

import lombok.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Configuration
public class OpenAIRestTemplateConfig {
    @Bean
    @Qualifier("openaiRestTemplate")
    public RestTemplate openaiRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        String s = "";
        try{
            ClassPathResource resource = new ClassPathResource("k.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            s = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String openaiApiKey = s;
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + openaiApiKey);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
