package live.connector.vertxui.samples.client.testjUnitWithDom;

import static live.connector.vertxui.client.fluent.Fluent.document;
import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gwt.core.shared.GwtIncompatible;

import elemental.dom.Element;
import elemental.dom.NodeList;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.test.TestDOM;
import live.connector.vertxui.samples.client.mvcBootstrap.Controller;
import live.connector.vertxui.samples.client.mvcBootstrap.Store;
import live.connector.vertxui.samples.client.mvcBootstrap.View;

/**
 * Run this or the suite in your IDE with junit.
 * 
 * This type of testing involves compiling to javascript, and then running the
 * tests inside a headless browser.
 * 
 * First of all you must use Asserty instead of Assert. The first method
 * everywhere is a description. This might feel uncomfortable but is necessary
 * because GWT does not point out where in a method the exception was thrown.
 * 
 * Secondly, you can use registerJS() to register a lambda under a number. Use
 * runJS() to run the javascript test itsself.
 * 
 * Note that in most cases you do not need to have the DOM itsself, so the
 * withoutDom method is absolutely preferred above this slower one. Think twice,
 * do you really need the DOM, or can you just talk to the virtual DOM inside
 * Fluent instead? In most cases you can.
 *
 * Do not create a constructor. It will be used in junit and in the browser.
 * 
 * @author Niels Gorisse
 *
 */

public class TestjUnitWithDom extends TestDOM {

	private static int testNumber = 234;

	@GwtIncompatible
	@Test
	public void test() throws Exception {
		runJS(testNumber);
		System.out.println("This runs in Java");
	}

	@Override
	public Map<Integer, Runnable> registerJS() {
		Map<Integer, Runnable> result = new HashMap<>();
		result.put(testNumber, () -> mvcBootstrapStateChange());
		return result;
	}

	public void mvcBootstrapStateChange() {
		Fluent.console.log("This runs in Javascript");

		// This below is like 'new View().onModuleLoad()'
		Store transport = new StoreNone(); // BUT with a different store!
		View view = new View();
		Controller controller = new Controller(transport, view);
		view.start(controller);

		// After pressing the menuBills button, check in the LI with class
		// active, the name of the A-link.
		controller.onMenuBills(null, null);

		NodeList b = document.getElementsByClassName("active");
		assertEquals("length of actives", b.length(), 1);
		assertTrue("selected item title", ((Element) b.item(0).getChildNodes().at(0)).getTextContent().equals("Bills"));
	}

}