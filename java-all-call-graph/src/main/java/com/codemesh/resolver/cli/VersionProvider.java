package com.codemesh.resolver.cli;

import com.codemesh.sdk.config.CodeMeshVersion;
import picocli.CommandLine.IVersionProvider;

/**
 * Picocli 版本信息提供器
 * 用于动态提供版本信息给 picocli 命令行工具
 *
 * @author Fio
 * @date 2025/3/8
 */
public class VersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
        return new String[] {
                "code-mesh-resolver-java version: " + CodeMeshVersion.getVersion(),
                "Build time: " + CodeMeshVersion.getBuildTime()
        };
    }
}
