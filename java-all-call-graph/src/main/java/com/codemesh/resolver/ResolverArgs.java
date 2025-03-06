package com.codemesh.resolver;

import lombok.Getter;
import picocli.CommandLine.Option;

/**
 * 命令行参数
 */
@Getter
public class ResolverArgs {

    @Option(
            names = {"-v", "--verbose"},
            description = "显示详细日志"
    )
    private boolean verbose;

}
