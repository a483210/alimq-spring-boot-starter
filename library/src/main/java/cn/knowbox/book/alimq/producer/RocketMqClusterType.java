package cn.knowbox.book.alimq.producer;

import lombok.Getter;

/**
 * rocketMq 集群类型
 *
 * @author Created by gold on 2021/8/17 11:29
 * @since 1.0.0
 */
@Getter
public enum RocketMqClusterType {

    /**
     * 阿里云集群
     */
    ALIYUN(0),
    /**
     * apache集群
     */
    APACHE(1);

    private final int type;

    RocketMqClusterType(int code) {
        this.type = code;
    }

    /**
     * 格式化集群类型，未找到时默认为{@link RocketMqClusterType#ALIYUN}
     *
     * @param type 类型
     * @return 枚举
     */
    public static RocketMqClusterType valueOf(int type) {
        for (RocketMqClusterType item : RocketMqClusterType.values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        return ALIYUN;
    }
}
