package Capstone.server.DTO.Etc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDto {
    String nickname;
    String reporter;
    int reportKind; //신고 유형
    String reportMsg;  //신고 사유
}
