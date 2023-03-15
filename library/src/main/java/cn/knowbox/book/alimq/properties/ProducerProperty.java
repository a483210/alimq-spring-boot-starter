package cn.knowbox.book.alimq.properties;

import lombok.Data;

/**
 * producer配置
 *
 * @author Created by gold on 2019/10/5 15:01
 */
@Data
public class ProducerProperty {

    /**
     * 是否启用生产者
     */
    private boolean enabled = true;
    /**
     * 是否启用顺序生产者
     */
    private boolean orderEnabled;
    /**
     * 是否启用事务生产者
     */
    private boolean transactionEnabled;

    /**
     * 生产者的groupId
     */
    private String groupId;

    /**
     * 生产者日志
     */
    private boolean logging = false;

}
