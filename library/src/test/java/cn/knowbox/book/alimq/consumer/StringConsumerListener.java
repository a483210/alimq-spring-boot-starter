package cn.knowbox.book.alimq.consumer;

import org.springframework.lang.NonNull;

/**
 * StringConsumerListener
 *
 * @author Created by gold on 2023/3/17 15:37
 * @since 1.0.0
 */
public class StringConsumerListener implements ConsumerListener<String> {

    @Override
    public void onMessage(@NonNull String message) {
    }
}