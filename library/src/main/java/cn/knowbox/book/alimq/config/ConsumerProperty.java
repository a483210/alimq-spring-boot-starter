package cn.knowbox.book.alimq.config;

import lombok.Data;

/**
 * consumer配置
 *
 * @author Created by gold on 2019/10/4 22:59
 */
@Data
public class ConsumerProperty {

    private boolean enabled;
    private String groupId;

}
