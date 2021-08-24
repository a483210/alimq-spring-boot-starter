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

    /**
     * 地址
     */
    private String address;

    /**
     * key
     */
    private String accessKey;
    /**
     * 秘钥
     */
    private String secretKey;

    /**
     * 生产者配置
     */
    @NestedConfigurationProperty
    private ProducerProperty producer;

    /**
     * 消费者配置
     */
    @NestedConfigurationProperty
    private ConsumerProperty consumer;

    /**
     * tag后缀
     */
    private String tagSuffix;

}
