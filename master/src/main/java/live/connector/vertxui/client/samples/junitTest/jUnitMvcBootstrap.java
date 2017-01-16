package live.connector.vertxui.client.samples.junitTest;

import java.util.List;

import com.google.gwt.core.shared.GwtIncompatible;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.fluent.VirtualDomSearch;
import live.connector.vertxui.client.samples.mvcBootstrap.View;

/**
 * Run this class inside your favourite IDE as junit test.
 * 
 * This file belongs in your src/test folder. However, for demonstration
 * purposes, it's put here. That's why there are no imports that GWt doesn't
 * like, and all the classpaths are inline (like "@org.junit.Test").
 * 
 * If you want to test VertxUI with a real DOM (should not be necessary if there
 * are no bugs in Fluent HTML), you can write Fluent HTML and perform your tests
 * right inside the browser and not in your IDE (with Fluent.DOM() you have
 * access to the DOM object).
 * 
 * If you want to call the whole from outside (integration tests), just call
 * 'Vertxui.with(yourClass,null)' to compile to javascript, and then fire up
 * Selenium (or JBrowserDriver, HTMLUnit,Rhino+env-js or whatever you want) to
 * take a browser-look at the /war/index.html . You will be writing plain
 * selenium tests which interact with the browser to interact with the DOM. It
 * works but it is the slowest option.
 * 
 * @author ng
 *
 */
@GwtIncompatible
public class jUnitMvcBootstrap {

	@org.junit.Test
	public void titleById() {

		// There is zero extra code for junit inside Fluent, so you have to
		// clean the DOM manually between testcases. For the first method in the
		// class you can leave it out.
		Fluent.clearDOM();

		new View().onModuleLoad();
		// note: you can leave .onModuleLoad() out, it's nicer when it's empty.

		// Check the title (using 'id')
		List<Fluent> a = VirtualDomSearch.getElementsById("titlerForJunitTest", Fluent.body);
		org.junit.Assert.assertEquals(a.size(), 1);
		org.junit.Assert.assertTrue(a.get(0).tag().equals("H1"));
	}

	@org.junit.Test
	public void stateChange() {

		// There is zero extra code for junit inside Fluent, so you have to
		// clean the DOM manually between testcases. For the first method in the
		// class you can leave it out.
		Fluent.clearDOM();

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

}
