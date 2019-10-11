package cn.knowbox.book.alimq.error;

import com.aliyun.openservices.ons.api.OnExceptionContext;

import lombok.Getter;

/**
 * mq异常
 *
 * @author Created by gold on 2019/10/4 16:10
 */
@Getter
public class RocketMqException extends RuntimeException {

    private OnExceptionContext exceptionContext;

    public RocketMqException(String message) {
        super(message);
    }

    public RocketMqException(OnExceptionContext exceptionContext) {
        super(exceptionContext.getException());
        this.exceptionContext = exceptionContext;
    }

}
