package com.codemesh.resolver;

import lombok.Getter;
import picocli.CommandLine.Option;

import java.util.List;

/**
 * 命令行参数
 */
@Getter
public class ResolverArgs {

    @Option(names = {"-v", "--verbose"}, description = "显示详细日志")
    private boolean verbose;

    @Option(names = {"--workspace-id"}, description = "WorkspaceID")
    private String workspaceId;

    @Option(names = {"--project-id"}, description = "ProjectID")
    private String projectId;

    @Option(names = {"--jar-path"}, description = "解析的 jar 目录，或 jar 包路径")
    private String jarPath;

    @Option(names = {"--allowed-class-prefix"}, description = "解析的类前缀，使用英文逗号分隔")
    private List<String> allowedClassPrefix;

    @Option(names = {"--allowed-method-prefix"}, description = "解析的方法前缀，使用英文逗号分隔")
    private List<String> allowedMethodPrefix;

}
