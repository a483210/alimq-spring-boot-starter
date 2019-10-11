package cn.knowbox.book.alimq.consumer;

import org.springframework.lang.NonNull;

/**
 * 消息监听者
 *
 * @author Created by gold on 2019/10/4 15:23
 */
public interface RocketMqListener<T> {

    void onMessage(@NonNull T message);

}
