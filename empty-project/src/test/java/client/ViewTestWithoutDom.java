package client;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.gwt.core.shared.GwtIncompatible;

import client.app.Controller;
import client.app.View;
import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.test.VirtualDomSearch;

@GwtIncompatible
public class ViewTestWithoutDom {

	@Test
	public void theDivs() {
		Fluent.clearVirtualDOM();

		// Create a view and controller without ajax action
		View view = new View();
		Controller controller = Mockito.spy(Controller.class);
		Mockito.doNothing().when(controller).doAjax();
		view.start(controller);

		// after viewing
		List<Fluent> divs = VirtualDomSearch.getElementsByTagName("DIV", Fluent.body);
		// there should be one DIV
		assertEquals(divs.size(), 1);
		// which contains the inner text 'Hi'
		assertTrue(divs.get(0).txt().contains("Hi"));

		// after some AJAX
		String random = Math.random() + "";
		view.setResponse(200, random);
		// there should be two DIVs
		divs = VirtualDomSearch.getElementsByTagName("DIV", Fluent.body);
		assertEquals(divs.size(), 2);
		// and the second should equal our ajax call.
		assertEquals(divs.get(1).txt(), "Server said: " + random);
	}

}
