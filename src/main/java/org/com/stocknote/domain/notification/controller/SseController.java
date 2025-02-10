package org.com.stocknote.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.notification.service.SseEmitterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String memberId) {
        return sseEmitterService.createEmitter(memberId);
    }

//    @PostMapping("/send")
//    public void sendNotification(@RequestParam String memberId, @RequestBody Object data) {
//        sseEmitterService.sendNotification(memberId, data);
//    }
}
