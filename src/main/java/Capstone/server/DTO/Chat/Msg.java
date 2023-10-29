package Capstone.server.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Msg {
    int msg_num;
    String nickname;
    String type;
    String msg;
    String image;
}
