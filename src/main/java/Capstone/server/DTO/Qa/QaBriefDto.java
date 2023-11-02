package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaBriefDto {
    int qaKey;   //질문구분을 위한 key
    String type;
    String course;
    int point;
}
