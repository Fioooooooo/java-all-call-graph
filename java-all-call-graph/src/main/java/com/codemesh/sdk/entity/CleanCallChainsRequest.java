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
public class CleanCallChainsRequest extends CodeMeshRequest {

    private static final long serialVersionUID = 5730799454901710799L;

}
