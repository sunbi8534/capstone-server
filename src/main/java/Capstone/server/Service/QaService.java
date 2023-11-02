package Capstone.server.Service;

import Capstone.server.DTO.Qa.QaBriefDto;
import Capstone.server.DTO.Qa.QaDto;
import Capstone.server.DTO.Qa.QaMsgDto;
import Capstone.server.DTO.Qa.QaSendMsgDto;
import Capstone.server.Repository.QaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QaService {
    QaRepository qaRepository;
    ProfileService profileService;

    QaService(QaRepository qaRepository,
              ProfileService profileService) {
        this.qaRepository = qaRepository;
        this.profileService = profileService;
    }

    public int enrollQa(QaDto qaDto) {
        return qaRepository.enrollQa(qaDto);
    }

    public String deleteQa(int qaKey) {
        return qaRepository.deleteQa(qaKey);
    }

    public List<QaBriefDto> getQaList(String nickname) {
        return qaRepository.getQaList(nickname, profileService.getUserCourse(nickname));
    }

    public List<QaMsgDto> getQuestion(int qaKey, String nickname) {
        return qaRepository.getQuestion(qaKey, nickname);
    }

    public void qaGiveUp(int qaKey, String nickname) {
        qaRepository.qaGiveUp(qaKey, nickname);
    }

    public void qaSolve(int qaKey) {
        qaRepository.qaSolve(qaKey);
    }

    public void sendQaMsg(int qaKey, QaSendMsgDto msg) {
        qaRepository.sendQaMsg(qaKey, msg);
    }

    public List<QaMsgDto> getQaMsgs(int qaKey, String nickname) {
        return qaRepository.getQaMsgs(qaKey, nickname);
    }
}
