package com.gahoccode.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Objects;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final PromptConfig promptConfig;



    public ChatService(ChatClient.Builder chatClientBuilder,
                       PromptConfig promptConfig) {
        this.chatClient = chatClientBuilder.build();
        this.promptConfig = promptConfig;
    }


//    public String askHCM(String question) throws IOException {
//        String context = promptConfig.getDocumentForHCM();
//        return askWithSource(question, context, "Tư tưởng Hồ Chí Minh");
//    }
//
//    public String askMLN(String question) throws IOException {
//        String context = promptConfig.getDocumentForMLN();
//        return askWithSource(question, context, "Chủ nghĩa xã hội và khoa học");
//    }

    public String askVNR(String question) throws IOException {
        String context = promptConfig.getDocumentForVNR();
        return askWithSource(question, context, "Lịch sử Đảng Cộng sản Việt Nam");
    }


    private String askWithSource(String question, String context, String subject) {


        String promptText = createPrompt(context, question, subject);

        var response = chatClient.prompt()
                .system(context)
                .user(promptText)
                .call();

        return Objects.requireNonNull(response.content()).replaceAll("\\r?\\n", " ").trim();
    }


    private String createPrompt(String context, String question, String subject) {

        return """
            Bạn là một trợ lý học tập chuyên về môn %s.
            
            TÀI LIỆU THAM KHẢO:
            %s
            
            CÂU HỎI: %s
            
            HƯỚNG DẪN TRẢ LỜI:
            - Nếu câu hỏi ngoài phạm vi %s: "Xin lỗi, tôi chỉ hỗ trợ câu hỏi về %s."
            - Nếu không biết: "Xin lỗi, tôi không có kiến thức về nội dung này."
            - Trả lời ngắn gọn, dễ hiểu, có ví dụ thực tiễn.
            - Khuyến khích tinh thần học tập và đạo đức.
            - Trả lời duy nhất trên 1 dòng, không được chứa ký tự xuống dòng (newline).
            """.formatted(subject, context, question, subject, subject);
    }

}
