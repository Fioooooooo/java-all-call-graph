package com.codemesh.sdk.request;

import lombok.Data;

/**
 * @author Fio
 * @date 2025/3/5
 */
@Data
public class CodeMeshResponse {

    private Integer code;

    private String message;

    private Boolean success;

}
