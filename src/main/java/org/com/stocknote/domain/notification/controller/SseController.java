package org.com.stocknote.domain.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.com.stocknote.domain.notification.service.SseEmitterService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
@Tag(name = "SSE API", description = "SSE API")
public class SseController {

    private final SseEmitterService sseEmitterService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "SSE 연결")
    public SseEmitter connect(@RequestParam String memberId) {
        return sseEmitterService.createEmitter(memberId);
    }

}
