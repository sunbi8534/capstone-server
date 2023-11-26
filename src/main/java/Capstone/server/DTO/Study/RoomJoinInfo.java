package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RoomJoinInfo {
    int maxNum;
    int curNum;
    Boolean isOpen;
    String code;
    String course;
}
