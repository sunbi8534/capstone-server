package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudySendMsgDto {
    String sender;
    int roomKey;
    String type;
    String msg;
    String img;
    String time;
}
