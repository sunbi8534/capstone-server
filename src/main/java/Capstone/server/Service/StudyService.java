package Capstone.server.Service;

import Capstone.server.DTO.Chat.ChatMemberDto;
import Capstone.server.DTO.Chat.Msg;
import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Chat.SendMsgDto;
import Capstone.server.DTO.Profile.UserInfoMinimumDto;
import Capstone.server.DTO.Study.*;
import Capstone.server.Repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {
    StudyRepository studyRepository;
    ProfileService profileService;

    public StudyService(StudyRepository studyRepository,
                        ProfileService profileService) {
        this.studyRepository = studyRepository;
        this.profileService = profileService;
    }

    public List<StudyInfoDto> getStudyRoomList(String nickname, RoomStatusDto roomStatus) {
        return studyRepository.getStudyRoomList(nickname, roomStatus);
    }

    public String makeStudyRoom(StudyMakeDto info) {
        return studyRepository.makeStudyRoom(info);
    }

    public String joinStudy(String nickname, StudyJoinDto joinInfo) {
        return studyRepository.joinStudy(nickname, joinInfo);
    }

    public List<MyStudyInfoDto> getMyStudyRoomList(String nickname) {
        return studyRepository.getMyStudyRoomList(nickname);
    }

    public List<UserInfoMinimumDto> enterStudy(int roomKey, String nickname) {
        return studyRepository.enterStudy(roomKey, nickname);
    }

    public List<MsgDto> getAllMsg(int roomKey, String nickname) {
        List<MsgDto> msgDtos = new ArrayList<>();

        List<Msg> msgs = studyRepository.getAllMsgs(roomKey, nickname);
        if(msgs == null)
            return msgDtos;

        for(Msg msg : msgs) {
            MsgDto msgDto = new MsgDto(msg.getNickname(), msg.getType(), msg.getMsg(), msg.getImage(), msg.getTime());
            msgDtos.add(msgDto);
        }

        Msg lastMsg = msgs.get(msgs.size() - 1);
        int updateMsgNum = lastMsg.getMsg_num();
        studyRepository.updateMsgNum(roomKey, updateMsgNum, nickname);
        studyRepository.setChatIsOn(roomKey, nickname, true);
        return msgDtos;
    }

    public void processSendMsg(StudySendMsgDto sendMsg) {
        studyRepository.updateSendMsg(sendMsg);
        //if(!chatRepository.checkUserIsOn(sendMsg.getReceiver(), sendMsg.getSender()))
        //    chatRepository.sendAlarm(sendMsg);
    }

    public List<MsgDto> getUnreadMsg(int roomKey, String nickname) {
        List<MsgDto> unreadMsg = new ArrayList<>();
        List<Msg> msgs = studyRepository.getUnreadMsg(roomKey, nickname);
        if(msgs.isEmpty())
            return unreadMsg;

        for(Msg msg : msgs) {
            MsgDto msgDto = new MsgDto(msg.getNickname(), msg.getType(), msg.getMsg(), msg.getImage(), msg.getTime());
            unreadMsg.add(msgDto);
        }

        Msg m = msgs.get(msgs.size() - 1);
        studyRepository.updateMsgNum(roomKey, m.getMsg_num(), nickname);

        return unreadMsg;
    }

    public void outChat(int roomKey, String nickname) {
        studyRepository.setChatIsOn(roomKey, nickname, false);
    }

    public String changeInfo(int roomKey, StudyChangeDto info) {
        return studyRepository.changeInfo(roomKey, info);
    }

    public void studyCommitLeader(int roomKey, String newLeader) {
        studyRepository.studyCommitLeader(roomKey, newLeader);
    }

    public void outStudy(int roomKey, String nickname) {
        studyRepository.outStudy(roomKey, nickname);
    }
}
