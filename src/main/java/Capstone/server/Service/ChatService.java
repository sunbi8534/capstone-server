package Capstone.server.Service;

import Capstone.server.DTO.Chat.*;
import Capstone.server.Repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ChatService {
    ProfileService profileService;
    ChatRepository chatRepository;
    public ChatService(ProfileService profileService,
                       ChatRepository chatRepository) {
        this.profileService = profileService;
        this.chatRepository = chatRepository;
    }

    public List<MsgDto> getAllMsg(String nickname1, String nickname2) {
        List<MsgDto> msgDtos = new ArrayList<>();

        if(!chatRepository.checkIsExistChat(nickname1, nickname2))
            chatRepository.makeNewChat(nickname1, nickname2);

        List<Msg> msgs = chatRepository.getAllMsgs(nickname1, nickname2);
        if(msgs == null)
            return msgDtos;

        for(Msg msg : msgs) {
            MsgDto msgDto = new MsgDto(msg.getNickname(), msg.getType(), msg.getMsg(), msg.getImage(), msg.getTime());
            msgDtos.add(msgDto);
        }

        Msg lastMsg = msgs.get(msgs.size() - 1);
        int updateMsgNum = lastMsg.getMsg_num();
        chatRepository.updateMsgNum(nickname1, nickname2, updateMsgNum);
        chatRepository.setChatIsOn(nickname1, nickname2, true);
        return msgDtos;
    }

    public void processSendMsg(SendMsgDto sendMsg) {
        chatRepository.updateSendMsg(sendMsg);
        if(!chatRepository.checkUserIsOn(sendMsg.getReceiver(), sendMsg.getSender()))
            chatRepository.sendAlarm(sendMsg);
    }

    public List<MsgDto> getUnreadMsg(String nickname1, String nickname2) {
        List<MsgDto> unreadMsg = new ArrayList<>();
        List<Msg> msgs = chatRepository.getUnreadMsg(nickname1, nickname2);
        if(msgs.isEmpty())
            return unreadMsg;

        for(Msg msg : msgs) {
            MsgDto msgDto = new MsgDto(msg.getNickname(), msg.getType(), msg.getMsg(), msg.getImage(), msg.getTime());
            unreadMsg.add(msgDto);
        }

        Msg m = msgs.get(msgs.size() - 1);
        chatRepository.updateMsgNum(nickname1, nickname2, m.getMsg_num());

        return unreadMsg;
    }

    public void outChat(ChatMemberDto chatMember) {
        chatRepository.setChatIsOn(chatMember.getNickname1(), chatMember.getNickname2(), false);
    }

    public List<ChatListDto> getChatList(String nickname) {
        return chatRepository.getChatList(nickname);
    }

    public void deleteChat(String nickname1, String nickname2) {
        chatRepository.deleteChat(nickname1, nickname2);
    }
}
