package Capstone.server.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class SendMsgDto {
    String sender;
    String receiver;
    String type;
    String msg;
    String img;
}
