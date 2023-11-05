package Capstone.server.Controller;

import Capstone.server.DTO.Qa.QaBriefDto;
import Capstone.server.DTO.Qa.QaDto;
import Capstone.server.DTO.Qa.QaMsgDto;
import Capstone.server.DTO.Qa.QaSendMsgDto;
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
    @PostMapping("/qa")
    public int enrollQuestion(@RequestBody QaDto question) {
        return qaService.enrollQa(question);
    }

    @ResponseBody
    @PostMapping("/user/qa/delete")
    public String deleteQa(@RequestParam int qaKey) {
        return qaService.deleteQa(qaKey);
    }

    @ResponseBody
    @GetMapping("/qa")
    public List<QaBriefDto> getQaList(@RequestParam String nickname) {
        return qaService.getQaList(nickname);
    }

    @ResponseBody
    @GetMapping("/qa/pick")
    public List<QaMsgDto> getQuestion(@RequestParam int qaKey, @RequestParam String nickname) {
        return qaService.getQuestion(qaKey, nickname);
    }

    @ResponseBody
    @PostMapping("/qa/giveup/{qaKey}")
    public void qaGiveUp(@PathVariable int qaKey, @RequestParam String nickname) {
        qaService.qaGiveUp(qaKey, nickname);
    }

    @ResponseBody
    @PostMapping("/qa/answer/{key}")
    public void qaSolve(@PathVariable int qaKey) {
        qaService.qaSolve(qaKey);
    }

    @ResponseBody
    @PostMapping("/qa/chat")
    public void sendQaMsg(@PathVariable int qaKey, @RequestBody QaSendMsgDto msg) {
        qaService.sendQaMsg(qaKey, msg);
    }

    @ResponseBody
    @GetMapping("/qa/chat")
    public List<QaMsgDto> getQaMsgs(@RequestParam int qaKey, @RequestParam String nickname) {
        return qaService.getQaMsgs(qaKey, nickname);
    }
}
