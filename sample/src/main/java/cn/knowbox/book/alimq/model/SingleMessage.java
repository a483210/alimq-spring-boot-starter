package cn.knowbox.book.alimq.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 私聊消息
 *
 * @author Created by gold on 2019/10/5 14:33
 */
@Data
public class SingleMessage {

    private String msgId;
    private String content;

}
