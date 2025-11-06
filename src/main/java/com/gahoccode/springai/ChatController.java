package com.gahoccode.springai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class ChatController {

    private final ChatService chatService;


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }


    @GetMapping("/mln")
    public Map<String, String> askMLN(@RequestParam String message) {
        try {

            String answer = chatService.askMLN(message);

            return Map.of("message", answer);

        } catch (Exception e) {
            log.error("Error in MLN Q&A: ", e);
            return Map.of("message", "Có lỗi xảy ra khi xử lý câu hỏi");
        }
    }


}
