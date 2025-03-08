package com.codemesh.resolver;


import com.codemesh.resolver.cli.ResolverArgs;
import com.codemesh.resolver.cli.VersionProvider;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

/**
 * CodeMesh Resolver CLI 入口类
 */
@Command(
        name = "code-mesh-resolver-java",
        versionProvider = VersionProvider.class,
        description = "CodeMesh Java Code Resolver - Scan Java class files and generate method call graph%n",
        headerHeading = "Usage Guide:%n%n",
        synopsisHeading = "Command Format:%n",
        descriptionHeading = "Description:%n",
        parameterListHeading = "Parameters:%n",
        optionListHeading = "Options:%n",
        footerHeading = "Examples:%n",
        footer = "  java -jar code-mesh-resolver-java.jar --workspace-id=myworkspace --project-id=myproject --jar-path=/path/to/jars/dir --allowed-class-prefix=com.example,org.sample%n",
        mixinStandardHelpOptions = true,  // Add --help and --version options
        sortOptions = false
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
