package live.connector.vertxui.samples.client.testjUnitWithDom;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.document;
import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import elemental.dom.Element;
import live.connector.vertxui.client.test.TestDOM;

public class AnotherTest extends TestDOM {

	@Override
	public void tests() {

		String id = "id" + Math.random();
		String inner = "bladiebla" + Math.random();
		body.div().inner(inner).id(id);

		Element found = document.getElementById(id);
		assertTrue("should exist", found != null);
		assertEquals("inner text", found.getInnerHTML(), inner);
	}

}