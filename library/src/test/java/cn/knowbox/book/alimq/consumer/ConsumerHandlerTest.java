package cn.knowbox.book.alimq.consumer;

import cn.knowbox.book.alimq.annotation.RocketMqConsume;
import cn.knowbox.book.alimq.error.MessageNullException;
import cn.knowbox.book.alimq.message.RocketMqMessage;
import cn.knowbox.book.alimq.parser.MqParser;
import com.aliyun.openservices.ons.api.Action;
import com.aliyun.openservices.ons.api.ConsumeContext;
import com.aliyun.openservices.ons.api.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * ConsumerHandlerTest
 *
 * @author Created by gold on 2023/3/17 11:40
 * @since 1.0.0
 */
public class ConsumerHandlerTest {

    private MqParser parser;
    private RocketMqConsume annotation;
    private ConsumerListener<String> stringListener;

    @BeforeEach
    public void setUp() {
        this.parser = mock(MqParser.class);
        this.annotation = mock(RocketMqConsume.class);
        this.stringListener = mock(StringConsumerListener.class);
    }

    @Test
    public void testConsume() {
        String domain = "body";
        Message message = genMessage(domain);

        when(parser.parse(eq(domain), any())).thenReturn(domain);

        ConsumerHandler<String> consumerHandler = new ConsumerHandler<>(parser, stringListener, annotation, false);

        Action action = consumerHandler.consume(message, new ConsumeContext());

        assertThat(action)
                .isEqualTo(Action.CommitMessage);

        verify(parser).parse(domain, String.class);
        verify(stringListener).onMessage(anyString());
    }

    @Test
    public void testConsumeFailure() {
        Message message = genMessage(null);

        ConsumerHandler<String> consumerHandler = new ConsumerHandler<>(parser, stringListener, annotation, false);

        Action action = consumerHandler.consume(message, new ConsumeContext());

        assertThat(action)
                .isEqualTo(Action.ReconsumeLater);

        verify(parser, never()).parse(any(), any());
        verify(stringListener, never()).onMessage(anyString());
    }

    @Test
    public void testConsumeFailureByReconsumeFor() {
        when(annotation.reconsumeFor()).thenReturn(new Class[]{MessageNullException.class});

        Message message = genMessage(null);

        ConsumerHandler<String> consumerHandler = new ConsumerHandler<>(parser, stringListener, annotation, false);

        Action action = consumerHandler.consume(message, new ConsumeContext());

        assertThat(action)
                .isEqualTo(Action.CommitMessage);

        verify(parser, never()).parse(any(), any());
        verify(stringListener, never()).onMessage(anyString());
    }

    @Test
    public void testConsumeFailureByReconsumeForAndMissException() {
        when(annotation.reconsumeFor()).thenReturn(new Class[]{NullPointerException.class});

        Message message = genMessage(null);

        ConsumerHandler<String> consumerHandler = new ConsumerHandler<>(parser, stringListener, annotation, false);

        Action action = consumerHandler.consume(message, new ConsumeContext());

        assertThat(action)
                .isEqualTo(Action.ReconsumeLater);

        verify(parser, never()).parse(any(), any());
        verify(stringListener, never()).onMessage(anyString());
    }

    private Message genMessage(String domain) {
        String topic = "topic";
        String tag = "tag";

        RocketMqMessage rocketMqMessage = new RocketMqMessage();
        rocketMqMessage.setTopic(topic);
        rocketMqMessage.setTag(tag);
        rocketMqMessage.setDomain(domain);

        Message message = new Message();
        message.setTopic(topic);
        message.setTag(tag);
        message.setStartDeliverTime(System.currentTimeMillis());
        message.setMsgID(UUID.randomUUID().toString());
        message.setBody(SerializationUtils.serialize(rocketMqMessage));

        return message;
    }

}
