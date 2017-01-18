package live.connector.vertxui.client.samples.testjUnitWithoutDom;

import java.util.List;

import com.google.gwt.core.shared.GwtIncompatible;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.samples.mvcBootstrap.View;
import live.connector.vertxui.server.test.VirtualDomSearch;

/**
 * Run this class in junit in your IDE.
 * 
 * If you prefer, you can place this class in your .server package too, doesn't
 * matter.
 * 
 * @author ng
 *
 */

@GwtIncompatible
public class TestjUnitWithoutDom {

	@org.junit.Test
	public void mvcBootStrapTitle() {

		// There is zero extra code for junit inside Fluent, so you have to
		// clean the virtualDOM manually between testcases. For the first method
		// in the class you can leave it out.
		Fluent.clearVirtualDOM();

		new View().onModuleLoad();
		// note: you can leave .onModuleLoad() out, it's nicer when it's empty.

		// Check the title (using 'id')
		List<Fluent> a = VirtualDomSearch.getElementsById("titlerForJunitTest", Fluent.body);
		org.junit.Assert.assertEquals(a.size(), 1);
		org.junit.Assert.assertTrue(a.get(0).tag().equals("H1"));
	}

	@org.junit.Test
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
		v.menuBills(null);
		List<Fluent> b = VirtualDomSearch.getElementsByClassName("active", Fluent.body);
		org.junit.Assert.assertEquals(b.size(), 1);
		org.junit.Assert.assertTrue(((Fluent) b.get(0).getChildren().get(0)).inner().equals("Bills"));
	}

	@org.junit.Test
	public void nonBodyTest() {
		Fluent root = Fluent.Div();
		root.h1("blabla");
		root.ul("aClass", Fluent.Li().inner("bladiebla"), Fluent.Li().inner("pooo"));

		org.junit.Assert.assertEquals(1, VirtualDomSearch.getElementsByClassName("aClass", root).size());
		org.junit.Assert.assertEquals(2, VirtualDomSearch.getElementsByTagName("LI", root).size());
	}

}
