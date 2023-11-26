package Capstone.server.DTO.Quiz;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuizDto {
    int quizNum;
    String question;
    String answer;
    Boolean isSolved;
}
