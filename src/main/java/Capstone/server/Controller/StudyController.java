package Capstone.server.Controller;

import Capstone.server.DTO.Chat.MsgDto;
import Capstone.server.DTO.Profile.UserInfoMinimumDto;
import Capstone.server.DTO.Quiz.Quiz;
import Capstone.server.DTO.Quiz.QuizDto;
import Capstone.server.DTO.Study.*;
import Capstone.server.Service.StudyService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    @GetMapping("/study/code")
    public String getCode(@RequestParam int roomKey) {
        return studyService.getCode(roomKey);
    }

    @ResponseBody
    @GetMapping("/study/leader")
    public String getLeader(@RequestParam int roomKey) {
        return studyService.getLeader(roomKey);
    }

    @ResponseBody
    @PostMapping("/study/enroll/File/{roomKey}/{folderKey}/{nickname}")
    public String enrollFile(@PathVariable int roomKey, @PathVariable int folderKey, @PathVariable String nickname,
                           @RequestParam MultipartFile file) {
        return studyService.enrollFile(roomKey, folderKey, nickname, file);
    }

    @ResponseBody
    @PostMapping("/study/folder/{roomKey}/{folderName}")
    public void makeFolder(@PathVariable int roomKey, @PathVariable String folderName) {
        studyService.makeFolder(roomKey, folderName);
    }

    @ResponseBody
    @GetMapping("/study/quiz")
    public List<Quiz> getQuiz(@RequestBody StudyQuizInfoDto info) {
        return studyService.getQuiz(info);
    }

    @ResponseBody
    @GetMapping("/study/{roomKey}/{folderKey}")
    public List<String> getEnrollUserList(@PathVariable int roomKey, @PathVariable int folderKey) {
        return studyService.getEnrollUserList(roomKey, folderKey);
    }

    @ResponseBody
    @PostMapping("/study/{roomKey}/{folderKey}")
    public void deleteUserInFolder(@PathVariable int roomKey, @PathVariable int folderKey, @RequestParam String nickname) {
        studyService.deleteUserInFolder(roomKey, folderKey, nickname);
    }

    @ResponseBody
    @GetMapping("/study/folder/list")
    public List<StudyQuizListDto> getFolderList(@RequestParam int roomKey) {
        return studyService.getQuizList(roomKey);
    }

    @ResponseBody
    @PostMapping("/study/quiz/add/{quizKey}")
    public void addQuiz(@PathVariable int quizKey, @RequestBody List<QuizDto> quiz) {
        studyService.plusQuiz(quizKey, quiz);
    }
}
