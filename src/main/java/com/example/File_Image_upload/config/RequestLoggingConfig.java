package com.example.File_Image_upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class RequestLoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();

        // Include query string
        filter.setIncludeQueryString(true);

        // Include request payload (POST/PUT body)
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);

        // Include headers
        filter.setIncludeHeaders(true);

        // Include client info
        filter.setIncludeClientInfo(true);

        // Custom message format
        filter.setBeforeMessagePrefix("INCOMING REQUEST: ");
        filter.setAfterMessagePrefix("REQUEST COMPLETED: ");

        return filter;
    }
}
