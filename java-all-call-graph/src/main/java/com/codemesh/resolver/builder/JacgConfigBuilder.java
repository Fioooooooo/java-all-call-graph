package com.codemesh.resolver.builder;

import com.adrninistrator.jacg.common.JACGConstants;
import com.adrninistrator.jacg.common.enums.OutputDetailEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.conf.enums.ConfigDbKeyEnum;
import com.adrninistrator.jacg.conf.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.conf.enums.OtherConfigFileUseListEnum;
import com.adrninistrator.jacg.conf.enums.OtherConfigFileUseSetEnum;
import com.adrninistrator.javacg2.common.JavaCG2CommonNameConstants;
import com.adrninistrator.javacg2.common.JavaCG2Constants;
import com.adrninistrator.javacg2.conf.JavaCG2ConfigureWrapper;
import com.adrninistrator.javacg2.conf.enums.JavaCG2ConfigKeyEnum;
import com.adrninistrator.javacg2.conf.enums.JavaCG2OtherConfigFileUseListEnum;
import com.adrninistrator.javacg2.conf.enums.JavaCG2OtherConfigFileUseSetEnum;
import com.adrninistrator.javacg2.el.enums.JavaCG2ElAllowedVariableEnum;
import com.adrninistrator.javacg2.el.enums.JavaCG2ElConfigEnum;
import com.codemesh.resolver.ResolverArgs;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author Fio
 * @date 2025/3/6
 */
public class JacgConfigBuilder {

    private final ResolverArgs resolverArgs;

    private JacgConfigBuilder(ResolverArgs resolverArgs) {
        this.resolverArgs = resolverArgs;
    }

    public static JacgConfigBuilder args(ResolverArgs resolverArgs) {
        return new JacgConfigBuilder(resolverArgs);
    }

    public ConfigureWrapper createConfigureWrapper() {
        // java-all-call-graph的配置
        ConfigureWrapper configureWrapper = new ConfigureWrapper();
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_APP_NAME, resolverArgs.getProjectId());
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_CALL_GRAPH_OUTPUT_DETAIL, OutputDetailEnum.ODE_1.getDetail());
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_THREAD_NUM, "20");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_IGNORE_DUP_CALLEE_IN_ONE_CALLER, Boolean.FALSE.toString());
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_DB_INSERT_BATCH_SIZE, "1000");
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_CHECK_JAR_FILE_UPDATED, Boolean.TRUE.toString());
        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_OUTPUT_ROOT_PATH, "");

        // H2
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_USE_H2, Boolean.TRUE.toString());
        configureWrapper.setMainConfig(ConfigDbKeyEnum.CDKE_DB_H2_FILE_PATH, "./build/jacg_h2db_rbc");

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_FIND_STACK_KEYWORD_4EE,
                JACGConstants.CALLEE_FLAG_ENTRY_NO_TAB,
                JavaCG2CommonNameConstants.METHOD_NAME_INIT
        );

        configureWrapper.setOtherConfigList(OtherConfigFileUseListEnum.OCFULE_FIND_STACK_KEYWORD_4ER,
                System.class.getSimpleName() + JavaCG2Constants.FLAG_COLON,
                Deprecated.class.getName()
        );

        configureWrapper.setMainConfig(ConfigKeyEnum.CKE_CALL_GRAPH_GEN_SEPARATE_STACK, Boolean.TRUE.toString());

        return configureWrapper;
    }

    public JavaCG2ConfigureWrapper createJavaCG2ConfigureWrapper() {
        JavaCG2ConfigureWrapper javaCG2ConfigureWrapper = new JavaCG2ConfigureWrapper();
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_PARSE_METHOD_CALL_TYPE_VALUE, Boolean.TRUE.toString());
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_FIRST_PARSE_INIT_METHOD_TYPE, Boolean.TRUE.toString());
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_ANALYSE_FIELD_RELATIONSHIP, Boolean.TRUE.toString());
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_LOG_METHOD_SPEND_TIME, Boolean.TRUE.toString());
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_CONTINUE_WHEN_ERROR, Boolean.FALSE.toString());
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_OUTPUT_FILE_EXT, JavaCG2Constants.EXT_MD);
        javaCG2ConfigureWrapper.setMainConfig(JavaCG2ConfigKeyEnum.CKE_ANALYSE_FIELD_RELATIONSHIP, Boolean.TRUE.toString());

        // 解析的 jar 目录
        javaCG2ConfigureWrapper.setOtherConfigList(JavaCG2OtherConfigFileUseListEnum.OCFULE_JAR_DIR, resolverArgs.getJarPath());

        // 指定解析范围
        // 语法文档：https://github.com/Adrninistrator/java-callgraph2/blob/main/src/main/resources/el_example.md
        if (CollectionUtils.isNotEmpty(resolverArgs.getAllowedClassPrefix())) {
            String classPrefix = resolverArgs.getAllowedClassPrefix().stream()
                    .map(item -> String.format("'%s'", item)).collect(Collectors.joining(", "));
            javaCG2ConfigureWrapper.setElConfigText(JavaCG2ElConfigEnum.ECE_PARSE_IGNORE_CLASS,
                    "!(string.startsWithAny(class_name, " + classPrefix + "))" +
                            " || !(string.startsWithAny(package_name, "+classPrefix+"))"
            );
        }

        if (CollectionUtils.isNotEmpty(resolverArgs.getAllowedMethodPrefix())) {
            String methodPrefix = resolverArgs.getAllowedMethodPrefix().stream()
                    .map(item -> String.format("'%s'", item)).collect(Collectors.joining(", "));
            javaCG2ConfigureWrapper.setElConfigText(JavaCG2ElConfigEnum.ECE_PARSE_IGNORE_METHOD,
                    "!(string.startsWithAny(method_name, " + methodPrefix + "))"
            );
        }

        return javaCG2ConfigureWrapper;
    }
}
