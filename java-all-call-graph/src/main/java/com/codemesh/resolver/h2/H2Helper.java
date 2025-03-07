package com.codemesh.resolver.h2;

import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.conf.enums.ConfigDbKeyEnum;
import com.adrninistrator.jacg.conf.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.dboper.DbInitializer;
import com.adrninistrator.jacg.dboper.DbOperator;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4MethodCall;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import com.codemesh.resolver.ResolverArgs;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Fio
 * @date 2025/3/7
 */
public class H2Helper implements AutoCloseable {

    private ResolverArgs cliArgs;

    private DbOperator dbOperator;

    private String appName;

    private String tableSuffix;

    public H2Helper(ResolverArgs resolverArgs, ConfigureWrapper configureWrapper) {
        this.cliArgs = resolverArgs;
        this.dbOperator = DbInitializer.genDbOperWrapper(configureWrapper, new H2Helper()).getDbOperator();
        this.appName = configureWrapper.getMainConfig(ConfigKeyEnum.CKE_APP_NAME);
        this.tableSuffix = configureWrapper.getMainConfig(ConfigDbKeyEnum.CDKE_DB_TABLE_SUFFIX);
    }

    private H2Helper() {

    }

    public void batchQueryMethodCall(int batchSize, Consumer<List<WriteDbData4MethodCall>> batchConsumer) {
        int count = countMethodCall();
        int batchNum = (count + batchSize - 1) / batchSize;

        for (int i = 0; i < batchNum; i++) {
            int lastId = i * batchSize;
            String sql = "SELECT * FROM " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName()
                    + " WHERE " + DC.MC_CALL_ID + " > " + lastId
                    + " LIMIT " + batchSize;
            List<WriteDbData4MethodCall> methodCallList = dbOperator.queryList(replaceSql(sql), WriteDbData4MethodCall.class);
            if (CollectionUtils.isNotEmpty(methodCallList)) {
                batchConsumer.accept(methodCallList);
            }
        }
    }

    private int countMethodCall() {
        String sql = "SELECT MAX(" + DC.MC_CALL_ID + ") FROM " + DbTableInfoEnum.DTIE_METHOD_CALL.getTableName();
        return dbOperator.queryObjectOneColumn(replaceSql(sql), Integer.class);
    }

    private String replaceSql(String sql) {
        return JACGSqlUtil.replaceFlagInSql(sql, appName, tableSuffix);
    }

    @Override
    public void close() throws Exception {
        dbOperator.close();
    }

}
