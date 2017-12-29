package live.connector.vertxui.client;

import static live.connector.vertxui.client.fluent.Fluent.Li;
import static live.connector.vertxui.client.fluent.Fluent.Ul;
import static live.connector.vertxui.client.test.Asserty.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.core.shared.GwtIncompatible;

import elemental.dom.NodeList;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.test.TestDOM;

public class FluentInnerRendering extends TestDOM {

	@GwtIncompatible
	@Test
	public void test() throws Exception {
		runJS(3);
	}

	@Override
	public Map<Integer, Runnable> registerJS() {
		Map<Integer, Runnable> result = new HashMap<>();
		result.put(3, () -> {
			inner();
			middleChildRemoval();
		});
		return result;
	}

	private void middleChildRemoval() {
		ViewOn<Integer> children = Fluent.body.add(0, i -> {
			switch (i) {
			case 0:
				return Ul(null, Li(null, "a"), Li(null, "b"), Li(null, "c"));
			case 1:
				return Ul(null, Li(null, "a"), Li(null, "b"), Li(null, "c"));
			case 2:
				return Ul(null, Li(null, "a"), Li(null, "c"));
			case 3:
				return Ul(null, Li(null, "a"), Li(null, "b"));
			case 4:
				return Ul(null, Li(null, "c"), Li(null, "b"), Li(null, "a"));
			default:
				return null;
			}
		});
		NodeList nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("1. length", 3, nodes.length());
		assertEquals("1. first value", "a", nodes.item(0).getTextContent());
		assertEquals("1. second value", "b", nodes.item(1).getTextContent());
		assertEquals("1. third value", "c", nodes.item(2).getTextContent());

		children.state(1);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("2. length", 3, nodes.length());
		assertEquals("2. first value", "a", nodes.item(0).getTextContent());
		assertEquals("2. second value", "b", nodes.item(1).getTextContent());
		assertEquals("2. third alue", "c", nodes.item(2).getTextContent());

		children.state(2);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("3. length", 2, nodes.length());
		assertEquals("3. first value", "a", nodes.item(0).getTextContent());
		assertEquals("3. second value", "c", nodes.item(1).getTextContent());

		children.state(3);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("4. length", 2, nodes.length());
		assertEquals("4. first value", "a", nodes.item(0).getTextContent());
		assertEquals("4. second value", "b", nodes.item(1).getTextContent());

		children.state(1);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("5. length", 3, nodes.length());
		assertEquals("5. first value", "a", nodes.item(0).getTextContent());
		assertEquals("5. second value", "b", nodes.item(1).getTextContent());
		assertEquals("5. third alue", "c", nodes.item(2).getTextContent());

		children.state(4);
		nodes = Fluent.document.getElementsByTagName("LI");
		assertEquals("6. length", 3, nodes.length());
		assertEquals("6. first value", "c", nodes.item(0).getTextContent());
		assertEquals("6. second value", "b", nodes.item(1).getTextContent());
		assertEquals("6. third alue", "a", nodes.item(2).getTextContent());

	}

	private void inner() {
		String starttext = Math.random() + "aSeed";
		Fluent div = Fluent.body.div();

		assertEquals("0. real DOM is empty string before start", "", div.dom().getTextContent());

		div.txt(starttext);

		assertEquals("1. given value match virtual DOM", starttext, div.txt());
		assertEquals("1. given value match real DOM", starttext, div.dom().getTextContent());

		div.txt(starttext); // manualtest: should skip

		div.txt(null);
		assertEquals("2. given value match virtual DOM", null, div.txt());
		assertEquals("2. given value match real DOM not null but empty string", "", div.dom().getTextContent());

		div.txt(null); // manual test: should skip
	}

}
