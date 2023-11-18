package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudyQuizInfoDto {
    int roomKey;
    int folderKey;
    boolean type; //true면 다지선다, false면 개념
    int quizNum;
}
