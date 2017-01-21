package live.connector.vertxui.samples.client.testjUnitWithDom;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.google.gwt.core.shared.GwtIncompatible;

@GwtIncompatible
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestjUnitWithDom.class, AnotherTest.class })
public class SuiteTest {
}
