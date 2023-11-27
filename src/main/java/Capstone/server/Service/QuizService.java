package Capstone.server.Service;

import Capstone.server.DTO.Quiz.QuizDto;
import Capstone.server.DTO.Quiz.QuizInfoDto;
import Capstone.server.DTO.Quiz.QuizMakeDto;
import Capstone.server.Repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }
    public List<QuizInfoDto> getMyQuiz(String nickname, String course) {
        return quizRepository.getMyQuiz(nickname, course);
    }

    public List<String> getMyQuizFolderName(String nickname) {
        return quizRepository.getMyQuizFolderName(nickname);
    }

    public void makeQuizFolder(QuizMakeDto quiz) {
        quizRepository.makeQuizFolder(quiz);
    }

    public List<QuizDto> quiz(int quizKey) {
        return quizRepository.quiz(quizKey);
    }

    public void makeQuiz(int quizKey, List<QuizDto> quiz) {
        quizRepository.makeQuiz(quizKey, quiz);
    }

    public void deleteQuizFolder(int quizKey) {
        quizRepository.deleteQuizKey(quizKey);
    }

}
