package com.codemesh.sdk.request;

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
public class LogReportRequest extends CodeMeshRequest {
    private static final long serialVersionUID = -4636524107269128836L;
}
