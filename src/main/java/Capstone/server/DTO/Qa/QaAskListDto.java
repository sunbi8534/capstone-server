package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaAskListDto {
    int qaKey;
    String type;
    String course;
    String status;
}
