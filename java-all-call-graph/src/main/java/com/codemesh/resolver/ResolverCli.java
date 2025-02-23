package com.codemesh.resolver;

import com.codemesh.resolver.args.ResolverArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ResolverCli extends ResolverArgs implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(ResolverCli.class);

    public static void main(String[] args) {
        int exitCode = new CommandLine(new ResolverCli()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            // 配置日志级别
            if (isVerbose()) {
                // TODO: 设置详细日志级别
                logger.info("Verbose mode enabled");
            }

            logger.info("Source directory: {}", getSourceDir());
            logger.info("Output directory: {}", getOutputDir());

            // TODO: 实现具体的业务逻辑
            return 0; // 成功返回 0
        } catch (Exception e) {
            logger.error("Error while running CodeMesh Resolver", e);
            return 1; // 失败返回 1
        }
    }
}
