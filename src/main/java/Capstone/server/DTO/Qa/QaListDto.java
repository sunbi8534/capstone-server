package Capstone.server.DTO.Qa;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class QaListDto {
    int qaKey;
    String type;
    String course;
    Boolean status;
}
