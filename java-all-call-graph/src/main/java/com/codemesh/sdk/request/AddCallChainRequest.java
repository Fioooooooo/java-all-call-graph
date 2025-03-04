package com.codemesh.sdk.request;

import lombok.Builder;
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
public class AddCallChainRequest extends CodeMeshRequest {

    private static final long serialVersionUID = -2997876208106244483L;

    /**
     * 调用者文件路径/全限定名
     */
    private String callerFilePath;

    /**
     * 调用者文件名
     */
    private String callerFileName;

    /**
     * 调用者模块/类名
     */
    private String callerModuleName;

    /**
     * 调用者方法名
     */
    private String callerMethodName;

    /**
     * 调用者方法签名
     */
    private String callerMethodSignature;

    /**
     * 调用者开始行
     */
    private Integer callerStartLine;

    /**
     * 调用者结束行
     */
    private Integer callerEndLine;

    /**
     * 被调用者文件路径/全限定名
     */
    private String calleeFilePath;

    /**
     * 被调用者文件名
     */
    private String calleeFileName;

    /**
     * 被调用者模块/类名
     */
    private String calleeModuleName;

    /**
     * 被调用者方法名
     */
    private String calleeMethodName;

    /**
     * 被调用者方法签名
     */
    private String calleeMethodSignature;

    /**
     * 被调用者开始行
     */
    private Integer calleeStartLine;

    /**
     * 被调用者结束行
     */
    private Integer calleeEndLine;

    /**
     * 编程语言
     */
    @Builder.Default
    private String language = "java";

    /**
     * 调用类型（如方法调用、类实例化等）
     */
    private String callType;

    /**
     * 调用上下文
     */
    private String context;

}
