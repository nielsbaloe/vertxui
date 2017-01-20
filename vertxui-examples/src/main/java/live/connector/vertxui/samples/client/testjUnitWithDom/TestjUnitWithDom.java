package live.connector.vertxui.samples.client.testjUnitWithDom;

import static live.connector.vertxui.client.fluent.Fluent.body;
import static live.connector.vertxui.client.fluent.Fluent.document;
import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GwtIncompatible;

import elemental.css.CSSStyleDeclaration;
import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import elemental.dom.NodeList;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.Style;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.test.Asserty;
import live.connector.vertxui.samples.client.mvcBootstrap.View;
import live.connector.vertxui.server.test.TestWithDom;

/**
 * This type of testing involves compiling to javascript, and then running the
 * tests inside a headless browser. You must use Asserty instead of Assert. And
 * manually calling all your tests in the javascript side, so it is a bit
 * clumbersome, but it works great and allows continuous integration. Note that
 * in most cases you do not need to have the DOM itsself, so the withoutDom
 * method is absolutely preferred above this slower one.
 *
 * Do not create a constructor. It will be used in junit and in the browser.
 * 
 * @author ng
 *
 */

public class TestjUnitWithDom implements EntryPoint {

	@GwtIncompatible
	@Test
	public void runWithjUnit() throws Exception {
		TestWithDom.runwithJunit(this.getClass());
	}

	@Override
	public void onModuleLoad() {
		Asserty.asserty(() -> {
			mvcBootstrapStateChange();
			fluentAttributeRenderTests();
			fluentStylesTests();
		});
	}

	public void mvcBootstrapStateChange() {
		// You can do clearVirtualDOM(), but the DOM wil not be cleared, so only
		// run clearVirtualDom if you test without the DOM.
		// Fluent.clearVirtualDOM();

		View v = new live.connector.vertxui.samples.client.mvcBootstrap.View(); // create
																				// the
																				// _whole_
																				// view
		v.onModuleLoad();
		// note: you can leave .onModuleLoad() out, it's nicer when it's empty.

		// After pressing the menuBills button, check in the LI with class
		// active, the name of the A-link.
		v.menuBills(null);
		NodeList b = document.getElementsByClassName("active");
		assertEquals("length of actives", b.length(), 1);
		assertTrue("selected item title", ((Element) b.item(0).getChildNodes().at(0)).getInnerHTML().equals("Bills"));
	}

	public void fluentAttributeRenderTests() {
		ViewOn<Integer> view = body.add(0, s -> {
			Fluent result = Fluent.Div();
			switch (s) {
			case 0:
				result.attr(Att.id, "id");
				break;
			case 1:
				result.attr(Att.id, "id");
				result.attr(Att.accept, "accept");
				break;
			case 2:
				result.attr(Att.alt, "alt");
				break;
			case 3:
				break;
			case 4:
				result.attr(Att.id, "id");
				result.attr(Att.checked, "checked");
				result.attr(Att.accept, "accept");
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
		assertEquals("8.2", attributeNames.get(1), "checked");
		assertEquals("8.3", attributeNames.get(2), "id");

		// Should remove checked
		view.state(1);
		attributeNames = getAllNamesFromAttributes(view);
		assertEquals("9", attributeNames.size(), 2);
		assertEquals("9.1", attributeNames.get(0), "accept");
		assertEquals("9.2", attributeNames.get(1), "id");

	}

	private List<String> getAllNamesFromAttributes(ViewOn<Integer> view) {
		NamedNodeMap attributes = view.getViewForDebugPurposesOnly().dom().getAttributes();
		List<String> attributeNames = new ArrayList<>();
		for (int x = 0; x < attributes.length(); x++) {
			attributeNames.add(attributes.item(x).getNodeName());
		}
		Collections.sort(attributeNames);
		return attributeNames;
	}

	public void fluentStylesTests() {
		// tricky to test because the browser does whatever it wants. First of
		// all, only valid values get through, the rest is ignored. And
		// sometimes things are added extra (like when setting the background).

		ViewOn<Integer> view = body.add(0, s -> {
			Fluent result = Fluent.Div();
			switch (s) {
			case 0:
				result.css(Style.color, "blue");
				break;
			case 1:
				result.css(Style.color, "blue");
				result.css(Style.fontSize, "50px");
				break;
			case 2:
				result.css(Style.marginLeft, "0");
				break;
			case 3:
				break;
			case 4:
				result.css(Style.color, "blue");
				result.css(Style.fontSize, "50px");
				result.css(Style.textAlign, "left");
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
		assertEquals("2.2", styleNames.get(1), Style.fontSize.nameValid());

		view.state(0);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("length should be 1", styleNames.size(), 1);
		assertTrue("and the first item color", styleNames.get(0).equals("color"));

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("3", styleNames.size(), 2);
		assertEquals("3.1", styleNames.get(0), "color");
		assertEquals("3.2", styleNames.get(1), Style.fontSize.nameValid());

		view.state(2);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("4", styleNames.size(), 1);
		assertEquals("4.1", styleNames.get(0), "margin-left");

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("5", styleNames.size(), 2);
		assertEquals("5.1", styleNames.get(0), "color");
		assertEquals("5.2", styleNames.get(1), Style.fontSize.nameValid());

		view.state(3);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("6", styleNames.size(), 0);

		view.state(1);
		styleNames = getAllNamesFromStyles(view);
		assertEquals("7", styleNames.size(), 2);
		assertEquals("7.1", styleNames.get(0), "color");
		assertEquals("7.2", styleNames.get(1), Style.fontSize.nameValid());

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
		CSSStyleDeclaration styles = view.getViewForDebugPurposesOnly().dom().getStyle();
		List<String> result = new ArrayList<>();
		for (int x = 0; x < styles.getLength(); x++) {
			result.add(styles.item(x));
		}
		Collections.sort(result);
		return result;
	}

}