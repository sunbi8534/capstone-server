package Capstone.server.Service;

import Capstone.server.DTO.Etc.AlarmDto;
import Capstone.server.DTO.Etc.NotifyDto;
import Capstone.server.DTO.Etc.ReportDto;
import Capstone.server.Repository.EtcRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtcService {
    EtcRepository etcRepository;

    public EtcService(EtcRepository etcRepository) {
        this.etcRepository = etcRepository;
    }
    public boolean checkAlarm(String nickname) {
        return etcRepository.checkAlarm(nickname);
    }

    public List<AlarmDto> getAlarms(String nickname) {
        return etcRepository.getAlarms(nickname);
    }

    public List<NotifyDto> getNotify() {
        return etcRepository.getNotify();
    }

    public void storeReport(ReportDto report) {
        etcRepository.storeReport(report);
    }
}
