package Capstone.server.Service;

import Capstone.server.DTO.Chat.ChatMemberDto;
import Capstone.server.DTO.Chat.Msg;
import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Chat.SendMsgDto;
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
        String userProfileImg = profileService.getProfileImage(nickname1);
        String friendProfileImg = profileService.getProfileImage(nickname2);

        List<Msg> msgs = chatRepository.getAllMsgs(nickname1, nickname2);
        for(Msg msg : msgs) {
            String profileImg;
            if(msg.getNickname().equals(nickname1))
                profileImg = userProfileImg;
            else
                profileImg = friendProfileImg;
            MsgDto msgDto = new MsgDto(msg.getNickname(), profileImg, msg.getType(), msg.getMsg(), msg.getImage());
            msgDtos.add(msgDto);
        }
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
        String userProfileImg = profileService.getProfileImage(nickname1);
        String friendProfileImg = profileService.getProfileImage(nickname2);
        List<Msg> msgs = chatRepository.getUnreadMsg(nickname1, nickname2);
        if(msgs.isEmpty())
            return null;

        Msg m = msgs.get(msgs.size() - 1);
        chatRepository.updateMsgNum(nickname1, nickname2, m.getMsg_num());
        for(Msg msg : msgs) {
            String profileImg;
            if(msg.getNickname().equals(nickname1))
                profileImg = userProfileImg;
            else
                profileImg = friendProfileImg;
            MsgDto msgDto = new MsgDto(msg.getNickname(), profileImg, msg.getType(), msg.getMsg(), msg.getImage());
            unreadMsg.add(msgDto);
        }
        return unreadMsg;
    }

    public void outChat(ChatMemberDto chatMember) {
        chatRepository.setChatIsOn(chatMember.getNickname1(), chatMember.getNickname2(), false);
    }
}
