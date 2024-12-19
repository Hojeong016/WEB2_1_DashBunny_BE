package com.devcourse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 활성화 한 기능
 * EnableAsync : 비동기 활성화.
 */
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EnableScheduling // 스케줄링을 활성화
public class Web21DashBunnyBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Web21DashBunnyBeApplication.class, args);
    }

}
