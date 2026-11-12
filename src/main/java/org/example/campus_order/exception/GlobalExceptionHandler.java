package org.example.campus_order.exception;

import org.example.campus_order.utils.BusinessException;
import org.example.campus_order.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * 全局统一异常处理器
 * 所有Controller抛出的异常统一拦截、标准化返回
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义业务异常（主动抛出的业务校验错误，优先级最高）
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK) // 业务错误属于正常业务反馈，不用500
    public Result<String> handleBusinessException(BusinessException e) {
        return Result.error(e.getMessage());
    }

    /**
     * @RequestBody 参数校验异常（@NotBlank、@NotNull等注解）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + "：" + err.getDefaultMessage())
                .findFirst()
                .orElse("请求参数格式非法");
        return Result.error(msg);
    }

    /**
     * 缺少必填请求参数（@RequestParam缺失）
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error("缺少必填参数：" + e.getParameterName());
    }

    /**
     * 非法参数（手动抛出IllegalArgumentException）
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleIllegalArg(IllegalArgumentException e) {
        return Result.error("参数错误：" + e.getMessage());
    }

    /**
     * 文件上传大小超出限制
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<String> handleUploadSize(MaxUploadSizeExceededException e) {
        return Result.error("上传文件大小超出限制，请压缩后重试");
    }

    /**
     * 兜底：所有未匹配的未知系统异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<String> handleGlobalException(Exception e) {
        // 生产环境可注释打印堆栈，使用日志框架记录
        e.printStackTrace();
        return Result.error("服务器内部异常，请联系管理员");
    }
}