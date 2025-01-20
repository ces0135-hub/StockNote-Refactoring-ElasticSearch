package org.com.stocknote.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.com.stocknote.global.error.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
}
