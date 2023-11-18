package Capstone.server.Service;

import Capstone.server.DTO.ChatGpt.ChatRequest;
import Capstone.server.DTO.ChatGpt.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {
    private RestTemplate restTemplate;

    public ChatGptService(@Qualifier("openaiRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getQuiz(StringBuilder contents, boolean type) {
        String model = "gpt-3.5-turbo-16k";
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        if(type) {
            contents.append(" 를 읽고 다른 말 필요없이 이 문장들에 대한 4지선다 문제와 답을 json형식으로 문제번호를 의미하는 quizNum," +
                    "문제를 의미하는 question, 답을 의미하는 answer 이 세 개의 변수 안에 값을 담아서 총 10개 반환해줘.");
        } else {
            contents.append(" 를 읽고 다른 말 필요없이 이 문장들에서 얻을 수 있는 개념에 대한 문제와 답을 json형식으로 문제번호를 의미하는 quizNum," +
                    "문제를 의미하는 question, 답을 의미하는 answer 이 세 개의 변수 안에 값을 담아서 총 10개 반환해줘.");
        }

        // create a request
        ChatRequest request = new ChatRequest(model, contents.toString());

        // call the API
        ChatResponse response = restTemplate.postForObject(apiUrl, request, ChatResponse.class);

        if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
            return "No response";
        }

        // return the first response
        return response.getChoices().get(0).getMessage().getContent();
    }
}
