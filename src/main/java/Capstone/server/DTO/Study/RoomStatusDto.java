package Capstone.server.DTO.Study;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RoomStatusDto {
    Boolean isAll;
    Boolean isSeatLeft;
    Boolean isOpen;
}
