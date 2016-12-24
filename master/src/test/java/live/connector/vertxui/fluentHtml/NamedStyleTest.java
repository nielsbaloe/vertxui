package live.connector.vertxui.fluentHtml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NamedStyleTest {

	@Test
	public void style() {
		NameStyle a = NameStyle.transitionTimingFunction;
		String expect = "transition-timing-function";
		assertEquals(NameStyle.valueOfValid(expect), a);
		assertEquals(a.nameValid(), expect);

		NameStyle a2 = NameStyle.alignSelf;
		String expect2 = "align-self";
		assertEquals(NameStyle.valueOfValid(expect2), a2);
		assertEquals(a2.nameValid(), expect2);

		NameStyle a3 = NameStyle.opacity;
		String expect3 = "opacity";
		assertEquals(NameStyle.valueOfValid(expect3), a3);
		assertEquals(a3.nameValid(), expect3);
	}

	@Test
	public void attr() {
		NameAttr a1 = NameAttr.accept_charset;
		String expect1 = "accept-charset";
		assertEquals(a1.nameValid(), expect1);
		assertEquals(a1, NameAttr.valueOfValid(expect1));
	}

}
