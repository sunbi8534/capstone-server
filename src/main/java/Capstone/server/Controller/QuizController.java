package Capstone.server.Controller;

import Capstone.server.DTO.Quiz.QuizDto;
import Capstone.server.DTO.Quiz.QuizInfoDto;
import Capstone.server.DTO.Quiz.QuizMakeDto;
import Capstone.server.Service.QuizService;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class QuizController {
    QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @ResponseBody
    @GetMapping("/user/quiz")
    public List<QuizInfoDto> getMyQuiz(@RequestParam String nickname, @RequestParam String course) {
        return quizService.getMyQuiz(nickname,course);
    }

    @ResponseBody
    @GetMapping("/user/quiz/list")
    public List<String> getMyQuizFolderName(@RequestParam String nickname) {
        return quizService.getMyQuizFolderName(nickname);
    }

    @ResponseBody
    @PostMapping("/user/quiz/makeFolder")
    public void makeQuizFolder(@RequestBody QuizMakeDto quiz) {
        quizService.makeQuizFolder(quiz);
    }

    @ResponseBody
    @GetMapping("/user/quiz/solve")
    public List<QuizDto> quiz(@RequestParam int quizKey) {
        return quizService.quiz(quizKey);
    }

    @ResponseBody
    @PostMapping("/user/makeQuiz/{quizKey}")
    public void makeQuiz(@PathVariable int quizKey, @RequestBody List<QuizDto> quiz) {
        quizService.makeQuiz(quizKey, quiz);
    }

    @ResponseBody
    @PostMapping("/user/quiz/delete/{quizKey}")
    public void deleteQuizFolder(@PathVariable int quizKey) {
        quizService.deleteQuizFolder(quizKey);
    }
}
