package com.codemesh.resolver;

import com.codemesh.sdk.CodeMeshQueue;
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

        CodeMeshQueue codeMeshQueue = CodeMeshQueue.getInstance();
        codeMeshQueue.start();

    }

}
