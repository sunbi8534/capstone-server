package Capstone.server.Controller;

import Capstone.server.Service.ChatGptService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    ChatGptService chatService;

    public TestController(ChatGptService chatService) {
        this.chatService = chatService;
    }

    @ResponseBody
    @GetMapping("/test")
    public void get(@RequestParam String qa) {

    }
}
