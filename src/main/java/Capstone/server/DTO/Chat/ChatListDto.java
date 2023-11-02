package Capstone.server.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ChatListDto {
    String nickname;
    String time;
    String msg;
    Boolean alarm;
}
