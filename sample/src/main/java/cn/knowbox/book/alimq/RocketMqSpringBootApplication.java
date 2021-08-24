package cn.knowbox.book.alimq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动
 *
 * @author Created by gold on 2019/10/5 14:34
 */
@EnableScheduling
@SpringBootApplication
public class RocketMqSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(RocketMqSpringBootApplication.class, args);
    }
}
