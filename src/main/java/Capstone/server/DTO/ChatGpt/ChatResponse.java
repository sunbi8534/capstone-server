package Capstone.server.DTO.ChatGpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ChatResponse {
    String id;
    String object;
    long created;
    String mode;
    Usage usage;
    List<Choice> choices;


    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Usage {
        int prompt_tokens;
        int completion_tokens;
        int total_tokens;
    }

    // constructors, getters and setters

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Choice {
        Message message;
        String finish_reason;
        int index;

        // constructors, getters and setters
    }

}
