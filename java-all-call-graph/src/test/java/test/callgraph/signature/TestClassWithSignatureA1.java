package test.callgraph.signature;

import test.callgraph.methodargument.TestArgument1;
import test.callgraph.methodargument.TestArgument2;

import java.util.List;

/**
 * @author adrninistrator
 * @date 2022/12/7
 * @description:
 */
public class TestClassWithSignatureA1 extends TestAbstractClassWithSignatureA<TestArgument1, TestArgument2> {
    @Override
    public void test() {

    }

    @Override
    public TestArgument2 test2(TestArgument1 testArgument1) {
        superMethod1(testArgument1);
        return null;
    }

    @Override
    public TestArgument2 test3(List<String> stringList) {
        TestArgument2 testArgument2 = new TestArgument2();
        superMethod2(testArgument2);
        return testArgument2;
    }
}
