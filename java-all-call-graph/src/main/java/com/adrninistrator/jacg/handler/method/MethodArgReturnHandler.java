package com.adrninistrator.jacg.handler.method;

import com.adrninistrator.jacg.common.DC;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.common.enums.SqlKeyEnum;
import com.adrninistrator.jacg.conf.ConfigureWrapper;
import com.adrninistrator.jacg.dboper.DbOperWrapper;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4MethodArgGenericsType;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4MethodReturnArgSeq;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4MethodReturnCallId;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4MethodReturnGenericsType;
import com.adrninistrator.jacg.handler.base.BaseHandler;
import com.adrninistrator.jacg.handler.dto.genericstype.GenericsTypeValue;
import com.adrninistrator.jacg.handler.dto.genericstype.MethodArgGenericsTypeInfo;
import com.adrninistrator.jacg.util.JACGSqlUtil;
import com.adrninistrator.jacg.util.JACGUtil;
import com.adrninistrator.javacg2.common.JavaCG2Constants;
import com.adrninistrator.javacg2.util.JavaCG2Util;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author adrninistrator
 * @date 2023/11/5
 * @description: 处理方法参数与返回的类
 */
public class MethodArgReturnHandler extends BaseHandler {

    public MethodArgReturnHandler(ConfigureWrapper configureWrapper) {
        super(configureWrapper);
    }

    public MethodArgReturnHandler(DbOperWrapper dbOperWrapper) {
        super(dbOperWrapper);
    }

    /**
     * 根据方法HASH+长度查询对应的方法参数泛型类型
     *
     * @param methodHash
     * @return
     */
    public MethodArgGenericsTypeInfo queryArgsGenericsTypeInfo(String methodHash) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MAGT_QUERY;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns(DC.MAGT_ARG_SEQ, DC.MAGT_TYPE, DC.MAGT_GENERICS_TYPE) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_ARG_GENERICS_TYPE.getTableName() +
                    " where " + DC.MAGT_METHOD_HASH + " = ?" +
                    " order by " + JACGSqlUtil.joinColumns(DC.MAGT_ARG_SEQ, DC.MAGT_TYPE, DC.MAGT_TYPE_SEQ);
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        List<WriteDbData4MethodArgGenericsType> list = dbOperator.queryList(sql, WriteDbData4MethodArgGenericsType.class, methodHash);
        if (JavaCG2Util.isCollectionEmpty(list)) {
            return null;
        }

        MethodArgGenericsTypeInfo methodArgGenericsTypeInfo = new MethodArgGenericsTypeInfo();
        Map<Integer, GenericsTypeValue> genericsTypeMap = new HashMap<>();
        for (WriteDbData4MethodArgGenericsType writeDbData4MethodArgGenericsType : list) {
            GenericsTypeValue methodArgGenericsTypeValue = genericsTypeMap.computeIfAbsent(writeDbData4MethodArgGenericsType.getArgSeq(),
                    k -> new GenericsTypeValue());
            if (JavaCG2Constants.FILE_KEY_CLASS_TYPE.equals(writeDbData4MethodArgGenericsType.getType())) {
                methodArgGenericsTypeValue.setType(writeDbData4MethodArgGenericsType.getGenericsType());
            } else {
                methodArgGenericsTypeValue.addGenericsType(writeDbData4MethodArgGenericsType.getGenericsType());
            }
        }

        for (Map.Entry<Integer, GenericsTypeValue> entry : genericsTypeMap.entrySet()) {
            methodArgGenericsTypeInfo.putTypeValue(entry.getKey(), entry.getValue());
        }
        return methodArgGenericsTypeInfo;
    }

    /**
     * 根据方法HASH+长度查询对应的方法返回泛型类型
     *
     * @param methodHash
     * @return
     */
    public GenericsTypeValue queryReturnGenericsTypeInfo(String methodHash) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MRGT_QUERY;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.joinColumns(DC.MRGT_TYPE, DC.MRGT_GENERICS_TYPE) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_RETURN_GENERICS_TYPE.getTableName() +
                    " where " + DC.MRGT_METHOD_HASH + " = ?" +
                    " order by " + JACGSqlUtil.joinColumns(DC.MRGT_TYPE, DC.MRGT_TYPE_SEQ);
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        List<WriteDbData4MethodReturnGenericsType> list = dbOperator.queryList(sql, WriteDbData4MethodReturnGenericsType.class, methodHash);
        if (JavaCG2Util.isCollectionEmpty(list)) {
            return null;
        }

        GenericsTypeValue methodReturnGenericsTypeInfo = new GenericsTypeValue();
        for (WriteDbData4MethodReturnGenericsType writeDbData4MethodReturnGenericsType : list) {
            if (JavaCG2Constants.FILE_KEY_CLASS_TYPE.equals(writeDbData4MethodReturnGenericsType.getType())) {
                methodReturnGenericsTypeInfo.setType(writeDbData4MethodReturnGenericsType.getGenericsType());
            } else {
                methodReturnGenericsTypeInfo.addGenericsType(writeDbData4MethodReturnGenericsType.getGenericsType());
            }
        }
        return methodReturnGenericsTypeInfo;
    }

    /**
     * 查询参数中有使用指定类型的完整方法
     *
     * @param argType 指定的参数类型
     * @return
     */
    public Set<String> findMethodByArgType(String argType) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MARG_QUERY_BY_ARG_TYPE;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select distinct(" + DC.MARG_FULL_METHOD + ")" +
                    " from " + DbTableInfoEnum.DTIE_METHOD_ARGUMENT.getTableName() +
                    " where " + DC.MARG_SIMPLE_ARG_TYPE + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        String simpleArgType = dbOperWrapper.querySimpleClassName(argType);
        List<String> list = dbOperator.queryListOneColumn(sql, String.class, simpleArgType);
        if (JavaCG2Util.isCollectionEmpty(list)) {
            return Collections.emptySet();
        }
        return new HashSet<>(list);
    }

    /**
     * 查询参数泛型中有使用指定类型的完整方法
     *
     * @param argType 指定的参数类型
     * @return
     */
    public Set<String> findMethodByArgGenericsType(String argType) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MAGT_QUERY_BY_ARG_TYPE;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select distinct(" + DC.MAGT_FULL_METHOD + ")" +
                    " from " + DbTableInfoEnum.DTIE_METHOD_ARG_GENERICS_TYPE.getTableName() +
                    " where " + DC.MAGT_SIMPLE_GENERICS_TYPE + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        String simpleArgType = dbOperWrapper.querySimpleClassName(argType);
        List<String> list = dbOperator.queryListOneColumn(sql, String.class, simpleArgType);
        if (JavaCG2Util.isCollectionEmpty(list)) {
            return Collections.emptySet();
        }
        return new HashSet<>(list);
    }

    /**
     * 查询返回指定类型的完整方法
     *
     * @param returnType 指定的返回类型
     * @return
     */
    public Set<String> findMethodByReturnType(String returnType) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MI_QUERY_FULL_METHOD_BY_RETURN_TYPE;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select distinct(" + DC.MI_FULL_METHOD + ")" +
                    " from " + DbTableInfoEnum.DTIE_METHOD_INFO.getTableName() +
                    " where " + DC.MI_SIMPLE_RETURN_TYPE + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        String simpleArgType = dbOperWrapper.querySimpleClassName(returnType);
        List<String> list = dbOperator.queryListOneColumn(sql, String.class, simpleArgType);
        if (JavaCG2Util.isCollectionEmpty(list)) {
            return Collections.emptySet();
        }
        return new HashSet<>(list);
    }

    /**
     * 查询返回的泛型包含指定类型的完整方法
     *
     * @param returnType 指定的返回类型
     * @return
     */
    public Set<String> findMethodByReturnGenericsType(String returnType) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MRGT_QUERY_BY_RETURN_TYPE;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select distinct(" + DC.MRGT_FULL_METHOD + ")" +
                    " from " + DbTableInfoEnum.DTIE_METHOD_RETURN_GENERICS_TYPE.getTableName() +
                    " where " + DC.MRGT_SIMPLE_GENERICS_TYPE + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }

        String simpleArgType = dbOperWrapper.querySimpleClassName(returnType);
        List<String> list = dbOperator.queryListOneColumn(sql, String.class, simpleArgType);
        if (JavaCG2Util.isCollectionEmpty(list)) {
            return Collections.emptySet();
        }
        return new HashSet<>(list);
    }

    /**
     * 查询方法参数、方法参数泛型、方法返回类型、方法返回泛型类型中包含指定dto类型的完整方法
     *
     * @param dtoType 指定的dto类型
     * @return
     */
    public Set<String> findMethodByAllType(String dtoType) {
        Set<String> fullMethodSet = new HashSet<>();
        // 查询参数中有使用指定类型的完整方法
        fullMethodSet.addAll(findMethodByArgType(dtoType));
        // 查询参数泛型中有使用指定类型的完整方法
        fullMethodSet.addAll(findMethodByArgGenericsType(dtoType));
        // 查询返回指定类型的完整方法
        fullMethodSet.addAll(findMethodByReturnType(dtoType));
        // 查询返回的泛型包含指定类型的完整方法
        fullMethodSet.addAll(findMethodByReturnGenericsType(dtoType));
        return fullMethodSet;
    }

    /**
     * 查询指定方法指定序号的参数类型
     *
     * @param fullMethod
     * @param argSeq
     * @return 可能返回null或""
     */
    public String queryMethodArgName(String fullMethod, int argSeq) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MARG_QUERY_NAME;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + DC.MARG_ARG_NAME +
                    " from " + DbTableInfoEnum.DTIE_METHOD_ARGUMENT.getTableName() +
                    " where " + DC.MARG_METHOD_HASH + " = ?" +
                    " and " + DC.MARG_ARG_SEQ + " = ?";
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        String methodHash = JACGUtil.genHashWithLen(fullMethod);
        return dbOperator.queryObjectOneColumn(sql, String.class, methodHash, argSeq);
    }

    /**
     * 查询指定方法的返回值对应的方法调用序号
     *
     * @param fullMethod
     * @return
     */
    public List<WriteDbData4MethodReturnCallId> queryMethodReturnCallId(String fullMethod) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MRCI_QUERY;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.getTableAllColumns(DbTableInfoEnum.DTIE_METHOD_RETURN_CALL_ID) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_RETURN_CALL_ID.getTableName() +
                    " where " + DC.MRCI_CALLER_METHOD_HASH + " = ?" +
                    " order by " + DC.MRCI_RETURN_CALL_ID;
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        String methodHash = JACGUtil.genHashWithLen(fullMethod);
        return dbOperator.queryList(sql, WriteDbData4MethodReturnCallId.class, methodHash);
    }

    /**
     * 查询指定方法的返回值对应的方法参数序号
     *
     * @param fullMethod
     * @return
     */
    public List<WriteDbData4MethodReturnArgSeq> queryMethodReturnArgSeq(String fullMethod) {
        SqlKeyEnum sqlKeyEnum = SqlKeyEnum.MRAS_QUERY;
        String sql = dbOperWrapper.getCachedSql(sqlKeyEnum);
        if (sql == null) {
            sql = "select " + JACGSqlUtil.getTableAllColumns(DbTableInfoEnum.DTIE_METHOD_RETURN_ARG_SEQ) +
                    " from " + DbTableInfoEnum.DTIE_METHOD_RETURN_ARG_SEQ.getTableName() +
                    " where " + DC.MRAS_CALLER_METHOD_HASH + " = ?" +
                    " order by " + DC.MRAS_RETURN_ARG_SEQ;
            sql = dbOperWrapper.cacheSql(sqlKeyEnum, sql);
        }
        String methodHash = JACGUtil.genHashWithLen(fullMethod);
        return dbOperator.queryList(sql, WriteDbData4MethodReturnArgSeq.class, methodHash);
    }
}
