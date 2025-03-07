package com.codemesh.resolver.converter;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Fio
 * @date 2025/3/7
 */
public class ResolverConverter {

    public static MethodDTO extractMethod(String fullMethod) {
        // com.codemesh.sdk.api.ApiUrl:getFullUrl(java.lang.String)
        String[] split = fullMethod.split(":");

        String classFullName = split[0];
        String classSimpleName = classFullName.substring(classFullName.lastIndexOf(".") + 1);

        String methodSplit = split[1];
        String methodName = methodSplit.split("\\(")[0];

        return new MethodDTO()
                .setClassFullName(classFullName)
                .setClassSimpleName(classSimpleName)
                .setModuleName("")
                .setMethodName(methodName)
                .setMethodSignature(fullMethod);
    }

    @Data
    @Accessors(chain = true)
    public static class MethodDTO {

        private String classFullName;

        private String classSimpleName;

        private String moduleName;

        private String methodName;

        private String methodSignature;

    }

}
