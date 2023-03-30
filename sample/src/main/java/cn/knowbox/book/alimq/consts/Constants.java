package cn.knowbox.book.alimq.consts;

/**
 * Constants
 *
 * @author Created by gold on 2021/8/24 11:36
 * @since 1.0.0
 */
public interface Constants {

    interface Topic {
        /**
         * topic 私聊
         */
        String SINGLE = "singleMessage";
        /**
         * topic 延迟
         */
        String DELAY = "delayMessage";
        /**
         * topic 顺序
         */
        String ORDER = "orderMessage";
        /**
         * topic 事务
         */
        String TRANSACTION = "transactionMessage";
    }

    interface GroupId {
        /**
         * groupId 私聊消息
         */
        String SINGLE = "GID_single_message";
        /**
         * groupId 列表消息
         */
        String SINGLE_LIST = "GID_list_single_message";
        /**
         * groupId v2消息
         */
        String SINGLE_V2 = "GID_single_message_v2";
        /**
         * groupId 延迟消息
         */
        String DELAY = "GID_delay_message";
        /**
         * groupId 顺序消息
         */
        String ORDER = "GID_order_message";
        /**
         * groupId 事务消息
         */
        String TRANSACTION = "GID_transaction_message";
    }

    interface Tag {
        /**
         * tag 私聊消息
         */
        String SINGLE = "single";
        /**
         * tag 列表消息
         */
        String LIST = "list";
        /**
         * tag v2私聊消息
         */
        String SINGLE_V2 = "singlev2";
        /**
         * tag 延迟消息
         */
        String DELAY = "delay";
        /**
         * tag 顺序消息
         */
        String ORDER = "order";
        /**
         * tag 事务消息
         */
        String TRANSACTION = "transaction";
    }
}
