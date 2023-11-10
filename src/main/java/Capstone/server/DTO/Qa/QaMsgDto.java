package Capstone.server.DTO.Qa;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QaMsgDto {
    String nickname;
    String profileImg; //익명이라면 null값이 담겨있다.
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
