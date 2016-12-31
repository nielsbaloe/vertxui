package live.connector.vertxui.client.samples.figwheely;

import com.google.gwt.core.client.EntryPoint;

import live.connector.vertxui.client.fluent.Fluent;

public class Client implements EntryPoint {

	/**
	 * Please, run the server and change some text in the constructor below,
	 * save, and then look at your browser, press the button or see what
	 * happens. Don't forget to edit the /sources/sample.css file, save, and
	 * look at your browser at the same time. Do NOT reload your browser.
	 */

	public static String figLocation = "/figwheely.js";

	public Client() {
		Fluent.style("/sourcez/sample.css?" + System.currentTimeMillis());
		Fluent.script(figLocation);

		Fluent body = Fluent.getBody();
		body.div().id("picture");
		Fluent button = body.button("Look at the Client and css, and change something WITHOUT reloading the browser!");
		button.click(e -> {
			button.inner("Something else");
			body.button("sdfsdf!");
		});
	}

	@Override
	public void onModuleLoad() {
	}
}
