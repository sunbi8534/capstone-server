package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StudyMakeDto {
    String roomName;
    String course;
    int maxNum;
    String leader;
    String startDate;
    Boolean isOpen;
    String code;
    String studyIntroduction;
}
