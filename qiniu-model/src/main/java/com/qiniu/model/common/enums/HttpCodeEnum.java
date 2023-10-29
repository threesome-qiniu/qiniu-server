package com.qiniu.model.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum HttpCodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    // 登录
    NEED_LOGIN(401, "需要登录后操作"),
    NO_OPERATOR_AUTH(403, "无权限操作"),

    SYSTEM_ERROR(500, "出现错误"),

    REQUIRE_USERNAME(504, "必需填写用户名"),
    CONTENT_NOT_NULL(506, "评论内容不能为空"),
    FILE_TYPE_ERROR(507, "文件类型错误，请上传png文件"),
    NICKNAME_EXIST(512, "昵称已存在"),
    LOGIN_ERROR(505, "用户名或密码错误"),
    PASSWORD_ERROR(510, "密码错误"),

    USER_NOT_EXISTS(1000, "用户名不存在"),
    USERNAME_NOT_NULL(1001, "用户名不能为空"),
    NICKNAME_NOT_NULL(1002, "昵称不能为空"),
    PASSWORD_NOT_NULL(1003, "密码不能为空"),
    EMAIL_NOT_NULL(1004, "邮箱不能为空"),

    USERNAME_EXIST(1011, "用户名已存在"),
    PHONENUMBER_EXIST(1012, "手机号已存在"),
    EMAIL_EXIST(1013, "邮箱已存在"),

    TELEPHONE_VALID_ERROR(1020, "手机号码格式错误"),
    EMAIL_VALID_ERROR(1021, "邮箱格式错误"),

    CONFIRM_PASSWORD_NOT_MATCH(1020, "两次密码不一致"),
    ;

    int code;
    String msg;

}
