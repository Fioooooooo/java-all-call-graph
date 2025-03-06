package com.codemesh.resolver;

import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.runner.RunnerWriteDb;
import com.adrninistrator.javacg2.conf.JavaCG2ConfigureWrapper;
import com.codemesh.resolver.builder.JacgConfigBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fio
 * @date 2025/3/6
 */
@Slf4j
@RequiredArgsConstructor
public class MainHandler {

    private final ResolverArgs cliArgs;

    public void start() {
        if (cliArgs.isVerbose()) {
            log.info("CodeMesh Resolver start.");
        }

//        CodeMeshQueue codeMeshQueue = CodeMeshQueue.getInstance();
//        codeMeshQueue.start();

        createJacgRunner().run();

//        codeMeshQueue.finish();
    }

    private RunnerWriteDb createJacgRunner() {
        JacgConfigBuilder jacgConfigBuilder = JacgConfigBuilder.args(cliArgs);
        ConfigureWrapper configureWrapper = jacgConfigBuilder.createConfigureWrapper();
        JavaCG2ConfigureWrapper javaCG2ConfigureWrapper = jacgConfigBuilder.createJavaCG2ConfigureWrapper();

        return new RunnerWriteDb(javaCG2ConfigureWrapper, configureWrapper);
    }

}
