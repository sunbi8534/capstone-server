package Capstone.server.DTO.Qa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QaMsgDto {
    String nickname;
    String type;
    String msg;
    String img;
    String time;
    Boolean isAnonymity;

    public QaMsgDto(String nickname, String type, String msg, String img, String time) {
        this.nickname = nickname;
        this.type = type;
        this.msg = msg;
        this.img = img;
        this.time = time;
        this.isAnonymity = false;
    }
}
