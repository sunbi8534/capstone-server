package Capstone.server.DTO.Chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MsgDto {
    String nickname;
    String profileImage;   //이미지 파일을 base64로 인코딩한 문자열
    String type;   //메세지 유형이 이미지인지 문자인지
    String msg;
    String image;   //이미지 파일을 base64로 인코딩한 문자열
}
