package live.connector.vertxui.client;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.test.TestDOM;

import static live.connector.vertxui.client.test.Asserty.*;

public class FluentInnerRendering extends TestDOM {

	@Override
	public void tests() {
		inner();
	}

	private void inner() {
		String starttext = Math.random() + "aSeed";
		Fluent div = Fluent.body.div();

		assertEquals("0. real DOM is empty string before start", "", div.dom().getInnerHTML());

		div.in(starttext);

		assertEquals("1. given value match virtual DOM", starttext, div.in());
		assertEquals("1. given value match real DOM", starttext, div.dom().getInnerHTML());

		div.in(starttext); // manualtest: should skip

		div.in(null);
		assertEquals("2. given value match virtual DOM", null, div.in());
		assertEquals("2. given value match real DOM not null but empty string", "", div.dom().getInnerHTML());

		div.in(null); // manual test: should skip
	}

}
