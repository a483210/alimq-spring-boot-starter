package cn.knowbox.book.alimq.error;

/**
 * 消息为空异常
 *
 * @author Created by gold on 2023/3/17 15:20
 * @since 1.0.0
 */
public class MessageNullException extends Exception {

    public MessageNullException(String message) {
        super(message);
    }
}
