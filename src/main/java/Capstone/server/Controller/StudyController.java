package Capstone.server.Controller;

import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Profile.UserInfoMinimumDto;
import Capstone.server.DTO.Study.*;
import Capstone.server.Service.StudyService;
import org.springframework.web.bind.annotation.*;

import javax.mail.Multipart;
import java.util.List;

@RestController
public class StudyController {
    StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @ResponseBody
    @GetMapping("/study/{nickname}")
    public List<StudyInfoDto> getStudyRoomList(@PathVariable String nickname, @RequestBody RoomStatusDto roomStatus) {
        return studyService.getStudyRoomList(nickname, roomStatus);
    }

    @ResponseBody
    @PostMapping("/study/make")
    public String makeStudyRoom(@RequestBody StudyMakeDto info) {
        return studyService.makeStudyRoom(info);
    }

    @ResponseBody
    @PostMapping("/study/join/{nickname}")
    public String joinStudy(@PathVariable String nickname, @RequestBody StudyJoinDto joinInfo) {
        return studyService.joinStudy(nickname, joinInfo);
    }

    @ResponseBody
    @GetMapping("/user/study")
    public List<MyStudyInfoDto> getMyStudyRoomList(@RequestParam String nickname) {
        return studyService.getMyStudyRoomList(nickname);
    }

    @ResponseBody
    @GetMapping("/study/enter")
    public List<UserInfoMinimumDto> enterStudy(@RequestParam int roomKey, @RequestParam String nickname) {
        return studyService.enterStudy(roomKey, nickname);
    }

    @ResponseBody
    @GetMapping("/study/chat")
    public List<MsgDto> getAllMsg(@RequestParam int roomKey, @RequestParam String nickname) {
        return studyService.getAllMsg(roomKey, nickname);
    }

    @ResponseBody
    @PostMapping("/study/chat/sendMsg")
    public void sendMsg(@RequestBody StudySendMsgDto sendMsg) {
        studyService.processSendMsg(sendMsg);
    }

    @ResponseBody
    @GetMapping("/study/chat/getMsg")
    public List<MsgDto> getMsg(@RequestParam int roomKey, @RequestParam String nickname) {
        return studyService.getUnreadMsg(roomKey, nickname);
    }

    @ResponseBody
    @PostMapping("/study/chat/outChat")
    public void outChat(@RequestParam int roomKey, @RequestParam String nickname) {
        studyService.outChat(roomKey, nickname);
    }

    @ResponseBody
    @PostMapping("/study/change/{roomKey}")
    public String changeInfo(@PathVariable int roomKey, @RequestBody StudyChangeDto info) {
        return studyService.changeInfo(roomKey, info);
    }

    @ResponseBody
    @PostMapping("/study/commitLeader/{roomKey}")
    public void studyCommitLeader(@PathVariable int roomKey, @RequestParam String newLeader) {
        studyService.studyCommitLeader(roomKey, newLeader);
    }

    @ResponseBody
    @PostMapping("/study/out/{nickname}")
    public void outStudy(@PathVariable String nickname, @RequestParam int roomKey) {
        studyService.outStudy(roomKey, nickname);
    }

    @ResponseBody
    @PostMapping("/study/enroll/File/{roomKey}")
    public void enrollFile(@PathVariable int roomKey, Multipart file) {

    }

    @ResponseBody
    @GetMapping("/study/getQuiz/{roomKey}")
    public void getQuiz(@PathVariable int roomKey) {

    }
}
