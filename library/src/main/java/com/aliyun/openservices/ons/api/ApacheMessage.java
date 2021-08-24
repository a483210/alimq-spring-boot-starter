package com.aliyun.openservices.ons.api;

/**
 * 用于apache的延迟消息兼容
 *
 * @author Created by gold on 2021/8/16 21:27
 * @since 1.0.0
 */
public class ApacheMessage extends Message {
    public ApacheMessage() {
    }

    public ApacheMessage(String topic, String tag, String key, byte[] body) {
        super(topic, tag, key, body);
    }

    public ApacheMessage(String topic, String tags, byte[] body) {
        super(topic, tags, body);
    }

    @Override
    public void setStartDeliverTime(long value) {
        super.setStartDeliverTime(value);
        putSystemProperties("DELAY", String.valueOf((value - System.currentTimeMillis()) / 1000L));
    }
}
