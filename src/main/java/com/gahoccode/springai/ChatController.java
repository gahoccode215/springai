package com.gahoccode.springai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;


    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

//    @PostMapping("/hcm")
//    public Map<String, String> askHCM(@RequestBody Map<String, Object> request) {
//        try {
//            String question = (String) request.get("question");
//
//            String answer = chatService.askHCM(question);
//
//            return Map.of("message", answer);
//
//
//        } catch (Exception e) {
//            log.error("Error in HCM Q&A: ", e);
//            return Map.of("message", "Có lỗi xảy ra khi xử lý câu hỏi");
//        }
//
//    }
//
//    @PostMapping("/mln")
//    public Map<String, String> askMLN(@RequestBody Map<String, Object> request) {
//        try {
//            String question = (String) request.get("question");
//
//            String answer = chatService.askMLN(question);
//
//            return Map.of("message", answer);
//
//        } catch (Exception e) {
//            log.error("Error in MLN Q&A: ", e);
//            return Map.of("message", "Có lỗi xảy ra khi xử lý câu hỏi");
//        }
//    }

    @GetMapping("/vnr")
    public Map<String, String> askVNR(@RequestBody Map<String, Object> request) {
        try {
            String question = (String) request.get("question");

            String answer = chatService.askVNR(question);

            return Map.of("message", answer);

        } catch (Exception e) {
            log.error("Error in VNR Q&A: ", e);
            return Map.of("message", "Có lỗi xảy ra khi xử lý câu hỏi");
        }
    }
}
