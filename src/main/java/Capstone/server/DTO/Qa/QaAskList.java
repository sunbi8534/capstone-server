package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaAskList {
    int qaKey;
    String type;
    String course;
    Boolean isWatching;
    Boolean isSolving;
    Boolean status;
    long time;
}
