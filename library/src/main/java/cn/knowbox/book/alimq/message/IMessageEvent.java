package cn.knowbox.book.alimq.message;

/**
 * 枚举
 */
public interface IMessageEvent {

    String getTopic();

    String[] getTags();

}
