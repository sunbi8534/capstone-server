package Capstone.server.DTO.Enroll;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VerificationCheckDto {
    int verificationNum;
    long EpochSecond;
    String email;
    String verificationHashcode;
}
