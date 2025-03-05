package com.codemesh.sdk.entity;

import com.codemesh.resolver.util.ThreadLocalUtil;
import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.util.JsonUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Fio
 * @date 2025/3/4
 */
@Data
@SuperBuilder
public class CodeMeshRequest implements Serializable {

    private static final long serialVersionUID = 6424880880797663631L;

    /**
     * workspace ID
     */
    @Builder.Default
    private Long workspaceId = ThreadLocalUtil.getWorkspaceId();

    /**
     * project ID
     */
    @Builder.Default
    private Long projectId = ThreadLocalUtil.getProjectId();

    /**
     * request name for sdk
     */
    @JsonIgnore
    private CodeMeshQueue.Event requestEvent;

    @Override
    public String toString() {
        return JsonUtil.toJsonString(this);
    }

}
