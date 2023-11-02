package Capstone.server.DTO.Qa;

import Capstone.server.DTO.Chat.MsgDto;
import lombok.Getter;

import java.util.List;

@Getter
public class QaDto {
    String type; //조언인지 문제인지
    String course;
    int point;
    String nickname; //질문 올린 사람
    Boolean isAnonymity; //익명으로 질문을 올렸는지
    List<MsgDto> msg; //질문 채팅 내용이 담긴 메세지들
}
