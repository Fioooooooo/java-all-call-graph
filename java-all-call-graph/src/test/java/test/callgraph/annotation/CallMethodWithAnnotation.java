package test.callgraph.annotation;

import com.adrninistrator.jacg.conf.enums.ConfigKeyEnum;
import com.adrninistrator.jacg.dboper.DbOperator;

/**
 * @author adrninistrator
 * @date 2022/11/10
 * @description:
 */
public class CallMethodWithAnnotation {

    @TestAnnotation(
            strValue = "aaa\r\n",
            intValue = 111,
            intArrayValue = {1, 2, 3, 4},
            clazz1 = DbOperator.class,
            enum1 = ConfigKeyEnum.CKE_APP_NAME,
            annotation1 = @TestAnnotationInner(valueA = "Cva1", valueB = "Cvb1"))
    private void test1() {
        MethodWithAnnotation methodWithAnnotation = new MethodWithAnnotation();
        methodWithAnnotation.test1();
        methodWithAnnotation.test2();
        methodWithAnnotation.test3();
    }

    public void test2() {
        test1();
    }
}
