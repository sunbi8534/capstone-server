package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MyStudyInfoDto {
    int roomKey;
    String roomName;
    String course;
    int maxNum;
    int curNum;
    String startDate;
    String studyIntroduction;
}
