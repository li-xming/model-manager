package com.example.datamodel.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应VO
 *
 * @author DataModel Team
 */
@Data
public class ResponseVO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应
     */
    public static <T> ResponseVO<T> success(T data) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setData(data);
        return response;
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ResponseVO<T> success() {
        return success(null);
    }

    /**
     * 失败响应
     */
    public static <T> ResponseVO<T> error(String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(500);
        response.setMessage(message);
        return response;
    }

    /**
     * 失败响应（自定义错误码）
     */
    public static <T> ResponseVO<T> error(Integer code, String message) {
        ResponseVO<T> response = new ResponseVO<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
}

