package com.scheduler.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class MailConfig {
    private String host;
    private Integer port;
    private String username;
    private String password;
    private Properties properties = new Properties();

    @Data
    public static class Properties {
        private Smtp smtp = new Smtp();

        @Data
        public static class Smtp {
            private boolean auth;
            private Starttls starttls = new Starttls();

            @Data
            public static class Starttls {
                private boolean enable;
            }
        }
    }
} 