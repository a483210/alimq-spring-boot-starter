package cn.knowbox.book.alimq.config;

import lombok.Data;

/**
 * producer配置
 *
 * @author Created by gold on 2019/10/5 15:01
 */
@Data
public class ProducerProperty {

    private String groupId;
    private boolean enabled;
    private boolean orderEnabled;
    private boolean transactionEnabled;

}
