package cn.knowbox.book.alimq;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

import cn.knowbox.book.alimq.config.RocketMqProperties;
import cn.knowbox.book.alimq.consumer.ConsumerProcessor;
import cn.knowbox.book.alimq.producer.TransactionCheckerProcessor;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionCheckerImpl;
import cn.knowbox.book.alimq.producer.template.RocketMqOrderTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTransactionTemplate;
import lombok.extern.log4j.Log4j2;

/**
 * 初始化(生成|消费)相关配置
 *
 * @author Created by gold on 2019/10/4 15:22
 */
@Log4j2
@Configuration
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true")
    public RocketMqTemplate rocketMqTemplate(ProducerBean producer) {
        return new RocketMqTemplate(producer);
    }

    @Bean(name = "producer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true")
    public ProducerBean producer(RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();

        log.info("RocketMq Producer初始化！！！");

        properties.put(PropertyKeyConst.GROUP_ID, rocketMqProperties.getProducer().getGroupId());
        properties.put(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getAddress());

        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(properties);
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public RocketMqOrderTemplate rocketMqOrderTemplate(OrderProducerBean orderProducer) {
        return new RocketMqOrderTemplate(orderProducer);
    }

    @Bean(name = "orderProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public OrderProducerBean orderProducer(RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();

        log.info("RocketMq OrderProducer初始化！！！");

        properties.put(PropertyKeyConst.GROUP_ID, rocketMqProperties.getProducer().getGroupId());
        properties.put(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getAddress());

        OrderProducerBean producerBean = new OrderProducerBean();
        producerBean.setProperties(properties);
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public RocketMqTransactionTemplate rocketMqTransactionTemplate(TransactionProducerBean transactionProducer) {
        return new RocketMqTransactionTemplate(transactionProducer);
    }

    @Bean(name = "transactionProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionProducerBean transactionProducer(RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();

        log.info("RocketMq TransactionProducer初始化！！！");

        properties.put(PropertyKeyConst.GROUP_ID, rocketMqProperties.getProducer().getGroupId());
        properties.put(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getAddress());

        TransactionProducerBean producerBean = new TransactionProducerBean();
        producerBean.setProperties(properties);
        producerBean.setLocalTransactionChecker(new LocalTransactionCheckerImpl());
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionCheckerProcessor transactionCheckerProducer(TransactionProducerBean transactionProducer) {
        return new TransactionCheckerProcessor(transactionProducer);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.consumer", value = "enabled", havingValue = "true")
    public ConsumerProcessor consumer(RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();

        log.info("RocketMq Consumer初始化！！！");

        properties.setProperty(PropertyKeyConst.GROUP_ID, rocketMqProperties.getConsumer().getGroupId());
        properties.setProperty(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.setProperty(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.setProperty(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getAddress());

        return new ConsumerProcessor(properties);
    }

}
