package live.connector.vertxui.client;

import static live.connector.vertxui.client.fluent.Fluent.*;
import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.core.shared.GwtIncompatible;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import elemental.events.Event;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Css;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.test.TestDOM;

public class FluentRenderer extends TestDOM {

	@GwtIncompatible
	@Test
	public void aTest() throws Exception {
		runJS(1);
		runJS(2);
	}

	public Map<Integer, Runnable> registerJS() {
		Map<Integer, Runnable> result = new HashMap<>();
		result.put(1, () -> attributes());
		result.put(2, () -> {
			styles();
			listeners();
		});
		return result;
	}

	private void attributes() {
		ViewOn<Integer> view = body.add(0, s -> {
			Fluent result = Fluent.Div();
			switch (s) {
			case 0:
				result.att(Att.id, "id");
				break;
			case 1:
				result.att(Att.id, "id");
				result.att(Att.accept, "accept");
				break;
			case 2:
				result.att(Att.alt, "alt");
				break;
			case 3:
				break;
			case 4:
				result.att(Att.id, "id");
				result.att(Att.content, "content");
				result.att(Att.accept, "accept");
				break;
			}
			return result;
		});

		// Should add: id
		List<String> attributeNames = getAllNamesFromAttributes(view);
		assertEquals("length should be 1", attributeNames.size(), 1);
		assertTrue("first should be id but is ", attributeNames.get(0).equals("id"));

		// Should add accept
		view.state(1);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("2", attributeNames.size(), 2);
		assertEquals("2.1", attributeNames.get(0), "accept");
		assertEquals("2.2", attributeNames.get(1), "id");

		// Should remove accept
		view.state(0);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("3", attributeNames.size(), 1);
		assertEquals("3.1", attributeNames.get(0), "id");

		// Should add accept
		view.state(1);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("4", attributeNames.size(), 2);
		assertEquals("4.1", attributeNames.get(0), "accept");
		assertEquals("4.2", attributeNames.get(1), "id");

		// Should remove id accept, and add alt
		view.state(2);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("5", attributeNames.size(), 1);
		assertEquals("5.1", attributeNames.get(0), "alt");

		// Should add id and accept, remove alt
		view.state(1);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("6", attributeNames.size(), 2);
		assertEquals("6.1", attributeNames.get(0), "accept");
		assertEquals("6.2", attributeNames.get(1), "id");

		// Should remove id and accept
		view.state(3);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("6.3", attributeNames.size(), 0);

		// Should add id accept
		view.state(1);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("7", attributeNames.size(), 2);
		assertEquals("7.1", attributeNames.get(0), "accept");
		assertEquals("7.2", attributeNames.get(1), "id");

		// Should add checked
		view.state(4);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("8", attributeNames.size(), 3);
		assertEquals("8.1", attributeNames.get(0), "accept");
		assertEquals("8.2", attributeNames.get(1), "content");
		assertEquals("8.3", attributeNames.get(2), "id");

		// Should remove checked
		view.state(1);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("9", attributeNames.size(), 2);
		assertEquals("9.1", attributeNames.get(0), "accept");
		assertEquals("9.2", attributeNames.get(1), "id");
	}

	private List<String> getAllNamesFromAttributes(ViewOn<Integer> view) {
		NamedNodeMap attributes = view.getView().dom().getAttributes();
		List<String> attributeNames = new ArrayList<>();
		for (int x = 0; x < attributes.length(); x++) {
			attributeNames.add(attributes.item(x).getNodeName());
		}
		Collections.sort(attributeNames);
		return attributeNames;
	}

	private void styles() {
		// tricky to test because the browser does whatever it wants. First of
		// all, only valid values get through, the rest is ignored. And
		// sometimes things are added extra (like when setting the background).

		ViewOn<Integer> view = body.add(0, s -> {
			Fluent result = Fluent.Div();
			switch (s) {
			case 0:
				result.css(Css.color, "blue");
				break;
			case 1:
				result.css(Css.color, "blue");
				result.css(Css.fontSize, "50px");
				break;
			case 2:
				result.css(Css.marginLeft, "0");
				break;
			case 3:
				break;
			case 4:
				result.css(Css.color, "blue");
				result.css(Css.fontSize, "50px");
				result.css(Css.textAlign, "left");
				break;
			}
			return result;
		});

		assertTrue("is this the real dom or just fantasy?", body.dom() != null);

		List<String> styleNames = getAllNamesFromStyles(view);
		assertEquals("length should be 1", styleNames.size(), 1);
		assertTrue("1", styleNames.get(0).equals("color"));

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("2", styleNames.size(), 2);
		assertEquals("2.1", styleNames.get(0), "color");
		assertEquals("2.2", styleNames.get(1), Css.fontSize.nameValid());

		view.state(0);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("length should be 1", styleNames.size(), 1);
		assertTrue("and the first item color", styleNames.get(0).equals("color"));

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("3", styleNames.size(), 2);
		assertEquals("3.1", styleNames.get(0), "color");
		assertEquals("3.2", styleNames.get(1), Css.fontSize.nameValid());

		view.state(2);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("4", styleNames.size(), 1);
		assertEquals("4.1", styleNames.get(0), "margin-left");

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("5", styleNames.size(), 2);
		assertEquals("5.1", styleNames.get(0), "color");
		assertEquals("5.2", styleNames.get(1), Css.fontSize.nameValid());

		view.state(3);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("6", styleNames.size(), 0);

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("7", styleNames.size(), 2);
		assertEquals("7.1", styleNames.get(0), "color");
		assertEquals("7.2", styleNames.get(1), Css.fontSize.nameValid());

		view.state(4);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("8", styleNames.size(), 3);
		assertEquals("8.1", styleNames.get(0), "color");
		assertEquals("8.2", styleNames.get(1), "font-size");
		assertEquals("8.3", styleNames.get(2), "text-align");

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("9", styleNames.size(), 2);
		assertEquals("9.1", styleNames.get(0), "color");
		assertEquals("9.2", styleNames.get(1), "font-size");
	}

	private List<String> getAllNamesFromStyles(ViewOn<Integer> view) {
		CSSStyleDeclaration styles = ((Element) view.getView().dom()).getStyle();
		List<String> result = new ArrayList<>();
		for (int x = 0; x < styles.getLength(); x++) {
			result.add(styles.item(x));
		}
		Collections.sort(result);
		return result;
	}

	private void listeners() {
		// unable to test, this is a manual test
		ViewOn<Integer> view = body.add(0, s -> {
			Fluent result = Fluent.Div();
			switch (s) {
			case 0:
				result.click(this::a);
				break;
			case 1:
				result.click(this::b);
				break;
			case 2:
				result.click(this::b);
				result.keypress(this::c);
				break;
			case 3:
				break;
			case 4:
				result.click(this::a);
				result.keypress(this::c);
				result.dblclick(this::b);
				break;
			}
			return result;
		});

		// console.log("should replace click");
		view.state(1);
		// console.log("2. should add focus and keep click");
		view.state(2);
		// console.log("3. should remove click and focus");
		view.state(3);
		// console.log("should add click focus and dblclick");
		view.state(4);
		// console.log("should remove focus and dblclick");
		view.state(0);
		// console.log("should add focus and dblclick");
		view.state(4);
	}

	public void a(Fluent __, Event e) {
	}

	public void b(Fluent __, Event e) {
	}

	public void c(Fluent __, Event e) {
	}

}