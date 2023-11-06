package Capstone.server.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatListDto {
    String nickname;
    String time;
    String msg;
    Boolean alarm;
}
