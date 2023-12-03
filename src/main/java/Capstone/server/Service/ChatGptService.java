package Capstone.server.Service;

import Capstone.server.DTO.ChatGpt.ChatRequest;
import Capstone.server.DTO.ChatGpt.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatGptService {
    private RestTemplate restTemplate;

    public ChatGptService(@Qualifier("openaiRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getQuiz(StringBuilder contents, int num) {
        String model = "gpt-4-1106-preview";
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        //
        contents.append(" 를 읽고 " +
                " 이 문장들에서 얻을 수 있는 개념에 대한 문제와 답을 json형식으로 quizNum변수에 int형으로 1부터 차례대로 문제번호를 저장하고," +
                "question변수에 1개의 문제를, answer변수에 1개의 정답을 저장해서 총 ");
        contents.append(String.valueOf(num));
        contents.append("개 json형식으로 반환해줘. 이때 응답받은 내용을 바로 java의 OpjectMapper로 변환할 거니까 json내용" +
                "이외의 말들은 필요없어.");

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
