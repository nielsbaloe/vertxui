package client.test;

import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.core.shared.GwtIncompatible;

import client.app.Controller;
import client.app.View;
import elemental.dom.NodeList;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.test.TestDOM;

public class ViewTestWithDom extends TestDOM {

	private static int testNumber = 1;

	@GwtIncompatible
	@Test
	public void test() throws Exception {
		runJS(testNumber);
	}

	@Override
	public Map<Integer, Runnable> registerJS() {
		Map<Integer, Runnable> result = new HashMap<>();
		result.put(testNumber, () -> divsWithDOM());
		return result;
	}

	private static class ControllerMock extends Controller {
		@Override
		public void doAjax() {
		}
	}

	public void divsWithDOM() {

		// Create a view and controller without ajax action
		View view = new View();

		Controller controller = new ControllerMock();
		view.start(controller);

		// after viewing
		NodeList divs = Fluent.document.getElementsByTagName("DIV");
		assertEquals("there should be one DIV", divs.length(), 1);
		assertTrue("which contains the inner text 'Hi'", divs.item(0).getTextContent().contains("Hi"));

		// after some AJAX
		String random = Math.random() + "";
		view.setResponse(200, random);
		divs = Fluent.document.getElementsByTagName("DIV");
		assertEquals("there should be two DIVs", divs.length(), 2);
		assertEquals("and the 2nd should equal ajax", divs.item(1).getTextContent(), "Server said: " + random);
	}

}
