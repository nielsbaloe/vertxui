package live.connector.vertxui.client;

import live.connector.vertxui.client.fluent.Fluent;
import live.connector.vertxui.client.test.TestDOM;

import static live.connector.vertxui.client.test.Asserty.*;

public class FluentInnerRendering extends TestDOM {

	@Override
	public void tests() {
		inner();

		// TODO finish for all types, and also built a check whether the new
		// value is different, otherwise don't take the new value.
	}

	private void inner() {
		String starttext = Math.random() + "aSeed";
		Fluent div = Fluent.body.div();

		assertEquals("0. real DOM is empty string before start", "", div.dom().getInnerHTML());

		div.inner(starttext);

		assertEquals("1. given value match virtual DOM", starttext, div.inner());
		assertEquals("1. given value match real DOM", starttext, div.dom().getInnerHTML());

		div.inner(starttext); // manualtest: should skip

		div.inner(null);
		assertEquals("2. given value match virtual DOM", null, div.inner());
		assertEquals("2. given value match real DOM not null but empty string", "", div.dom().getInnerHTML());

		div.inner(null); // manual test: should skip
	}

}
