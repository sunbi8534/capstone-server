package Capstone.server.Controller;

import Capstone.server.DTO.Qa.*;
import Capstone.server.Service.QaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class QaController {
    QaService qaService;

    QaController(QaService qaService) {
        this.qaService = qaService;
    }
    @ResponseBody
    @PostMapping("/qa")  //D
    public int enrollQuestion(@RequestBody QaDto question) {
        return qaService.enrollQa(question);
    }

    @ResponseBody
    @PostMapping("/user/qa/delete")  //D
    public String deleteQa(@RequestParam int qaKey) {
        return qaService.deleteQa(qaKey);
    }

    @ResponseBody
    @GetMapping("/qa")   //D
    public List<QaBriefDto> getQaList(@RequestParam String nickname) {
        return qaService.getQaList(nickname);
    }

    @ResponseBody
    @GetMapping("/qa/pick") //D
    public List<QaMsgDto> getQuestion(@RequestParam int qaKey, @RequestParam String nickname) {
        return qaService.getQuestion(qaKey, nickname);
    }

    @ResponseBody
    @GetMapping("/qa/isReview")
    public boolean isReview(@RequestParam int qaKey) {
        return qaService.isReview(qaKey);
    }

    @ResponseBody
    @PostMapping("/qa/giveup")  //D
    public void qaGiveUp(@RequestParam int qaKey, @RequestParam String nickname) {
        qaService.qaGiveUp(qaKey, nickname);
    }

    @ResponseBody
    @PostMapping("/qa/answer/{qaKey}") //D..
    public void qaSolve(@PathVariable int qaKey) {
        qaService.qaSolve(qaKey);
    }

    @ResponseBody
    @PostMapping("/qa/chat/{qaKey}") //D
    public void sendQaMsg(@PathVariable int qaKey, @RequestBody QaSendMsgDto msg) {
        qaService.sendQaMsg(qaKey, msg);
    }

    @ResponseBody
    @GetMapping("/qa/chat")  //D
    public List<QaMsgDto> getQaMsgs(@RequestParam int qaKey, @RequestParam String nickname) {
        return qaService.getQaMsgs(qaKey, nickname);
    }

    @ResponseBody
    @PostMapping("/qa/finish") //Dd
    public void qaFinish(@RequestParam int qaKey, @RequestParam int review) {
        qaService.qaFinish(qaKey, review);
    }

    @ResponseBody
    @GetMapping("/user/qa/ask") //D
    public List<QaAskListDto> getAskList(@RequestParam String nickname) {
        return qaService.getQaAskList(nickname);
    }

    @ResponseBody
    @GetMapping("/user/qa/answer") //D
    public List<QaListDto> getAnswerList(@RequestParam String nickname) {
        return qaService.getQaAnswerList(nickname);
    }

    @ResponseBody
    @GetMapping("/user/qa") //D
    public List<QaMsgDto> getQa(@RequestParam int qaKey) {
        return qaService.getQa(qaKey);
    }

    @ResponseBody
    @GetMapping("/qa/time")
    public long getTime(@RequestParam int qaKey) { return qaService.getTime(qaKey); }

    @ResponseBody
    @GetMapping("/qa/status")
    public String getQaStatus(@RequestParam int qaKey) {
        return qaService.getQaStatus(qaKey);
    }
}
