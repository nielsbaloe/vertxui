package live.connector.vertxui.samples.client.testjUnitWithDom;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.console;
import static live.connector.vertxui.client.fluent.Fluent.document;
import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.core.shared.GwtIncompatible;

import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import elemental.dom.Node;
import elemental.dom.NodeList;
import live.connector.vertxui.client.test.TestDOM;

public class AnotherTest extends TestDOM {

	@GwtIncompatible
	@Test
	public void test() throws Exception {
		runJS(3);
	}

	@Override
	public Map<Integer, Runnable> registerJS() {
		Map<Integer, Runnable> result = new HashMap<>();
		result.put(3, () -> {
			String id = "id" + Math.random();
			String inner = "bladiebla" + Math.random();
			body.div().txt(inner).id(id).classs("bladiebla");
			printStructure((Element) body.dom());

			Element found = document.getElementById(id);
			assertTrue("should exist", found != null);
			assertEquals("inner text", found.getTextContent(), inner);
		});
		return result;
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
		if (element.getTextContent() != null) {
			console.log(element.getTextContent());
		}
		NodeList children = element.getChildNodes();
		for (int x = 0; x < children.getLength(); x++) {
			printStructure((Element) children.at(x));
		}
		console.log("</" + element.getNodeName() + ">");
	}

}