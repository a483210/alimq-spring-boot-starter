package cn.knowbox.book.alimq.properties;

import com.aliyun.openservices.ons.api.PropertyValueConst;
import lombok.Data;

/**
 * consumer配置
 *
 * @author Created by gold on 2019/10/4 22:59
 */
@Data
public class ConsumerProperty {

    /**
     * 是否启用消费者
     */
    private boolean enabled = true;

    /**
     * GroupID的最大消息重试次数为20次
     * <p>
     * 默认为5次
     */
    private int maxReconsumeTimes = 5;
    /**
     * 消费模式, 默认集群消费
     *
     * @see PropertyValueConst
     */
    private String messageModel = PropertyValueConst.CLUSTERING;
    /**
     * 消费者线程数量，如果为0则使用默认值20
     */
    private int threadNums = 5;
    /**
     * 消费者日志
     */
    private boolean logging = false;
    /**
     * 消息轨迹开关，关闭消息轨迹有助于减少线程数
     */
    private boolean traceSwitch = false;

}
