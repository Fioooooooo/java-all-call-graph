package com.codemesh.sdk.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * @author Fio
 * @date 2025/3/4
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TaskStatusRequest extends CodeMeshRequest {

    private static final long serialVersionUID = -9079266413373565452L;

    /**
     * 任务状态
     */
    private StatusEnum status;

    public enum StatusEnum {
        START, FINISH;
    }

}
