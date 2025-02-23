package com.codemesh.resolver.args;

import lombok.Getter;
import picocli.CommandLine.Option;

import java.io.File;

/**
 * 通用命令行参数
 */
@Getter
public class ResolverArgs {
    @Option(
            names = {"-s", "--source"},
            description = "源代码目录",
            required = true
    )
    protected File sourceDir;

    @Option(
            names = {"-o", "--output"},
            description = "输出目录",
            defaultValue = "output"
    )
    protected File outputDir;

    @Option(
            names = {"-v", "--verbose"},
            description = "显示详细日志"
    )
    protected boolean verbose;

}
