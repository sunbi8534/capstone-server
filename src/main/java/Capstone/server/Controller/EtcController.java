package Capstone.server.Controller;

import Capstone.server.DTO.Etc.AlarmDto;
import Capstone.server.DTO.Etc.NotifyDto;
import Capstone.server.DTO.Etc.ReportDto;
import Capstone.server.Service.EtcService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class EtcController {
    EtcService etcService;

    public EtcController(EtcService etcService) {
        this.etcService = etcService;
    }

    @ResponseBody
    @GetMapping("/alarm/event")
    public String getAlarmEvent(@RequestParam String nickname) {
        if(etcService.checkAlarm(nickname))
            return "ok";
        else
            return "no";
    }

    @ResponseBody
    @GetMapping("/alarm")
    public List<AlarmDto> getAlarm(@RequestParam String nickname) {
        return etcService.getAlarms(nickname);
    }

    @ResponseBody
    @GetMapping("/notify")
    public List<NotifyDto> getNotify() {
        return etcService.getNotify();
    }

    @ResponseBody
    @PostMapping("/report")
    public String report(@RequestBody ReportDto report) {
        etcService.storeReport(report);
        return "ok";
    }
}
