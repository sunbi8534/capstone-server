package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class QaAnswerList {
    int qaKey;
    String type;
    String course;
    Boolean status;
}
