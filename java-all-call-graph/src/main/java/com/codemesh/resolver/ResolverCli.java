package com.codemesh.resolver;

import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * CodeMesh Resolver CLI 入口类
 */
@Command(
        name = "code-mesh-resolver-java",
        version = "1.0.0",
        description = "CodeMesh Java Code Resolver",
        mixinStandardHelpOptions = true  // 添加 --help 和 --version 选项
)
@Slf4j
public class ResolverCli extends ResolverArgs implements Callable<Integer> {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ResolverCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            new MainHandler(this).start();
            return 0;
        } catch (Exception e) {
            log.error("Error while running CodeMesh Resolver", e);
            return 1;
        }
    }
}
