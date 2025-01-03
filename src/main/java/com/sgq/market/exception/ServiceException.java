package com.sgq.market.exception;


import com.sgq.market.constants.ResultCode;
import lombok.Data;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2023/7/8 19:38
 */
@Data
public class ServiceException extends RuntimeException {
    ResultCode resultCode;

    public ServiceException(ResultCode resultCode) {
        super(resultCode.getName());
        this.resultCode = resultCode;
    }

    public ServiceException(ResultCode resultCode, String msg) {
        super(msg);
        resultCode.setName(msg);
        this.resultCode = resultCode;

    }
}
