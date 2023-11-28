package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaAlarmDto {
    String nickname;
    int qaKey;
    String course;
}
