package live.connector.vertxui.samples.client.testjUnitWithoutDom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.gwt.core.shared.GwtIncompatible;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.test.VirtualDomSearch;
import live.connector.vertxui.samples.client.mvcBootstrap.View;

/**
 * Run this class in junit in your IDE. Note that this is the preferred way of
 * testing your view, you don't need the DOM is you are writing Fluent HTML.
 * 
 * If you prefer, you can place this class in your .server package too, doesn't
 * matter.
 * 
 * @author ng
 *
 */

@GwtIncompatible
public class TestjUnitWithoutDom {

	@Test
	public void mvcBootStrapTitle() {

		// There is zero extra code for junit inside Fluent, so you have to
		// clean the virtualDOM manually between testcases. For the first method
		// in the class you can leave it out.
		Fluent.clearVirtualDOM();

		new View().onModuleLoad();
		// note: you can leave .onModuleLoad() out, it's nicer when it's empty.

		// Check the title (using 'id')
		Fluent a = VirtualDomSearch.getElementById("titlerForJunitTest", Fluent.body);
		assertTrue(a != null);
		assertTrue(a.tag().equals("H1"));
	}

	@Test
	public void mvcBootstrapStateChange() {

		// There is zero extra code for junit inside Fluent, so you have to
		// clean the virtualDOM manually between testcases. For the first method
		// in the class you can leave it out.
		Fluent.clearVirtualDOM();

		View v = new View();
		v.onModuleLoad();
		// note: you can leave .onModuleLoad() out, it's nicer when it's empty.

		// After pressing the menuBills button, check in the LI with class
		// active, the name of the A-link.
		v.onMenuBills(null);
		List<Fluent> b = VirtualDomSearch.getElementsByClassName("active", Fluent.body);
		assertEquals(b.size(), 1);
		assertTrue(((Fluent) b.get(0).getChildren().get(0)).txt().equals("Bills"));
	}

	@Test
	public void nonBodyTest() {
		Fluent root = Fluent.Div();
		root.h1(null, "blabla");
		root.ul("aClass", Fluent.Li().txt("bladiebla"), Fluent.Li().txt("pooo"));

		assertEquals(1, VirtualDomSearch.getElementsByClassName("aClass", root).size());
		assertEquals(2, VirtualDomSearch.getElementsByTagName("LI", root).size());
	}

}
