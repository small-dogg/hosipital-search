package com.smalldogg.hospitalsearch.queue;

import com.smalldogg.hospitalsearch.queue.in.ReserveHospitalParam;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping(value = "/hospitals/{encId")
@Controller
public class ReserveViewController {
    private final HospitalReserveService hospitalReserveService;

    /**
     * Queue 진입 이전 화면
     */
    @GetMapping("")
    public String queueHome(@PathVariable String encId, Model model) {
        model.addAttribute("encId", encId);
        return "queue-home";
    };

    /**
     * 대기열 합류
     */
    @PostMapping("/join")
    public String join(@PathVariable String encId,
                       @CookieValue(name = "SESSION_KEY", required = false) String sessionKey,
                       Model model) {

        // 테스트 목적: session_key 없으면 발급
        if (sessionKey == null || sessionKey.isBlank()) {
            sessionKey = java.util.UUID.randomUUID().toString();
        }

        UUID ticketId = hospitalReserveService.join(new ReserveHospitalParam(encId, sessionKey));

        return "redirect:/hospitals/" + encId + "/queue/wait?ticketId=" + ticketId + "&sessionKey=" + sessionKey;
    }

    /**
     * Queue 대기 화면
     */
    @GetMapping("/wait")
    public String waitPage(@PathVariable String encId,
                           @RequestParam UUID ticketId,
                           @RequestParam(required = false) String sessionKey,
                           Model model) {
        model.addAttribute("encId", encId);
        model.addAttribute("ticketId", ticketId);
        model.addAttribute("sessionKey", sessionKey); // 임시(나중에 쿠키로 정리)
        return "queue-wait";
    }

    /**
     * 예약화면
     */
    @GetMapping("/reserve")
    public String reservePage(@PathVariable String encId,
                              @RequestParam UUID ticketId,
                              Model model) {
        model.addAttribute("encId", encId);
        model.addAttribute("ticketId", ticketId);
        return "reserve-page";
    }
}
