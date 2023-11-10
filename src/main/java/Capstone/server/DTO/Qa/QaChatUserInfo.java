package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class QaChatUserInfo {
    String nickname;
    int msgNum;
    String profileImg;
    String solverNickname;
    int solverMsgNum;
    String solverProfileImg;

    public QaChatUserInfo(String nickname, int msgNum, String solverNickname, int solverMsgNum) {
        this.nickname = nickname;
        this.msgNum = msgNum;
        this.solverNickname = solverNickname;
        this.solverMsgNum = solverMsgNum;
    }
}