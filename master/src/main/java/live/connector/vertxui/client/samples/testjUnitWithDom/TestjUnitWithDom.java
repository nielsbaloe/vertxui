package live.connector.vertxui.client.samples.testjUnitWithDom;

import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import org.junit.Test;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GwtIncompatible;

import elemental.dom.Element;
import elemental.dom.NamedNodeMap;
import live.connector.vertxui.client.fluent.Att;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.ViewOn;
import live.connector.vertxui.client.test.Asserty;
import live.connector.vertxui.server.test.TestWithDom;

/**
 * This type of testing involves compiling to javascript, and then run the tests
 * inside a headless browser. You must use Asserty instead of Assert. And
 * manually calling all your tests in the javascript side, so it is a bit
 * clumbersome, but it works. Note that in most cases you do not need to have
 * the DOM itsself, so the withoutDom method is absolutely preferred above this
 * slower one.
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
			testInBrowser();
			// ... other tests here too
		});
	}

	public void testInBrowser() {
		ViewOn<Integer> view = Fluent.body.add(0, s -> {
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
		Element inner = view.getViewForDebugPurposesOnly().dom();
		NamedNodeMap attributes = inner.getAttributes();
		assertEquals("length should be 1", attributes.length(), 1);
		assertTrue("first should be ID", attributes.item(0).getNodeName().equals("ID"));

		// TODO more tests

		// System.out.println("Should add accept");
		// view.state(1);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.accept), "accept");
		//
		// System.out.println("Should remove accept");
		// view.state(0);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.accept), null);
		//
		// System.out.println("Should add accept");
		// view.state(1);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.accept), "accept");
		//
		// System.out.println("Should remove id accept, and add alt -- GAAT
		// FOUT!!!!!!!!!!!!!!!!");
		// view.state(2);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), null);
		// assertEquals(inner.attr(Att.accept), null);
		// assertEquals(inner.attr(Att.alt), "alt");
		//
		// System.out.println("Should add id and accept, remove alt");
		// view.state(1);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.accept), "accept");
		//
		// System.out.println("Should remove id and accept");
		// view.state(3);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), null);
		// assertEquals(inner.attr(Att.accept), null);
		// assertEquals(inner.attr(Att.alt), null);
		//
		// System.out.println("Should add id accept");
		// view.state(1);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.accept), "accept");
		// assertEquals(inner.attr(Att.checked), null);
		//
		// System.out.println("Should add checked");
		// view.state(4);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.checked), "checked");
		// assertEquals(inner.attr(Att.accept), "accept");
		//
		// System.out.println("Should remove checked");
		// view.state(1);
		// inner = view.getViewForDebugPurposesOnly();
		// assertEquals(inner.attr(Att.id), "id");
		// assertEquals(inner.attr(Att.accept), "accept");
		// assertEquals(inner.attr(Att.checked), null);

	}

}