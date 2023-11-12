package Capstone.server.DTO.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QuizDto {
    int quizNum;
    String question;
    String answer;
}
