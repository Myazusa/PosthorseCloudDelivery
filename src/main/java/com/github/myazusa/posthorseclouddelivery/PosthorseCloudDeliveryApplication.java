package com.github.myazusa.posthorseclouddelivery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class PosthorseCloudDeliveryApplication {

    public static void main(String[] args) {
        SpringApplication.run(PosthorseCloudDeliveryApplication.class, args);
        log.info("驿马云投递后端服务器已启动");
    }

}
