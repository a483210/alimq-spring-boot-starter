package cn.knowbox.book.alimq.config;

import lombok.Data;

/**
 * producer配置
 *
 * @author Created by gold on 2019/10/5 15:01
 */
@Data
public class ProducerProperty extends ConsumerProperty {

    private boolean orderEnabled;
    private boolean transactionEnabled;

}
