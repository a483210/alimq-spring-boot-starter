package cn.knowbox.book.alimq.aot;

import cn.knowbox.book.alimq.consumer.ConsumerInitializingProcessor;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.message.TransactionMessage;
import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;
import com.aliyun.openservices.ons.api.bean.TransactionProducerBean;
import com.aliyun.openservices.ons.api.impl.ONSFactoryImpl;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.consumer.store.OffsetSerializeWrapper;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.producer.SendResult;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.OrderTopicType;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.ProtectedMetaConfig;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.QueueGroupConfig;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.TopicConfig;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.common.subscription.SubscriptionGroupConfig;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.protocol.RemotingSerializable;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.proxy.ProxyConfig;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.stream.DefaultStream;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.lang.NonNull;

import java.util.Arrays;

/**
 * RocketMqAotHints
 *
 * @author Created by gold on 2023/3/29 09:18
 * @since 1.0.0
 */
public class RocketMqAotHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        if (classLoader == null) {
            return;
        }

        hints.serialization()
                .registerType(Message.class)
                .registerType(RocketMqMessage.class)
                .registerType(TransactionMessage.class);

        AotHintUtils.registerMethodCall(hints, ProducerBean.class, "shutdown");
        AotHintUtils.registerMethodCall(hints, OrderProducerBean.class, "shutdown");
        AotHintUtils.registerMethodCall(hints, TransactionProducerBean.class, "shutdown");
        AotHintUtils.registerMethodCall(hints, ConsumerInitializingProcessor.class, "shutdown");

        AotHintUtils.registerTypeBatch(hints,
                SendResult.class,
                OffsetSerializeWrapper.class,
                ProtectedMetaConfig.class,
                DefaultStream.class,
                ProxyConfig.class,
                QueueGroupConfig.class,
                TopicConfig.class,
                OrderTopicType.class,
                SubscriptionGroupConfig.class
        );

        hints.reflection()
                .registerType(ONSFactoryImpl.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);

        AotHintUtils.registerReflexPath(hints, classLoader,
                Arrays.asList("com.aliyun.openservices.shade.com.alibaba.rocketmq.common.protocol",
                        "com.aliyun.openservices.shade.com.alibaba.rocketmq.common.message",
                        "com.aliyun.openservices.shade.com.alibaba.rocketmq.common.admin",
                        "com.aliyun.openservices.shade.com.alibaba.rocketmq.remoting.protocol")
        );

        AotHintUtils.registerReflexPath(hints, classLoader,
                Arrays.asList("com.aliyun.openservices.shade.com.alibaba.rocketmq.common",
                        "com.aliyun.openservices.shade.com.alibaba.rocketmq.client"),
                RemotingSerializable.class::isAssignableFrom
        );
    }
}
