package cn.knowbox.book.alimq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

/**
 * 配置Bean
 *
 * @author Created by gold on 2019/10/4 15:21
 */
@Data
@ConfigurationProperties(prefix = "aliyun.mq")
public class RocketMqProperties {

    private String address;

    private String accessKey;

    private String secretKey;

    @NestedConfigurationProperty
    private ProducerProperty producer;

    @NestedConfigurationProperty
    private ConsumerProperty consumer;

    private String tagSuffix;

}
