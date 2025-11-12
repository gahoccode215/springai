package com.gahoccode.springai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class ChatService {

    private final ChatClient chatClient;
    private final PromptConfig promptConfig;
    private final WebSearchService webSearchService;

    public ChatService(ChatClient.Builder chatClientBuilder,
                       PromptConfig promptConfig,
                       WebSearchService webSearchService) {
        this.chatClient = chatClientBuilder.build();
        this.promptConfig = promptConfig;
        this.webSearchService = webSearchService;
    }

    public String askMLN(String question) throws IOException {
        String documentContext = promptConfig.getDocumentForMLN();

        // Bước 1: Kiểm tra xem tài liệu có đủ thông tin không
        String checkPrompt = """
            Dựa vào tài liệu sau, bạn có đủ thông tin để trả lời câu hỏi "%s" không?
            
            TÀI LIỆU:
            %s
            
            Chỉ trả lời "CÓ" hoặc "KHÔNG".
            """.formatted(question, documentContext);

        var checkResponse = chatClient.prompt()
                .user(checkPrompt)
                .call()
                .content();

        log.info("Document check result: {}", checkResponse);

        String finalContext = documentContext;
        String source = "tài liệu học tập";

        // Bước 2: Nếu không đủ thông tin, tìm kiếm trên web
        if (checkResponse != null && checkResponse.toUpperCase().contains("KHÔNG")) {
            log.info("Document insufficient, searching web...");

            String webInfo = webSearchService.search(question);

            if (webInfo != null && !webInfo.trim().isEmpty()) {
                finalContext = documentContext + "\n\nTHÔNG TIN BỔ SUNG TỪ INTERNET:\n" + webInfo;
                source = "tài liệu học tập và internet";
                log.info("Web search successful, added {} chars", webInfo.length());
            } else {
                log.warn("Web search returned no results");
            }
        }

        // Bước 3: Trả lời câu hỏi với context đầy đủ
        return askWithSource(question, finalContext, "Chủ nghĩa Mác-Lênin và Tư tưởng Hồ Chí Minh", source);
    }

    public String askVNR202(String question) throws IOException {
        String documentContext = promptConfig.getDocumentForVNR202();

        // Bước 1: Kiểm tra xem tài liệu có đủ thông tin không
        String checkPrompt = """
        Dựa vào tài liệu sau, bạn có đủ thông tin để trả lời câu hỏi "%s" không?
        
        TÀI LIỆU:
        %s
        
        Chỉ trả lời "CÓ" hoặc "KHÔNG".
        """.formatted(question, documentContext);

        var checkResponse = chatClient.prompt()
                .user(checkPrompt)
                .call()
                .content();

        log.info("Document check result: {}", checkResponse);

        String finalContext = documentContext;
        String source = "tài liệu học tập";

        // Bước 2: Nếu không đủ thông tin, tìm kiếm trên web
        if (checkResponse != null && checkResponse.toUpperCase().contains("KHÔNG")) {
            log.info("Document insufficient, searching web...");

            String webInfo = webSearchService.search(question);

            if (webInfo != null && !webInfo.trim().isEmpty()) {
                finalContext = documentContext + "\n\nTHÔNG TIN BỔ SUNG TỪ INTERNET:\n" + webInfo;
                source = "tài liệu học tập và internet";
                log.info("Web search successful, added {} chars", webInfo.length());
            } else {
                log.warn("Web search returned no results");
            }
        }

        // Bước 3: Trả lời câu hỏi với context đầy đủ
        return askWithSource(question, finalContext, "Lịch sử Việt Nam", source);
    }


    private String askWithSource(String question, String context, String subject, String source) {
        String promptText = """
        Bạn là trợ lý học tập chuyên về %s.
        
        NGUỒN THÔNG TIN: %s
        
        TÀI LIỆU THAM KHẢO:
        %s
        
        CÂU HỎI: %s
        
        HƯỚNG DẪN TRẢ LỜI:
        - Trả lời trực tiếp, bắt đầu ngay bằng nội dung chính
        - NGHIÊM CẤM dùng: "Theo tài liệu", "Theo nguồn", "Dựa theo", "Căn cứ vào"
        - Trả lời ngắn gọn, súc tích (5-10 câu)
        - Sử dụng ví dụ thực tiễn nếu có
        - Ưu tiên thông tin từ tài liệu học tập
        - Trả lời trên 1 đoạn văn, không xuống dòng
        """.formatted(subject, source, context, question);

        var response = chatClient.prompt()
                .user(promptText)
                .call();

        String answer = Objects.requireNonNull(response.content())
                .replaceAll("\\r?\\n", " ")
                .trim();

        // Double-check: bỏ các cụm từ nếu vẫn còn
        answer = answer.replaceAll("(?i)^(Theo|Dựa theo|Căn cứ vào)\\s+(tài liệu|nguồn)\\s+(tham khảo|học tập),?\\s*", "");

        // Thêm nguồn nếu dùng internet
        if (source.contains("internet")) {
            answer += " (Nguồn: tài liệu + tra cứu online)";
        }

        return answer;
    }

}