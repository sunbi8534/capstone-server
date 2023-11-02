package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaSendMsgDto {
    String nickname;
    String type;
    String msg;
    String img;
    String time;
}
