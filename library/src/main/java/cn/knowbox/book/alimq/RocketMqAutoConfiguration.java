package cn.knowbox.book.alimq;

import cn.knowbox.book.alimq.consts.RocketMqConstants;
import cn.knowbox.book.alimq.consumer.ConsumerInitializingProcessor;
import cn.knowbox.book.alimq.consumer.ConsumerPostProcessor;
import cn.knowbox.book.alimq.parser.JacksonMqParser;
import cn.knowbox.book.alimq.parser.MqParser;
import cn.knowbox.book.alimq.producer.TransactionCheckerInitializingProcessor;
import cn.knowbox.book.alimq.producer.TransactionCheckerPostProcessor;
import cn.knowbox.book.alimq.producer.impl.DelegateTransactionCheckerImpl;
import cn.knowbox.book.alimq.producer.template.RocketMqOrderTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTemplate;
import cn.knowbox.book.alimq.producer.template.RocketMqTransactionTemplate;
import cn.knowbox.book.alimq.properties.RocketMqProperties;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.util.Properties;

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
    @ConditionalOnMissingBean(MqParser.class)
    public MqParser mqParser(@Autowired(required = false) ObjectMapper objectMapper) {
        if (objectMapper != null) {
            return new JacksonMqParser(objectMapper);
        }

        return new JacksonMqParser();
    }

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true", matchIfMissing = true)
    public RocketMqTemplate rocketMqTemplate(MqParser mqParser,
                                             ProducerBean producer,
                                             RocketMqProperties properties) {
        return new RocketMqTemplate(mqParser, producer, properties);
    }

    @Bean(name = "producer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "enabled", havingValue = "true", matchIfMissing = true)
    public ProducerBean producer(RocketMqProperties rocketMqProperties) {
        log.info("rocketMq producer init");

        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(createProperties(rocketMqProperties));
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public RocketMqOrderTemplate rocketMqOrderTemplate(MqParser mqParser,
                                                       OrderProducerBean orderProducer,
                                                       RocketMqProperties properties) {
        return new RocketMqOrderTemplate(mqParser, orderProducer, properties);
    }

    @Bean(name = "orderProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "order-enabled", havingValue = "true")
    public OrderProducerBean orderProducer(RocketMqProperties rocketMqProperties) {
        log.info("rocketMq orderProducer init");

        OrderProducerBean producerBean = new OrderProducerBean();
        producerBean.setProperties(createProperties(rocketMqProperties));
        producerBean.start();

        return producerBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public RocketMqTransactionTemplate rocketMqTransactionTemplate(MqParser mqParser,
                                                                   TransactionProducerBean transactionProducer,
                                                                   RocketMqProperties properties) {
        return new RocketMqTransactionTemplate(mqParser, transactionProducer, properties);
    }

    @Bean(name = "transactionProducer", initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionProducerBean transactionProducer(RocketMqProperties rocketMqProperties, MqParser mqParser) {
        log.info("rocketMq transactionProducer init");

        TransactionProducerBean producerBean = new TransactionProducerBean();
        producerBean.setProperties(createProperties(rocketMqProperties));
        producerBean.setLocalTransactionChecker(new DelegateTransactionCheckerImpl(mqParser));
        producerBean.start();

        return producerBean;
    }

    private Properties createProperties(RocketMqProperties rocketMqProperties) {
        Properties properties = new Properties();

        String groupId = rocketMqProperties.getProducer().getGroupId();
        if (!ObjectUtils.isEmpty(groupId)) {
            properties.put(PropertyKeyConst.GROUP_ID, groupId);
        }

        properties.put(PropertyKeyConst.AccessKey, rocketMqProperties.getAccessKey());
        properties.put(PropertyKeyConst.SecretKey, rocketMqProperties.getSecretKey());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, rocketMqProperties.getAddress());
        properties.put(RocketMqConstants.CLUSTER_TYPE, rocketMqProperties.getClusterType());

        return properties;
    }

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.mq.consumer", value = "enabled", havingValue = "true", matchIfMissing = true)
    public ConsumerPostProcessor consumerPostProcessor() {
        return new ConsumerPostProcessor();
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = "aliyun.mq.consumer", value = "enabled", havingValue = "true", matchIfMissing = true)
    public ConsumerInitializingProcessor consumerInitializingProcessor(MqParser mqParser,
                                                                       RocketMqProperties properties,
                                                                       ConsumerPostProcessor processor) {
        log.info("rocketMq consumer init");

        return new ConsumerInitializingProcessor(mqParser, properties, processor);
    }

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionCheckerPostProcessor transactionCheckerPostProcessor() {
        return new TransactionCheckerPostProcessor();
    }

    @Bean
    @ConditionalOnProperty(prefix = "aliyun.mq.producer", value = "transaction-enabled", havingValue = "true")
    public TransactionCheckerInitializingProcessor transactionCheckerInitializingProcessor(TransactionProducerBean transactionProducer,
                                                                                           RocketMqProperties properties,
                                                                                           TransactionCheckerPostProcessor processor) {
        return new TransactionCheckerInitializingProcessor(transactionProducer, properties, processor);
    }
}
