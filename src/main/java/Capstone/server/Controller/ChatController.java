package Capstone.server.Controller;

import Capstone.server.DTO.Chat.ChatListDto;
import Capstone.server.DTO.Chat.ChatMemberDto;
import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Chat.SendMsgDto;
import Capstone.server.Service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatController {
    ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @ResponseBody
    @GetMapping("/chat")
    public List<MsgDto> getAllMsg(@RequestParam String nickname1, @RequestParam String nickname2) {
        return chatService.getAllMsg(nickname1, nickname2);
    }

    @ResponseBody
    @PostMapping("/chat/sendMsg")
    public void sendMsg(@RequestBody SendMsgDto sendMsg) {
        chatService.processSendMsg(sendMsg);
    }

    @ResponseBody
    @GetMapping("/chat/getMsg")
    public List<MsgDto> getMsg(@RequestParam String nickname1, @RequestParam String nickname2) {
        return chatService.getUnreadMsg(nickname1, nickname2);
    }

    @ResponseBody
    @PostMapping("/chat/outChat")
    public void outChat(@RequestBody ChatMemberDto chatMember) {
        chatService.outChat(chatMember);
    }

    @ResponseBody
    @GetMapping("chat/list")
    public List<ChatListDto> getChatList(@RequestParam String nickname) {
        return chatService.getChatList(nickname);
    }
}
