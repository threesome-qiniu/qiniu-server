package com.qiniu.common.exception;

import com.qiniu.common.domain.R;
import com.qiniu.model.common.enums.HttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice  //控制器增强类
public class ExceptionCatch {

    /**
     * 处理不可控异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public R<?> exception(Exception e) {
        log.error("catch exception:{}", e.getMessage());
        return R.fail(HttpCodeEnum.SYSTEM_ERROR);
    }

    /**
     * 处理可控异常  自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public R<?> exception(CustomException e) {
        log.error("catch exception:{}", e.getMessage());
        return R.fail(e.getHttpCodeEnum());
    }
}
