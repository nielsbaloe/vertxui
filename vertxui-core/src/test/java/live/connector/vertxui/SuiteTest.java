package live.connector.vertxui;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import live.connector.vertxui.client.FluentRenderer;
import live.connector.vertxui.server.NamedStyleTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({ FluentRenderer.class, NamedStyleTest.class })
public class SuiteTest {
}
