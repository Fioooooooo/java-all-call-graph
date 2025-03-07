package com.codemesh.resolver;

import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4MethodCall;
import com.adrninistrator.jacg.runner.RunnerWriteDb;
import com.adrninistrator.javacg2.conf.JavaCG2ConfigureWrapper;
import com.codemesh.resolver.builder.JacgConfigBuilder;
import com.codemesh.resolver.converter.ResolverConverter;
import com.codemesh.resolver.h2.H2Helper;
import com.codemesh.sdk.CodeMeshQueue;
import com.codemesh.sdk.entity.AddCallChainRequest;
import com.codemesh.sdk.util.ThreadLocalUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Fio
 * @date 2025/3/6
 */
@Slf4j
@RequiredArgsConstructor
public class MainHandler {

    private final ResolverArgs cliArgs;

    public void start() {
        log.info("CodeMesh Resolver start.");

        // set global params
        setGlobalParams();

        // jacg start
        JacgConfigBuilder jacgConfigBuilder = JacgConfigBuilder.args(cliArgs);
        ConfigureWrapper configureWrapper = jacgConfigBuilder.createConfigureWrapper();
        JavaCG2ConfigureWrapper javaCG2ConfigureWrapper = jacgConfigBuilder.createJavaCG2ConfigureWrapper();

        new RunnerWriteDb(javaCG2ConfigureWrapper, configureWrapper).run();

        // code mesh queue start
        CodeMeshQueue codeMeshQueue = CodeMeshQueue.getInstance();
        codeMeshQueue.start();

        try (H2Helper h2Helper = new H2Helper(cliArgs, configureWrapper)) {
            h2Helper.batchQueryMethodCall(10, this::batchConsumer);
        } catch (Exception e) {
            log.error("Exception when query method calls and do batch consumer", e);
            throw new RuntimeException(e);
        } finally {
            codeMeshQueue.finish();
        }
    }

    private void setGlobalParams() {
        ThreadLocalUtil.setWorkspaceId(cliArgs.getWorkspaceId());
        ThreadLocalUtil.setProjectId(cliArgs.getProjectId());
    }

    private void batchConsumer(List<WriteDbData4MethodCall> methodCallList) {
        CodeMeshQueue codeMeshQueue = CodeMeshQueue.getInstance();

        methodCallList.stream()
                .map(this::convert)
                .forEach(methodCall -> codeMeshQueue.put(CodeMeshQueue.Event.ADD_CALL_CHAIN, methodCall));
    }

    private AddCallChainRequest convert(WriteDbData4MethodCall methodCall) {
        ResolverConverter.MethodDTO callerMethodDto = ResolverConverter.extractMethod(methodCall.getCallerFullMethod());
        ResolverConverter.MethodDTO calleeMethodDto = ResolverConverter.extractMethod(methodCall.getCalleeFullMethod());

        return AddCallChainRequest.builder()
                .callerFilePath(callerMethodDto.getClassFullName())
                .callerFileName(callerMethodDto.getClassSimpleName())
                .callerModuleName(callerMethodDto.getModuleName())
                .callerMethodName(callerMethodDto.getMethodName())
                .callerMethodSignature(callerMethodDto.getMethodSignature())
                .callerLineNumber(methodCall.getCallerLineNumber())
                .calleeFilePath(calleeMethodDto.getClassFullName())
                .calleeFileName(calleeMethodDto.getClassSimpleName())
                .calleeModuleName(calleeMethodDto.getModuleName())
                .calleeMethodName(calleeMethodDto.getMethodName())
                .calleeMethodSignature(callerMethodDto.getMethodSignature())
                .build();
    }

}
