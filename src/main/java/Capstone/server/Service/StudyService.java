package Capstone.server.Service;

import Capstone.server.DTO.Study.*;
import Capstone.server.Repository.StudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class StudyService {
    StudyRepository studyRepository;

    public StudyService(StudyRepository studyRepository) {
        this.studyRepository = studyRepository;
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
}
