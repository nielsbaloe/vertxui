package live.connector.vertxui.server;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;

public class NamedStyleTest {

	@Test
	public void style() {
		Css a = Css.transitionTimingFunction;
		String expect = "transition-timing-function";
		assertEquals(Css.valueOfValid(expect), a);
		assertEquals(a.nameValid(), expect);

		Css a2 = Css.alignSelf;
		String expect2 = "align-self";
		assertEquals(Css.valueOfValid(expect2), a2);
		assertEquals(a2.nameValid(), expect2);

		Css a3 = Css.opacity;
		String expect3 = "opacity";
		assertEquals(Css.valueOfValid(expect3), a3);
		assertEquals(a3.nameValid(), expect3);
	}

	@Test
	public void attr() {
		Att a1 = Att.accept_charset;
		String expect1 = "accept-charset";
		assertEquals(a1.nameValid(), expect1);
		assertEquals(a1, Att.valueOfValid(expect1));
	}

}
