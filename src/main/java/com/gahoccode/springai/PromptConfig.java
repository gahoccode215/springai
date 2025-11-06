package com.gahoccode.springai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Configuration
public class PromptConfig {



    @Bean
    public String getDocumentForMLN() throws IOException {
        var resource = new ClassPathResource("MLN131_document.txt");
        try (var inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


}
