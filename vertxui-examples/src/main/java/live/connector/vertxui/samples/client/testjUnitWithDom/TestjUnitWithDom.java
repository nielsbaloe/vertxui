package live.connector.vertxui.samples.client.testjUnitWithDom;

import static live.connector.vertxui.client.fluent.Fluent.document;

import static live.connector.vertxui.client.test.Asserty.assertEquals;
import static live.connector.vertxui.client.test.Asserty.assertTrue;

import elemental.dom.Element;
import elemental.dom.NodeList;
import live.connector.vertxui.client.test.TestDOM;
import live.connector.vertxui.samples.client.mvcBootstrap.View;

/**
 * Run this or the suite in your IDE with junit.
 * 
 * This type of testing involves compiling to javascript, and then running the
 * tests inside a headless browser. You must use Asserty instead of Assert. And
 * manually calling all your tests in the javascript side in tests(), so it is a
 * bit clumbersome, but it works great and allows continuous integration. Note
 * that in most cases you do not need to have the DOM itsself, so the withoutDom
 * method is absolutely preferred above this slower one.
 *
 * Do not create a constructor. It will be used in junit and in the browser.
 * 
 * @author ng
 *
 */

public class TestjUnitWithDom extends TestDOM {

	@Override
	public void tests() {
		mvcBootstrapStateChange();
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

}