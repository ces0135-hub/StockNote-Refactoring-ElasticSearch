package org.com.stocknote.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {
    // 클라이언트별 Emitter 저장소
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // SSE 연결 생성
    public SseEmitter createEmitter(String memberId) {
        // 5분 타임아웃 설정
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        emitters.put(memberId, emitter);

        // 연결 종료 리스너
        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    // 알림 전송
    public void sendCommentNotification(String memberId, Object data) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("commentNotification")
                        .data(data));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }

    public void sendKeywordNotification(String memberId, Object data) {
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("keywordNotification")
                        .data(data));
            } catch (IOException e) {
                emitters.remove(memberId);
            }
        }
    }
}
