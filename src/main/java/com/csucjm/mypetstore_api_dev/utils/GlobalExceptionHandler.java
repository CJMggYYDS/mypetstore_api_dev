package com.csucjm.mypetstore_api_dev.utils;

import com.csucjm.mypetstore_api_dev.common.CommonResponse;
import com.csucjm.mypetstore_api_dev.common.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    //接口请求参数错误的异常处理
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public CommonResponse<Object> handleMissingParameterException(MissingServletRequestParameterException exception) {
        return CommonResponse.createForError(ResponseCode.ARGUMENT_ILLEGAL.getCode(), ResponseCode.ARGUMENT_ILLEGAL.getMsg());
    }

    //非对象参数校验
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Object> handleValidationException(ConstraintViolationException exception) {
        return CommonResponse.createForError(exception.getMessage());
    }

    //对象参数校验
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public CommonResponse<Object> handleValidException(MethodArgumentNotValidException exception) {
        return CommonResponse.createForError(
                ResponseCode.ARGUMENT_ILLEGAL.getCode(),
                formatValidErrorsMessage(exception.getAllErrors())
        );
    }

    private String formatValidErrorsMessage(List<ObjectError> errorList) {
        StringBuffer errorMessage = new StringBuffer();
        errorList.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(","));
        errorMessage.deleteCharAt(errorMessage.length() - 1);
        return errorMessage.toString();
    }
}
