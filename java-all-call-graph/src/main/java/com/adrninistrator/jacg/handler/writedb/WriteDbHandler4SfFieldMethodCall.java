package com.adrninistrator.jacg.handler.writedb;

import com.adrninistrator.jacg.common.annotations.JACGWriteDbHandler;
import com.adrninistrator.jacg.common.enums.DbTableInfoEnum;
import com.adrninistrator.jacg.dto.writedb.WriteDbData4SfFieldMethodCall;
import com.adrninistrator.jacg.dto.writedb.WriteDbResult;
import com.adrninistrator.javacg.common.enums.JavaCGOutPutFileTypeEnum;

/**
 * @author adrninistrator
 * @date 2023/8/4
 * @description: 写入数据库，static、final字段初始化方法信息
 */
@JACGWriteDbHandler(
        readFile = true,
        mainFile = true,
        mainFileTypeEnum = JavaCGOutPutFileTypeEnum.OPFTE_SF_FIELD_METHOD_CALL,
        minColumnNum = 7,
        maxColumnNum = 7,
        dbTableInfoEnum = DbTableInfoEnum.DTIE_SF_FIELD_METHOD_CALL
)
public class WriteDbHandler4SfFieldMethodCall extends AbstractWriteDbHandler<WriteDbData4SfFieldMethodCall> {

    public WriteDbHandler4SfFieldMethodCall(WriteDbResult writeDbResult) {
        super(writeDbResult);
    }

    @Override
    protected WriteDbData4SfFieldMethodCall genData(String[] array) {
        String className = array[0];
        // 根据完整类名判断是否需要处理
        if (!isAllowedClassPrefix(className)) {
            return null;
        }

        String fieldName = array[1];
        int seq = Integer.parseInt(array[2]);
        int callId = Integer.parseInt(array[3]);
        String fieldType = array[4];
        String calleeClassName = array[5];
        String calleeMethodName = array[6];
        String simpleClassName = dbOperWrapper.getSimpleClassName(className);
        WriteDbData4SfFieldMethodCall writeDbData4SfFieldMethodCall = new WriteDbData4SfFieldMethodCall();
        writeDbData4SfFieldMethodCall.setRecordId(genNextRecordId());
        writeDbData4SfFieldMethodCall.setSimpleClassName(simpleClassName);
        writeDbData4SfFieldMethodCall.setFieldName(fieldName);
        writeDbData4SfFieldMethodCall.setSeq(seq);
        writeDbData4SfFieldMethodCall.setCallId(callId);
        writeDbData4SfFieldMethodCall.setFieldType(fieldType);
        writeDbData4SfFieldMethodCall.setClassName(className);
        writeDbData4SfFieldMethodCall.setCalleeClassName(calleeClassName);
        writeDbData4SfFieldMethodCall.setCalleeMethodName(calleeMethodName);
        return writeDbData4SfFieldMethodCall;
    }

    @Override
    protected Object[] genObjectArray(WriteDbData4SfFieldMethodCall data) {
        return new Object[]{
                data.getRecordId(),
                data.getSimpleClassName(),
                data.getFieldName(),
                data.getSeq(),
                data.getCallId(),
                data.getFieldType(),
                data.getClassName(),
                data.getCalleeClassName(),
                data.getCalleeMethodName()
        };
    }

    @Override
    public String[] chooseFileColumnDesc() {
        return new String[]{
                "字段所在的完整类名",
                "字段名称",
                "序号，从0开始，大于0代表有多种可能",
                "字段初始化对应的方法调用序号，从1开始",
                "字段类型",
                "初始化方法被调类名",
                "初始化方法被调用方法名"
        };
    }

    @Override
    public String[] chooseFileDetailInfo() {
        return new String[]{
                "static、final字段在初始化时使用方法调用的返回值，保存这些字段及初始化方法的信息",
                "例如： public static final ClassA = new ClassA(\"test1\", \"test2\");",
                "也支持处理枚举中的字段"
        };
    }

}
