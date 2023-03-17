package cn.knowbox.book.alimq.message;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * RocketMqMessageTest
 *
 * @author Created by gold on 2023/3/16 15:46
 * @since 1.0.0
 */
public class RocketMqMessageTest {

    @Test
    public void testGenerateTxId() {
        RocketMqMessage message = new RocketMqMessage("topic", "tag", "domain");

        String txId = message.generateTxId();

        assertThat(txId)
                .startsWith("topic:tag");
    }

    @Test
    public void testGenerateTxIdByDomainKeyExisted() {
        RocketMqMessage message = new RocketMqMessage("topic", "tag", "domain");
        message.setDomainKey("domainKey");

        String txId = message.generateTxId();

        assertThat(txId)
                .isEqualTo("topic:tagdomainKey");
    }

    @Test
    public void testCreateByIMessageEvent() {
        IMessageEvent event = new IMessageEvent() {
            @Override
            public String getTopic() {
                return "topic";
            }

            @Override
            public String[] getTags() {
                return new String[]{"tag"};
            }
        };

        RocketMqMessage message = new RocketMqMessage(event, "-dev", "-test", "domain");

        assertThat(message)
                .hasFieldOrPropertyWithValue("topic", "topic-dev")
                .hasFieldOrPropertyWithValue("tag", "tag-test")
                .hasFieldOrPropertyWithValue("domain", "domain");
    }
}
