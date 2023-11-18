package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StudyInfoDto {
    int roomKey;  //스터디 고유키
    String roomName; //스터디 제목
    String course; //과목명
    int maxNum; //최대인원수
    int curNum; //현재 인원수
    String leader; //그룹장 닉네임
    String startDate; //시작일
    Boolean isOpen; //공개여부
    String studyIntroduction; //스터디 소개글
}
