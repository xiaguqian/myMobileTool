package com.datamodel.config;

import com.datamodel.common.Result;
import org.apache.shiro.authc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Void> handleException(Exception e) {
        logger.error("系统异常", e);
        return Result.error(500, e.getMessage());
    }

    @ExceptionHandler(UnknownAccountException.class)
    @ResponseBody
    public Result<Void> handleUnknownAccountException(UnknownAccountException e) {
        logger.warn("登录失败: 账号不存在", e);
        return Result.error(401, "账号不存在");
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    @ResponseBody
    public Result<Void> handleIncorrectCredentialsException(IncorrectCredentialsException e) {
        logger.warn("登录失败: 密码错误", e);
        return Result.error(401, "密码错误");
    }

    @ExceptionHandler(LockedAccountException.class)
    @ResponseBody
    public Result<Void> handleLockedAccountException(LockedAccountException e) {
        logger.warn("登录失败: 账号被锁定", e);
        return Result.error(403, "账号已被锁定");
    }

    @ExceptionHandler(ExcessiveAttemptsException.class)
    @ResponseBody
    public Result<Void> handleExcessiveAttemptsException(ExcessiveAttemptsException e) {
        logger.warn("登录失败: 登录次数过多", e);
        return Result.error(429, "登录次数过多，请稍后再试");
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Result<Void> handleAuthenticationException(AuthenticationException e) {
        logger.warn("登录失败: 认证异常", e);
        return Result.error(401, "认证失败: " + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<Void> handleRuntimeException(RuntimeException e) {
        logger.error("运行时异常", e);
        return Result.error(e.getMessage());
    }
}
