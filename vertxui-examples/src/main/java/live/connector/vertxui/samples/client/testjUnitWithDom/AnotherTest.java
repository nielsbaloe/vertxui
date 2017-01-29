package live.connector.vertxui.samples.client.testjUnitWithDom;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.document;
import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import elemental.dom.Node;
import elemental.dom.NodeList;
import live.connector.vertxui.client.test.TestDOM;

public class AnotherTest extends TestDOM {

	@Override
	public void tests() {

		String id = "id" + Math.random();
		String inner = "bladiebla" + Math.random();
		body.div().in(inner).id(id).classs("bladiebla");
		printStructure((Element) body.dom());

		Element found = document.getElementById(id);
		assertTrue("should exist", found != null);
		assertEquals("inner text", found.getInnerHTML(), inner);
	}

	private void printStructure(Element element) {
		console.log("<" + element.getNodeName());
		NamedNodeMap attributes = element.getAttributes();
		if (attributes != null) {
			for (int x = 0; x < attributes.length(); x++) {
				Node attr = attributes.item(x);
				console.log(" " + attr.getNodeName() + "=" + attr.getNodeValue());
			}
		}
		console.log(">");
		if (element.getInnerHTML() != null) {
			console.log(element.getInnerHTML());
		}
		NodeList children = element.getChildNodes();
		for (int x = 0; x < children.getLength(); x++) {
			printStructure((Element) children.at(x));
		}
		console.log("</" + element.getNodeName() + ">");
	}

}