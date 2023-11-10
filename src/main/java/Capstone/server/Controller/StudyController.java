package Capstone.server.Controller;

import Capstone.server.DTO.Study.RoomStatusDto;
import Capstone.server.DTO.Study.StudyInfoDto;
import Capstone.server.DTO.Study.StudyJoinDto;
import Capstone.server.DTO.Study.StudyMakeDto;
import Capstone.server.Service.StudyService;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/studyRoom/join/{nickname}")
    public String joinStudy(@PathVariable String nickname, @RequestBody StudyJoinDto joinInfo) {
        return studyService.joinStudy(nickname, joinInfo);
    }

    @ResponseBody
    @GetMapping("/user/study")
    public String getMyStudyRoomList(@RequestParam String nickname) {

    }
}
