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
import org.springframework.util.StringUtils;

import java.util.Properties;

import cn.knowbox.book.alimq.config.RocketMqProperties;
import cn.knowbox.book.alimq.consumer.ConsumerProcessor;
import cn.knowbox.book.alimq.parser.JacksonMqParser;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.TransactionCheckerProcessor;
import cn.knowbox.book.alimq.producer.impl.LocalTransactionCheckerImpl;
import cn.knowbox.book.alimq.producer.template.RocketMqOrderTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTransactionTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * 初始化(生成|消费)相关配置
 *
 * @author Created by gold on 2019/10/4 15:22
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(RocketMqProperties.class)
public class RocketMqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqParser mqParser() {
        return new JacksonMqParser();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true")
    public RocketMqTemplate rocketMqTemplate(MqParser mqParser, ProducerBean producer) {
        return new RocketMqTemplate(mqParser, producer);
    }

    @Bean(name = "producer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true")
    public ProducerBean producer(RocketMqProperties rocketMqProperties) {
        log.info("rocketMq producer init");

        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(createProperties(rocketMqProperties));
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public RocketMqOrderTemplate rocketMqOrderTemplate(MqParser mqParser, OrderProducerBean orderProducer) {
        return new RocketMqOrderTemplate(mqParser, orderProducer);
    }

    @Bean(name = "orderProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public OrderProducerBean orderProducer(RocketMqProperties rocketMqProperties) {
        log.info("rocketMq orderProducer init");

        OrderProducerBean producerBean = new OrderProducerBean();
        producerBean.setProperties(createProperties(rocketMqProperties));
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public RocketMqTransactionTemplate rocketMqTransactionTemplate(MqParser mqParser, TransactionProducerBean transactionProducer) {
        return new RocketMqTransactionTemplate(mqParser, transactionProducer);
    }

    @Bean(name = "transactionProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionProducerBean transactionProducer(RocketMqProperties rocketMqProperties, MqParser mqParser) {
        log.info("rocketMq transactionProducer init");

        TransactionProducerBean producerBean = new TransactionProducerBean();
        producerBean.setProperties(createProperties(rocketMqProperties));
        producerBean.setLocalTransactionChecker(new LocalTransactionCheckerImpl(mqParser));
        producerBean.start();

        return producerBean;
    }

    private Properties createProperties(RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();

        String groupId = rocketMqProperties.getProducer().getGroupId();
        if (!StringUtils.isEmpty(groupId)) {
            properties.put(PropertyKeyConst.GROUP_ID, groupId);
        }

        properties.put(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getAddress());
        return properties;
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.consumer", value = "enabled", havingValue = "true")
    public ConsumerProcessor consumer(MqParser mqParser, RocketMqProperties properties) {
        log.info("rocketMq consumer init");

        return new ConsumerProcessor(mqParser, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionCheckerProcessor transactionCheckerProducer(TransactionProducerBean transactionProducer) {
        return new TransactionCheckerProcessor(transactionProducer);
    }

}
