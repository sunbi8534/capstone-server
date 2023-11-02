package Capstone.server.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ChatList {
    String friendNickname;
    int msg_num;
    int chatTableKey;
}
