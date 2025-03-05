package com.codemesh.sdk.entity;

import lombok.Data;

/**
 * @author Fio
 * @date 2025/3/5
 */
@Data
public class CodeMeshResponse {

    /**
     * 状态码，0表示成功
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 是否成功
     */
    private Boolean success;

}
