package com.hxq.order.response;

import com.hxq.order.constant.ResultCodeConstants;
import lombok.Data;

/**
 * 通用返回结果类
 */
@Data
public class Result<T> {
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功结果（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(ResultCodeConstants.SUCCESS);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    /**
     * 失败结果
     */
    public static <T> Result<T> failure() {
        return failure(ResultCodeConstants.FAILURE, "操作失败");
    }

    /**
     * 失败结果（带消息）
     */
    public static <T> Result<T> failure(String message) {
        return failure(ResultCodeConstants.FAILURE, message);
    }

    /**
     * 失败结果（带状态码和消息）
     */
    public static <T> Result<T> failure(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}