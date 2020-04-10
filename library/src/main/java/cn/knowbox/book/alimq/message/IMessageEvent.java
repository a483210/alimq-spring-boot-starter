package cn.knowbox.book.alimq.message;

/**
 * 枚举
 */
public interface IMessageEvent {

    /**
     * 返回topic
     */
    String getTopic();

    /**
     * 返回tags
     */
    String[] getTags();

}
