package com.codemesh.resolver.cli;

import lombok.Getter;
import picocli.CommandLine.Option;

import java.util.List;

/**
 * 命令行参数
 */
@Getter
public class ResolverArgs {

    @Option(names = {"-v", "--verbose"},
            description = "Display detailed logs, including more information during the parsing process",
            paramLabel = "",
            arity = "0",
            order = 1)
    private boolean verbose;

    @Option(names = {"--workspace-id"},
            description = "Workspace ID, used to identify the workspace that the code belongs to",
            required = true,
            paramLabel = "<workspace>",
            order = 2)
    private String workspaceId;

    @Option(names = {"--project-id"},
            description = "Project ID, used to identify the project that the code belongs to",
            required = true,
            paramLabel = "<project>",
            order = 3)
    private String projectId;

    @Option(names = {"--jar-path"},
            description = "The directory of jar files or path to a jar file, specifies the location of Java class files to be analyzed",
            paramLabel = "<path>",
            order = 4)
    private String jarPath;

    @Option(names = {"--allowed-class-prefix"},
            description = "Class prefixes to be parsed, limits the scope of classes to be analyzed, multiple prefixes separated by commas, e.g.: com.example,org.sample",
            split = ",",
            paramLabel = "<prefix>[,<prefix>...]",
            order = 5)
    private List<String> allowedClassPrefix;

}
