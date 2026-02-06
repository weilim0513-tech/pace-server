package com.pace.server.global.error.exception;

import com.pace.server.global.common.code.ErrorCode;
import lombok.Getter;

@Getter
public class ExternalApiException extends BusinessException {

    private final String apiName;
    private final String originalMessage;

    public ExternalApiException(ErrorCode errorCode, String apiName, String originalMessage) {
        super(errorCode);
        this.apiName = apiName;
        this.originalMessage = originalMessage;
    }

    public ExternalApiException(String apiName, Throwable cause) {
        super(ErrorCode.EXTERNAL_API_ERROR, cause.getMessage());
        this.apiName = apiName;
        this.originalMessage = cause.getMessage();
    }
}
