package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StudyChangeDto {
    String roomName;
    String course;
    int maxNum;
    Boolean isOpen;
    String code;
    String studyIntroduction;
}
