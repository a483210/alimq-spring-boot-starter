package cn.knowbox.book.alimq.consumer;

import org.springframework.lang.NonNull;

/**
 * 消息监听者
 *
 * @author Created by gold on 2019/10/4 15:23
 */
public interface ConsumerListener<T> {

    /**
     * 消费
     *
     * @param message 消息
     */
    void onMessage(@NonNull T message);

}
